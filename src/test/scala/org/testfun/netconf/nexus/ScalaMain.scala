package org.testfun.netconf.nexus

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import org.testfun.netconf.nexus.Messages.NetConfCredentials

object ScalaMain extends App {

   import ExecutionContext.Implicits.global

   val server = new NetconfClietImpl()
   val r = Await.result(server.vlans(NetConfCredentials("192.168.1.1", "admin", "net@pp11")), 30 seconds)
   println(r)
 }
