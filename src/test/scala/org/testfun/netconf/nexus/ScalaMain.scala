package org.testfun.netconf.nexus

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import org.testfun.netconf.nexus.Messages.NetconfCredentials

object ScalaMain extends App {

   import ExecutionContext.Implicits.global
  val nxCreds = NetconfCredentials("192.168.2.1", "admin", "net@pp11")
   val server = new NetconfClietImpl(nxCreds)
   val r = Await.result(server.vlans(), 30 seconds)
  //val r = Await.result(server.deleteVlanInterface(nxCreds,33),30 seconds)
   println(r)
 }

