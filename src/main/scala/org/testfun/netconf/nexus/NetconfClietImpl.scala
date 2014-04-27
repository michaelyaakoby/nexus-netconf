package org.testfun.netconf.nexus

import Messages._
import scala.xml._
import ch.ethz.ssh2.Connection
import java.util.Scanner
import java.io.PrintWriter
import scala.concurrent.{ExecutionContext, Future}

class NetconfClietImpl(implicit ec: ExecutionContext) extends NetconfClient {

  override def vlans(credentials: NetconfCredentials): Future[Seq[Vlan]] = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.vlans()
      } finally {
        netConfSshClient.close()
      }
    }
  }

  override def interfaces(credentials: NetconfCredentials): Future[Seq[Interface]] = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.interfaces()
      } finally {
        netConfSshClient.close()
      }
    }
  }

  override def createVlanInterface(credentials: NetconfCredentials, vlanId: Int, address: String, netmaskBits: Int) = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.createVlanInterface(vlanId, address, netmaskBits)
      } finally {
        netConfSshClient.close()
      }
    }
  }

  override def deleteVlanInterface(credentials: NetconfCredentials, vlanId: Int) = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.deleteVlanInterface(vlanId)
      } finally {
        netConfSshClient.close()
      }
    }
  }

  override def allowVlanOnAllInterfaces(credentials: NetconfCredentials, vlanId: Int) = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.allowVlanOnAllInterfaces(vlanId)
      } finally {
        netConfSshClient.close()
      }
    }
  }


  override def configureBgp(credentials: NetconfCredentials, amazonBgpIp: String, svmCidr: String, bgpKey: String, customerAsnId: Int = 64514, amazonAsnId: Int = 7224) = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      try {
        netConfSshClient.open()
        netConfSshClient.configureBgp(customerAsnId, amazonAsnId, amazonBgpIp, svmCidr, bgpKey)
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

      assert(receiveOk())

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
      s"conf t ; interface Eth1-32",
      s" switchport trunk  allowed  vlan  add 10"
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
    handleErrors()
  }

  def createVlanInterface(vlanId: Int, address: String, netmaskBits: Int) = {
    val commands = Seq(
        s"conf t ; interface Vlan$vlanId",
        s"ip address $address/$netmaskBits"
    )
    editConfig(commands)
  }


  def createVlan(id: Int, name: String) = {
    val commands = Seq(
      s"config interface ; feature interface-vlan",
      s"vlan $id",
      s"name $name"
    )

    editConfig(commands)
  }

  def configureBgp(customerAsnId: Int = 64514, amazonAsnId: Int = 7224, amazonBgpIp: String, svmCidr: String, bgpKey: String) = {
    val commands = Seq(
      s"conf t ; router bgp $customerAsnId",
      s"address-family ipv4 unicast",
      s"network $svmCidr",
      s"neighbor $amazonBgpIp remote-as $amazonAsnId",
      s"password 0 $bgpKey",
      s"address-family ipv4 unicast"
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
  def receiveOk() = !(receiveXml() \\ "ok").isEmpty
  def handleErrors() = {
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
    println(x)
    XML.loadString(x)
  }
  
  def sendXml(message: Elem) {
    println(message.toString() + "]]>]]>")
    xmlRequestWriter.println(message.toString() + "]]>]]>")
    xmlRequestWriter.flush()
  }
}