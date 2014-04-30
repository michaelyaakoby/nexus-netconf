package org.testfun.netconf.nexus

import org.scalatest.{Matchers, WordSpecLike}
import IpAddressUtils._

class IpAddressUtilsSpec extends WordSpecLike with Matchers {

  "IP address block stream" should {
    "return 3 valid 3 address blocks" in {
      ipAddressBlockStream("192.168.1.30", 3) take 3 mkString "," should equal("192.168.1.30,192.168.1.33,192.168.1.36")
    }

    "return skip *.*.n.255 and start at *.*.n+1.1" in {
      ipAddressBlockStream("192.168.2.240", 10) take 3 mkString "," should equal("192.168.2.240,192.168.2.250,192.168.3.1")
      ipAddressBlockStream("192.168.2.241", 10) take 3 mkString "," should equal("192.168.2.241,192.168.2.251,192.168.3.1")
      ipAddressBlockStream("192.168.2.245", 10) take 3 mkString "," should equal("192.168.2.245,192.168.3.1,192.168.3.11")
      ipAddressBlockStream("192.168.2.250", 10) take 3 mkString "," should equal("192.168.2.250,192.168.3.1,192.168.3.11")
      ipAddressBlockStream("192.168.2.254", 10) take 3 mkString "," should equal("192.168.2.254,192.168.3.1,192.168.3.11")
    }
  }

  "next available ip address block" should {
    "return skip used blocks: .10-15, .16-21, .22-27" in {
      nextAvailableIpAddressBlock("192.168.2.10", 6, Seq("192.168.2.12", "192.168.2.13", "192.168.2.16", "192.168.2.27", "192.168.2.50")) should equal("192.168.2.28")
    }
  }

}
