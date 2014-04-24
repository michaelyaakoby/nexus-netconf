package org.testfun.netconf.nexus

import Messages._
import scala.concurrent.Future

trait NetconfClient {
  def vlans(credentials: NetconfCredentials): Future[Seq[Vlan]]
  //def interface(credentials: NetconfCredentials): Future[Seq[Interface]]
  def editConfig(credentials: NetconfCredentials, commands: Seq[String]) : Future[Unit]
}

object Messages {
  case class NetconfCredentials(ip: String, username: String, password: String)
  case class Vlan(id: Int, name: String, state: String, ports: Seq[String] = Seq())
  case class Interface(name: String, state: String, address: String, netmaskBc: Int)
}


