package org.testfun.netconf.nexus

import Messages._
import scala.xml._
import ch.ethz.ssh2.Connection
import java.util.Scanner
import java.io.PrintWriter
import scala.concurrent.{ExecutionContext, Future}

class NetconfClietImpl(nxCredentials: NetconfCredentials)(implicit ec: ExecutionContext) extends NetconfClient {

  override val credentials: NetconfCredentials = nxCredentials 

  override def vlans(): Future[Seq[Vlan]] = executeNxAction(_.vlans())

  override def interfaces(): Future[Seq[Interface]] = executeNxAction(_.interfaces())

  override def createVlanInterface(vrfName: String, vlanId: Int, address: String, netmaskBits: Int) = executeNxAction(_.createVlanInterface(vrfName, vlanId, address, netmaskBits))

  override def deleteVlanInterface(vlanId: Int) = executeNxAction(_.deleteVlanInterface(vlanId))

  override def allowVlanOnAllInterfaces(vlanId: Int) = executeNxAction(_.allowVlanOnAllInterfaces(vlanId))

  override def configureBgp(vrfName: String, amazonBgpIp: String, svmCidr: String, bgpKey: String, customerAsnId: Int = 64514, amazonAsnId: Int = 7224) = executeNxAction(_.configureBgp(vrfName, amazonBgpIp, svmCidr, bgpKey, customerAsnId, amazonAsnId))

  override def removeBgpNeighbor(amazonBgpIp: String, customerAsnId: Int = 64514) = executeNxAction(_.removeBgpNeighbor(amazonBgpIp, customerAsnId))

  override def createVlan(id: Int, name: String) = executeNxAction(_.createVlan(id, name))

  override def createVrf(name: String) = executeNxAction(_.createVrf(name))

  def executeNxAction[X](nxFunction: NetconfSshClient => X) = {
    val netConfSshClient = new NetconfSshClient(credentials)
    Future {
      try {
        netConfSshClient.open()
        nxFunction(netConfSshClient)
      } finally {
        netConfSshClient.close()
      }
    }
  }
}

trait XmlResponseParser {
  def extractPortsFromRange(s: String) = {
    val rangeMatcher = "(.*?)([0-9]+)-([0-9]+)".r

    s match {
      case rangeMatcher(prefix, fromPort, toPort) => for (i <- fromPort.toInt to toPort.toInt) yield prefix + i
      case x => Seq(x)
    }
  }

  def toVlans(xmlDoc: Elem): Seq[Vlan] = {
    val vlanXml = xmlDoc \\ "ROW_vlanbrief"
    vlanXml.map(n => Vlan(
      (n \ "vlanshowbr-vlanid").text.toInt,
      (n \ "vlanshowbr-vlanname").text,
      (n \ "vlanshowbr-vlanstate").text,
      (n \ "vlanshowplist-ifidx").map(_.text.split(',')).flatten.filter(!_.startsWith("port-channel")).map(extractPortsFromRange).flatten
    ))
  }

  def toInterfaces(xmlDoc: Elem): Seq[Interface] = {
    val interfaceXml = (xmlDoc \\ "ROW_interface").filter(x => (x \ "eth_ip_addr").text.nonEmpty && (x \ "svi_admin_state").text.nonEmpty && (x \ "interface").text.toLowerCase.startsWith("vlan"))
    interfaceXml.map(n => Interface(
      (n \ "interface").text,
      (n \ "svi_admin_state").text,
      (n \ "eth_ip_addr").text,
      (n \ "eth_ip_mask").text.toInt
    ))
  }
}

class NetconfSshClient(credentials: NetconfCredentials) extends XmlResponseParser {

  val connection = connect(credentials)
  val session = connection.openSession()
  val xmlRequestWriter: PrintWriter = new PrintWriter(session.getStdin)
  val responseScanner = new Scanner(session.getStdout).useDelimiter("]]>]]>")

  def open() {
    initXmlAgent()
    val sessionId = receiveHello()
    sendHello()
    println(s"Connected to ${credentials.ip} - session ID is $sessionId")
  }

  def close() {
    try {
      sendXml(<nc:rpc message-id="101" xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns="http://www.cisco.com/nxos:1.0">
        <nc:close-session/>
      </nc:rpc>)

      receive()

    } finally {
      if (session != null) session.close()
      if (connection != null) connection.close()
    }
  }

