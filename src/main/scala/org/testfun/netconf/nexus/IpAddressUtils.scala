package org.testfun.netconf.nexus

import com.twitter.util.NetUtil._

object IpAddressUtils {

  def intToIp(ip: Int) = s"${ip>>24&0xFF}.${ip>>16&0xFF}.${ip>>8&0xFF}.${ip&0xFF}"

  def ipAddressBlockStream(firstIp: Int, blockSize: Int): Stream[Int] = {
    def nextIntIpAddress(ip: Int) = {
      val lastByte = ip & 0xFF
      if (lastByte + blockSize >= 255) (ip & 0xFFFFFF00) + 257 else ip + blockSize
    }

    firstIp #:: ipAddressBlockStream(nextIntIpAddress(firstIp), blockSize)
  }

  def ipAddressBlockStream(firstIp: String, blockSize: Int): Stream[String] = {
//    def nextIntIpAddress(ip: Int) = {
//      val lastByte = ip & 0xFF
//      if (lastByte + blockSize >= 255) (ip & 0xFFFFFF00) + 257 else ip + blockSize
//    }
//
//    def intIpAddressBlockStream(ip: Int): Stream[Int] = ip #:: intIpAddressBlockStream(nextIntIpAddress(ip))
    ipAddressBlockStream(ipToInt(firstIp), blockSize).map(intToIp)
  }

  def nextAvailableIpAddressBlock(firstIp: String, blockSize: Int, usedIpAddresses: Seq[String]) = {
    val usedIntIpAddresses = usedIpAddresses.map(ipToInt)
    def unusedBlock(firstIp: Int) = usedIntIpAddresses.forall(ip => ip < firstIp || ip >= firstIp + blockSize)
    intToIp(ipAddressBlockStream(ipToInt(firstIp), blockSize).filter(unusedBlock).head)
  }

}
