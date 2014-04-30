package org.testfun.netconf.nexus

import com.twitter.util.NetUtil._

object IpAddressUtils {

  def intToIp(ip: Int) = s"${ip>>24&0xFF}.${ip>>16&0xFF}.${ip>>8&0xFF}.${ip&0xFF}"

  def ipInCidr(cidr: String, offset: Int) = {
    val cidrBlock = cidrToIpBlock(cidr)
    val ip = cidrBlock._1 + offset

    if (!isIpInBlock(ip, cidrBlock)) throw new IllegalArgumentException(s"Offset $offset is outside of CIDR $cidr")

    intToIp(ip)
  }

  def nextAvailableCidrBlock(cidr: String, usedIpAddresses: Seq[String]) = {
    val (baseAddress, mask) = cidrToIpBlock(cidr)
    val cidrSize = -mask
    def cidrStream(baseAddress: Int): Stream[Int] = baseAddress #:: cidrStream(baseAddress + cidrSize)

    val usedIntIpAddresses = usedIpAddresses.map(ipToInt)
    def unusedBlock(baseAddress: Int) = usedIntIpAddresses.forall(ip => !isIpInBlock(ip, (baseAddress, mask)))

    val cidrBits = cidr.split("/").apply(1)
    intToIp(cidrStream(baseAddress).filter(unusedBlock).head) + "/" + cidrBits
  }

}
