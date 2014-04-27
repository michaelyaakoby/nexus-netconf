

import org.testfun.netconf.nexus.NetconfClietImpl
import org.testfun.netconf.nexus.Messages._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
val server: NetconfClietImpl = new NetconfClietImpl()
val r = Await.result(server.vlans(NetconfCredentials("192.168.2.1", "admin", "net@pp11")), 30 seconds)

