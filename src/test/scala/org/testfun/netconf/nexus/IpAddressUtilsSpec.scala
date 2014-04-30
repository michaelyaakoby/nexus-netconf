package org.testfun.netconf.nexus

import org.scalatest.{Matchers, WordSpecLike}
import IpAddressUtils._

class IpAddressUtilsSpec extends WordSpecLike with Matchers {

  "next available /29 CIDR block" should {

    "return first CIDR if no IP is used and if ip beyond the first CIDR is used" in {
      nextAvailableCidrBlock("192.168.1.0/29", Seq()) should equal("192.168.1.0/29")
      nextAvailableCidrBlock("192.168.1.0/29", Seq("192.168.1.10")) should equal("192.168.1.0/29")
    }

    "return the third CIDR which is the first unsed CIDR" in {
      nextAvailableCidrBlock("192.168.1.0/29", Seq("192.168.1.2", "192.168.1.9")) should equal("192.168.1.16/29")
    }

    "move from 192.168.1.* to 192.168.2.* when the first is exhausted" in {
      nextAvailableCidrBlock("192.168.1.248/29", Seq("192.168.1.250")) should equal("192.168.2.0/29")
      nextAvailableCidrBlock("192.168.1.248/29", Seq("192.168.1.250", "192.168.2.2")) should equal("192.168.2.8/29")
    }
  }

  "get IP in CIDR" should {
    "return an IP within the CIDR" in {
      ipInCidr("192.168.1.8/29", 1) should equal ("192.168.1.9")
      ipInCidr("192.168.1.8/29", 7) should equal ("192.168.1.15")
    }

    "fail if requested IP is outside of CIDR" in {
      evaluating {
        ipInCidr("192.168.2.8/29", 8)
      } should produce [IllegalArgumentException]
    }
  }
}
