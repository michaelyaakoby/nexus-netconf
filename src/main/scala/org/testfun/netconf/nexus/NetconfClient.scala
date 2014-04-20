package org.testfun.netconf.nexus

import Messages._
import scala.concurrent.Future

trait NetconfClient {
  def vlans(credentials: NetConfCredentials): Future[Seq[Vlan]]
}

object Messages {
  case class NetConfCredentials(ip: String, username: String, password: String)
  case class Vlan(id: Int, name: String, state: String, ports: String)
}
