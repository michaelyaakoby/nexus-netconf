package org.testfun.netconf.nexus

import com.twitter.util.NetUtil._

object IpAddressUtils {

  def intToIp(ip: Int) = s"${ip>>24&0xFF}.${ip>>16&0xFF}.${ip>>8&0xFF}.${ip&0xFF}"

  def ipAddressBlockStream(firstIp: String, blockSize: Int): Stream[String] = {
    def nextIntIpAddress(ip: Int) = {
      val lastByte = ip & 0xFF
      if (lastByte + blockSize >= 255) (ip & 0xFFFFFF00) + 257 else ip + blockSize
    }

    def intIpAddressBlockStream(ip: Int): Stream[Int] = ip #:: intIpAddressBlockStream(nextIntIpAddress(ip))
    intIpAddressBlockStream(ipToInt(firstIp)).map(intToIp)
  }

  def getNextAvailableIpAddressBlock(firstIp: String, usedIpAddresses: Seq[String]) = "damn"

}
