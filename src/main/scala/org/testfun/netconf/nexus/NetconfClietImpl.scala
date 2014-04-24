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

  override def editConfig(credentials: NetconfCredentials, commands: Seq[String]) = {
    Future {
      val netConfSshClient = new NetconfSshClient(credentials)
      netConfSshClient.editConfig(Seq())
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


  def editConfig(commands: Seq[String]) = {
    sendXml(
    <rpc message-id="101" xmlns="urn:ietf:params:xml:ns:netconf:base:1.0">
      <edit-config>
        <target>
          <running/>
        </target>
        <config>
          <cli-config-data>
            {commands.map(x => {<cmd>{x}</cmd>})}
          </cli-config-data>
        </config>
      </edit-config>
    </rpc>
  )
  }

  def configInterface(name: String, address: String, netmaskBits: Int) = {
    val commands = Seq(
        s"interface $name",
        s"ip address $address $netmaskBits"
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