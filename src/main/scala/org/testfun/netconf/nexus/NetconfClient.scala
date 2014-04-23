package org.testfun.netconf.nexus

import Messages._
import scala.concurrent.Future

trait NetconfClient {
  def vlans(credentials: NetconfCredentials): Future[Seq[Vlan]]
}

object Messages {
  case class NetconfCredentials(ip: String, username: String, password: String)
  case class Vlan(id: Int, name: String, state: String, ports: String)
}
