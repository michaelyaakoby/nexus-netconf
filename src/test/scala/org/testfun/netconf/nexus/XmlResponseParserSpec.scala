package org.testfun.netconf.nexus

import org.scalatest.{Matchers, WordSpecLike}

class XmlResponseParserSpec extends WordSpecLike with Matchers {

  val showVlanXml =
    <nc:rpc-reply xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns="http://www.cisco.com/nxos:1.0:nfcli" message-id="1">
      <nc:data>
        <show>
          <vlan>
            <__XML__OPT_Cmd_show_vlan___readonly__>
              <__readonly__>
                <TABLE_vlanbrief>
                  <ROW_vlanbrief>
                    <vlanshowbr-vlanid>1</vlanshowbr-vlanid>
                    <vlanshowbr-vlanname>default</vlanshowbr-vlanname>
                    <vlanshowbr-vlanstate>active</vlanshowbr-vlanstate>
                    <vlanshowbr-shutstate>noshutdown</vlanshowbr-shutstate>
                    <vlanshowplist-ifidx>port-channel127-128,Ethernet1/1-32,Ethernet3/1-30,Ethernet4/4</vlanshowplist-ifidx>
                    <vlanshowplist-ifidx>Ethernet3/31-32</vlanshowplist-ifidx>
                  </ROW_vlanbrief>
                  <ROW_vlanbrief>
                    <vlanshowbr-vlanid>2</vlanshowbr-vlanid>
                    <vlanshowbr-vlanname>VLAN2</vlanshowbr-vlanname>
                    <vlanshowbr-vlanstate>active</vlanshowbr-vlanstate>
                    <vlanshowbr-shutstate>noshutdown</vlanshowbr-shutstate>
                    <vlanshowplist-ifidx>port-channel127-128,Ethernet1/1-32,Ethernet3/1-30</vlanshowplist-ifidx>
                    <vlanshowplist-ifidx>Ethernet3/31-32</vlanshowplist-ifidx>
                  </ROW_vlanbrief>
                  <ROW_vlanbrief>
                    <vlanshowbr-vlanid>3</vlanshowbr-vlanid>
                    <vlanshowbr-vlanname>VLAN#</vlanshowbr-vlanname>
                    <vlanshowbr-vlanstate>active</vlanshowbr-vlanstate>
                    <vlanshowbr-shutstate>noshutdown</vlanshowbr-shutstate>
                    <vlanshowplist-ifidx>port-channel127-128,Ethernet1/1-3,Ethernet3/1-32</vlanshowplist-ifidx>
                  </ROW_vlanbrief>
                </TABLE_vlanbrief>
                <TABLE_mtuinfo>
                  <ROW_mtuinfo>
                    <vlanshowinfo-vlanid>1</vlanshowinfo-vlanid>
                    <vlanshowinfo-media-type>enet</vlanshowinfo-media-type>
                    <vlanshowinfo-vlanmode>ce-vlan</vlanshowinfo-vlanmode>
                  </ROW_mtuinfo>
                  <ROW_mtuinfo>
                    <vlanshowinfo-vlanid>2</vlanshowinfo-vlanid>
                    <vlanshowinfo-media-type>enet</vlanshowinfo-media-type>
                    <vlanshowinfo-vlanmode>ce-vlan</vlanshowinfo-vlanmode>
                  </ROW_mtuinfo>
                  <ROW_mtuinfo>
                    <vlanshowinfo-vlanid>3</vlanshowinfo-vlanid>
                    <vlanshowinfo-media-type>enet</vlanshowinfo-media-type>
                    <vlanshowinfo-vlanmode>ce-vlan</vlanshowinfo-vlanmode>
                  </ROW_mtuinfo>
                </TABLE_mtuinfo>
              </__readonly__>
            </__XML__OPT_Cmd_show_vlan___readonly__>
          </vlan>
        </show>
      </nc:data>
    </nc:rpc-reply>

  "vlan parser" should {
    "return port range" in new XmlResponseParser {
      val ports = toVlans(showVlanXml)(0).ports
      ports should have size 65
      ports should contain ("Ethernet1/1")
      ports should contain ("Ethernet1/32")
    }

    "return individual port" in new XmlResponseParser {
      val ports = toVlans(showVlanXml)(0).ports
      ports should contain ("Ethernet4/4")
    }
  }

}