  def vlans() = {
    sendXml(
      <nc:rpc message-id="1" xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns="http://www.cisco.com/nxos:1.0:nfcli">
        <nc:get>
          <nc:filter type="subtree">
            <show>
              <vlan/>
            </show>
          </nc:filter>
        </nc:get>
      </nc:rpc>
    )

    toVlans(receiveXml())
  }

  def allowVlanOnAllInterfaces(vlanId: Int) = {
    val commands = Seq(
      s"conf t ; interface Eth1/1-32",
      s" switchport trunk  allowed  vlan  add $vlanId"
    )
    editConfig(commands)
  }


  def editConfig(commands: Seq[String]) = {
    sendXml(
      <nc:rpc xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns:nxos="http://www.cisco.com/nxos:1.0" message-id="1">
        <nxos:exec-command>
          {commands.map(x => {<nxos:cmd>{x}</nxos:cmd>})}
        </nxos:exec-command>
      </nc:rpc>
    )
    receive()
  }

  def createVlanInterface(vrfName: String, vlanId: Int, address: String, netmaskBits: Int) = {
    val commands = Seq(
        s"conf t ; interface Vlan$vlanId",
        s"no shutdown ; vrf member $vrfName",
        s"ip address $address/$netmaskBits"
    )
    editConfig(commands)
  }


  def createVlan(id: Int, name: String) = {
    val commands = Seq(
      s"conf t ; feature interface-vlan",
      s"vlan $id",
      s"name $name"
    )

    editConfig(commands)
  }

  def configureBgp(vrfName: String,amazonBgpIp: String, svmCidr: String, bgpKey: String, customerAsnId: Int, amazonAsnId: Int) = {
    val commands = Seq(
      s"conf t ; router bgp $customerAsnId ;  vrf $vrfName",
      s"address-family ipv4 unicast",
      s"network $svmCidr",
      s"neighbor $amazonBgpIp remote-as $amazonAsnId",
      s"password 0 $bgpKey",
      s"address-family ipv4 unicast"
    )
    editConfig(commands)
  }

  def removeBgpNeighbor(amazonBgpIp: String, customerAsnId: Int = 64514) = {
    val commands = Seq(
      s"conf t ; router bgp $customerAsnId",
      s"no neighbor $amazonBgpIp"
    )
    editConfig(commands)
  }

  def createVrf(name: String) = {
    val commands = Seq(
      s"conf t ; vrf context $name"
    )
    editConfig(commands)
  }

  def deleteVlanInterface(vlanId: Int) = {
    val commands = Seq(
      s"config t",
      s"no interface Vlan$vlanId"
    )
    editConfig(commands)
  }

  def interfaces() = {
    sendXml(
      <nc:rpc message-id="1" xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns="http://www.cisco.com/nxos:1.0:nfcli">
        <nc:get>
          <nc:filter type="subtree">
            <show>
              <interface/>
            </show>
          </nc:filter>
        </nc:get>
      </nc:rpc>
    )

    toInterfaces(receiveXml())
  }

  def connect(credentials: NetconfCredentials) = {
    import credentials._
    val connection = new Connection(ip)
    connection.connect
    assert(connection.authenticateWithPassword(username, password))
    connection
  }

  def initXmlAgent() { session.startSubSystem("xmlagent")}
  
  def receiveHello() = (receiveXml() \\ "session-id").text.toInt
  def receive() = {
    val response = receiveXml()
    if((response \\ "ok").isEmpty){
      throw new  NxosConfigException((response \ "rpc-error" \ "error-message").text)
    }
  }

  def sendHello() {
    sendXml(
      <nc:hello xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0">
        <nc:capabilities>
          <nc:capability>urn:ietf:params:xml:ns:netconf:base:1.0</nc:capability>
        </nc:capabilities>
      </nc:hello>
    )
  }

  def receiveXml() = {
    val x = responseScanner.next()

    XML.loadString(x)
  }
  
  def sendXml(message: Elem) {
    xmlRequestWriter.println(message.toString() + "]]>]]>")
    xmlRequestWriter.flush()
  }
}