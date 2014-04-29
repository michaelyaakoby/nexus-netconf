package org.testfun.netconf.nexus

import Messages._
import scala.concurrent.Future

trait NetconfClient {
  val credentials: NetconfCredentials
  def vlans(): Future[Seq[Vlan]]
  def interfaces(): Future[Seq[Interface]]
  def createVlanInterface(vlanId: Int, address: String, netmaskBits: Int) : Future[Unit]
  def configureBgp(amazonBgpIp: String, svmCidr: String, bgpKey: String, customerAsnId: Int = 64514, amazonAsnId: Int = 7224) : Future[Unit]
  def deleteVlanInterface(vlanId: Int) : Future[Unit]
  def allowVlanOnAllInterfaces(vlanId: Int) : Future[Unit]
  def removeBgpNeighbor(amazonBgpIp: String, customerAsnId: Int = 64514)
}

object Messages {
  case class NetconfCredentials(ip: String, username: String, password: String)
  case class Vlan(id: Int, name: String, state: String, ports: Seq[String] = Seq())
  case class Interface(name: String, state: String, address: String, netmaskBc: Int)
  case class NxosConfigException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
}


