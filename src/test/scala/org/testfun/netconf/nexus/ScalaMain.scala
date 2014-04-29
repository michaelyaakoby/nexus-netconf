package org.testfun.netconf.nexus

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import org.testfun.netconf.nexus.Messages.NetconfCredentials

object ScalaMain extends App {

   import ExecutionContext.Implicits.global
  val bgpKey = "N7ULut2JTGkVnk_DNPRYt1fd"
  val customerBgpIp = "169.254.253.18"
  val awsBgpIp = "169.254.253.17"
  val svmCidr = "192.168.24.80/28"
  val nxCreds = NetconfCredentials("192.168.2.1", "admin", "net@pp11")
  val server = new NetconfClietImpl(nxCreds)
   //val r = Await.result(server.vlans(), 30 seconds)
   //val r = Await.result(server.createVlan(3,"VLAN3"), 30 seconds)
   //val y = Await.result(server.allowVlanOnAllInterfaces(3), 30 seconds)
   //val r = Await.result(server.createVrf("VRF3"), 30 seconds)
    //val r = Await.result(server.createVlanInterface("VRF3",3,"192.168.24.81",28), 30 seconds)
  //val r = Await.result(server.configureBgp("VRF3",awsBgpIp,svmCidr,bgpKey), 30 seconds)
  val r = Await.result(server.createVlanInterface("VRF3",3,customerBgpIp,30), 30 seconds)

  //val r = Await.result(server.deleteVlanInterface(nxCreds,33),30 seconds)
   println(r)
 }

