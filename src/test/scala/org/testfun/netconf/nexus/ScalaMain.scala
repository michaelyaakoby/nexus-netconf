package org.testfun.netconf.nexus

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import org.testfun.netconf.nexus.Messages.NetconfCredentials

object ScalaMain extends App {

   import ExecutionContext.Implicits.global

   val server = new NetconfClietImpl()
   val r = Await.result(server.vlans(NetconfCredentials("192.168.2.1", "admin", "net@pp11")), 30 seconds)
   println(r)
 }
