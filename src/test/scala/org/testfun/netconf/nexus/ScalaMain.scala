package org.testfun.netconf.nexus

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import org.testfun.netconf.nexus.Messages.NetconfCredentials
import org.testfun.netconf.nexus.IpAddressUtils._
import org.testfun.netconf.nexus.Messages.NetconfCredentials

object ScalaMain extends App {

   import ExecutionContext.Implicits.global
  val bgpKey = "4uj4nwc4SaEJuWewBWOckJBy"
  val customerBgpIp = "169.254.253.26"
  val awsBgpIp = "169.254.253.25"
  val svmCidr = "192.168.24.96/28"
  val svmGw = ipInCidr(svmCidr, 1)
  val nxCreds = NetconfCredentials("192.168.2.1", "admin", "net@pp11")
  val server = new NetconfClietImpl(nxCreds)
//   var r = Await.result(server.vlans(), 30 seconds)
//   Await.result(server.createVrf("VRF4"),30 seconds)
//   Await.result(server.createVlan(4,"VLAN4"), 30 seconds)
//   Await.result(server.allowVlanOnAllInterfaces(4), 30 seconds)
//
   Await.result(server.configureBgp("VRF4",awsBgpIp,svmCidr,bgpKey), 30 seconds)
//   Await.result(server.createVlanInterface("VRF4",4,customerBgpIp,30), 30 seconds)
 // Await.result(server.addSecondaryIpToInterface("VRF4","Vlan4",svmGw,28), 30 seconds)
  //val r = Await.result(server.deleteVlanInterface(nxCreds,33),30 seconds)
   //println(r)
 }

