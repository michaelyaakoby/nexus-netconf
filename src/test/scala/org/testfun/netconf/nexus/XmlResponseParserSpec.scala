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

  val showInterfaceXml =
    <nf:rpc-reply xmlns:nf="urn:ietf:params:xml:ns:netconf:base:1.0" xmlns="http://www.cisco.com/nxos:1.0:if_manager">
      <nf:data>
        <show>
          <interface>
            <__XML__OPT_Cmd_show_interface___readonly__>
              <__readonly__>
                <TABLE_interface>
                  <ROW_interface>
                    <interface>Ethernet1/1</interface>
                    <state>up</state>
                    <share_state>Dedicated</share_state>
                    <eth_hw_desc>1000/10000 Ethernet</eth_hw_desc>
                    <eth_hw_addr>002a.6aa3.cfc8</eth_hw_addr>
                    <eth_bia_addr>002a.6aa3.cfc8</eth_bia_addr>
                    <eth_mtu>1500</eth_mtu>
                    <eth_bw>10000000</eth_bw>
                    <eth_dly>10</eth_dly>
                    <eth_reliability>255</eth_reliability>
                    <eth_txload>1</eth_txload>
                    <eth_rxload>1</eth_rxload>
                    <eth_mode>trunk</eth_mode>
                    <eth_duplex>full</eth_duplex>
                    <eth_speed>10 Gb/s</eth_speed>
                    <eth_media>10G</eth_media>
                    <eth_beacon>off</eth_beacon>
                    <eth_in_flowctrl>off</eth_in_flowctrl>
                    <eth_out_flowctrl>off</eth_out_flowctrl>
                    <eth_ratemode>dedicated</eth_ratemode>
                    <eth_swt_monitor>off</eth_swt_monitor>
                    <eth_ethertype>0x8100</eth_ethertype>
                    <eth_link_flapped>1week(s) 6day(s)</eth_link_flapped>
                    <eth_clear_counters>never</eth_clear_counters>
                    <eth_load_interval1>30</eth_load_interval1>
                    <eth_inrate1_bits>4560</eth_inrate1_bits>
                    <eth_inrate1_pkts>4</eth_inrate1_pkts>
                    <eth_load_interval1>30</eth_load_interval1>
                    <eth_outrate1_bits>5096</eth_outrate1_bits>
                    <eth_outrate1_pkts>3</eth_outrate1_pkts>
                    <eth_inucast>1132140</eth_inucast>
                    <eth_inmcast>0</eth_inmcast>
                    <eth_inbcast>6952</eth_inbcast>
                    <eth_inpkts>1139092</eth_inpkts>
                    <eth_inbytes>86203118</eth_inbytes>
                    <eth_giants>0</eth_giants>
                    <eth_storm_supp>0</eth_storm_supp>
                    <eth_runts>0</eth_runts>
                    <eth_giants>0</eth_giants>
                    <eth_crc>0</eth_crc>
                    <eth_nobuf>0</eth_nobuf>
                    <eth_inerr>0</eth_inerr>
                    <eth_frame>0</eth_frame>
                    <eth_overrun>0</eth_overrun>
                    <eth_underrun>0</eth_underrun>
                    <eth_ignored>0</eth_ignored>
                    <eth_watchdog>0</eth_watchdog>
                    <eth_bad_eth>0</eth_bad_eth>
                    <eth_bad_proto>0</eth_bad_proto>
                    <eth_in_ifdown_drops>0</eth_in_ifdown_drops>
                    <eth_dribble>0</eth_dribble>
                    <eth_indiscard>0</eth_indiscard>
                    <eth_inpause>0</eth_inpause>
                    <eth_outucast>1130102</eth_outucast>
                    <eth_outmcast>1487798</eth_outmcast>
                    <eth_outbcast>149</eth_outbcast>
                    <eth_outpkts>2618049</eth_outpkts>
                    <eth_outbytes>212425016</eth_outbytes>
                    <eth_giants>0</eth_giants>
                    <eth_crc>0</eth_crc>
                    <eth_coll>0</eth_coll>
                    <eth_deferred>0</eth_deferred>
                    <eth_latecoll>0</eth_latecoll>
                    <eth_lostcarrier>0</eth_lostcarrier>
                    <eth_nocarrier>0</eth_nocarrier>
                    <eth_babbles>0</eth_babbles>
                    <eth_out_drops>0</eth_out_drops>
                    <eth_outpause>0</eth_outpause>
                    <eth_reset_cntr>2</eth_reset_cntr>
                  </ROW_interface>
                  <ROW_interface>
                    <interface>Ethernet1/2</interface>
                    <state>up</state>
                    <share_state>Dedicated</share_state>
                    <eth_hw_desc>1000/10000 Ethernet</eth_hw_desc>
                    <eth_hw_addr>002a.6aa3.cfc8</eth_hw_addr>
                    <eth_bia_addr>002a.6aa3.cfc8</eth_bia_addr>
                    <eth_mtu>1500</eth_mtu>
                    <eth_bw>10000000</eth_bw>
                    <eth_dly>10</eth_dly>
                    <eth_reliability>255</eth_reliability>
                    <eth_txload>1</eth_txload>
                    <eth_rxload>1</eth_rxload>
                    <eth_mode>trunk</eth_mode>
                    <eth_duplex>full</eth_duplex>
                    <eth_speed>10 Gb/s</eth_speed>
                    <eth_media>10G</eth_media>
                    <eth_beacon>off</eth_beacon>
                    <eth_in_flowctrl>off</eth_in_flowctrl>
                    <eth_out_flowctrl>off</eth_out_flowctrl>
                    <eth_ratemode>dedicated</eth_ratemode>
                    <eth_swt_monitor>off</eth_swt_monitor>
                    <eth_ethertype>0x8100</eth_ethertype>
                    <eth_link_flapped>1week(s) 6day(s)</eth_link_flapped>
                    <eth_clear_counters>never</eth_clear_counters>
                    <eth_load_interval1>30</eth_load_interval1>
                    <eth_inrate1_bits>4560</eth_inrate1_bits>
                    <eth_inrate1_pkts>4</eth_inrate1_pkts>
                    <eth_load_interval1>30</eth_load_interval1>
                    <eth_outrate1_bits>5096</eth_outrate1_bits>
                    <eth_outrate1_pkts>3</eth_outrate1_pkts>
                    <eth_inucast>1132140</eth_inucast>
                    <eth_inmcast>0</eth_inmcast>
                    <eth_inbcast>6952</eth_inbcast>
                    <eth_inpkts>1139092</eth_inpkts>
                    <eth_inbytes>86203118</eth_inbytes>
                    <eth_giants>0</eth_giants>
                    <eth_storm_supp>0</eth_storm_supp>
                    <eth_runts>0</eth_runts>
                    <eth_giants>0</eth_giants>
                    <eth_crc>0</eth_crc>
                    <eth_nobuf>0</eth_nobuf>
                    <eth_inerr>0</eth_inerr>
                    <eth_frame>0</eth_frame>
                    <eth_overrun>0</eth_overrun>
                    <eth_underrun>0</eth_underrun>
                    <eth_ignored>0</eth_ignored>
                    <eth_watchdog>0</eth_watchdog>
                    <eth_bad_eth>0</eth_bad_eth>
                    <eth_bad_proto>0</eth_bad_proto>
                    <eth_in_ifdown_drops>0</eth_in_ifdown_drops>
                    <eth_dribble>0</eth_dribble>
                    <eth_indiscard>0</eth_indiscard>
                    <eth_inpause>0</eth_inpause>
                    <eth_outucast>1130102</eth_outucast>
                    <eth_outmcast>1487798</eth_outmcast>
                    <eth_outbcast>149</eth_outbcast>
                    <eth_outpkts>2618049</eth_outpkts>
                    <eth_outbytes>212425016</eth_outbytes>
                    <eth_giants>0</eth_giants>
                    <eth_crc>0</eth_crc>
                    <eth_coll>0</eth_coll>
                    <eth_deferred>0</eth_deferred>
                    <eth_latecoll>0</eth_latecoll>
                    <eth_lostcarrier>0</eth_lostcarrier>
                    <eth_nocarrier>0</eth_nocarrier>
                    <eth_babbles>0</eth_babbles>
                    <eth_out_drops>0</eth_out_drops>
                    <eth_outpause>0</eth_outpause>
                    <eth_reset_cntr>2</eth_reset_cntr>
                  </ROW_interface>
                  <ROW_interface>
                    <interface>mgmt0</interface>
                    <state>down</state>
                    <state_rsn_desc>Link not connected</state_rsn_desc>
                    <eth_hw_desc>GigabitEthernet</eth_hw_desc>
                    <eth_hw_addr>002a.6aa3.cfc1</eth_hw_addr>
                    <eth_bia_addr>002a.6aa3.cfc1</eth_bia_addr>
                    <eth_ip_addr>192.168.1.3</eth_ip_addr>
                    <eth_ip_mask>24</eth_ip_mask>
                    <eth_mtu>1500</eth_mtu>
                    <eth_bw>1000000</eth_bw>
                    <eth_dly>10</eth_dly>
                    <eth_reliability>255</eth_reliability>
                    <eth_txload>1</eth_txload>
                    <eth_rxload>1</eth_rxload>
                    <eth_duplex>auto</eth_duplex>
                    <eth_speed>auto-speed</eth_speed>
                    <eth_ethertype>0x0000</eth_ethertype>
                    <vdc_lvl_in_avg_bytes>0</vdc_lvl_in_avg_bytes>
                    <vdc_lvl_in_avg_pkts>0</vdc_lvl_in_avg_pkts>
                    <vdc_lvl_out_avg_bytes>0</vdc_lvl_out_avg_bytes>
                    <vdc_lvl_out_avg_pkts>0</vdc_lvl_out_avg_pkts>
                    <vdc_lvl_in_pkts>0</vdc_lvl_in_pkts>
                    <vdc_lvl_in_ucast>0</vdc_lvl_in_ucast>
                    <vdc_lvl_in_mcast>0</vdc_lvl_in_mcast>
                    <vdc_lvl_in_bcast>0</vdc_lvl_in_bcast>
                    <vdc_lvl_in_bytes>0</vdc_lvl_in_bytes>
                    <vdc_lvl_out_pkts>0</vdc_lvl_out_pkts>
                    <vdc_lvl_out_ucast>0</vdc_lvl_out_ucast>
                    <vdc_lvl_out_mcast>0</vdc_lvl_out_mcast>
                    <vdc_lvl_out_bcast>0</vdc_lvl_out_bcast>
                    <vdc_lvl_out_bytes>0</vdc_lvl_out_bytes>
                  </ROW_interface>
                  <ROW_interface>
                    <interface>Vlan1</interface>
                    <svi_admin_state>up</svi_admin_state>
                    <svi_line_proto>up</svi_line_proto>
                    <svi_mac> 002a.6aa3.d001</svi_mac>
                    <eth_ip_addr>192.168.2.1</eth_ip_addr>
                    <eth_ip_mask>24</eth_ip_mask>
                    <svi_mtu>1500</svi_mtu>
                    <svi_bw>1000000</svi_bw>
                    <svi_delay>10</svi_delay>
                  </ROW_interface>
                  <ROW_interface>
                    <interface>Vlan2</interface>
                    <svi_admin_state>up</svi_admin_state>
                    <svi_line_proto>up</svi_line_proto>
                    <svi_mac> 002a.6aa3.d001</svi_mac>
                    <eth_ip_addr>169.254.253.22</eth_ip_addr>
                    <eth_ip_mask>30</eth_ip_mask>
                    <svi_mtu>1500</svi_mtu>
                    <svi_bw>1000000</svi_bw>
                    <svi_delay>10</svi_delay>
                  </ROW_interface>
                  <ROW_interface>
                    <interface>Vlan3</interface>
                    <svi_admin_state>administratively down</svi_admin_state>
                    <svi_line_proto>down</svi_line_proto>
                    <svi_mac> 002a.6aa3.d001</svi_mac>
                    <eth_ip_addr>192.168.3.2</eth_ip_addr>
                    <eth_ip_mask>24</eth_ip_mask>
                    <svi_mtu>1500</svi_mtu>
                    <svi_bw>1000000</svi_bw>
                    <svi_delay>10</svi_delay>
                  </ROW_interface>
                </TABLE_interface>
              </__readonly__>
            </__XML__OPT_Cmd_show_interface___readonly__>
          </interface>
        </show>
      </nf:data>
    </nf:rpc-reply>


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

  "interface parse" should {

    "return vlan interfaces and filter out mgmt and ethernet ports" in new XmlResponseParser {
      val interfaces = toInterfaces(showInterfaceXml)
      val interfacesNames = interfaces.map(_.name)

      interfaces should have size 3
      interfacesNames should contain("Vlan2")
      interfacesNames should contain ("Vlan1")
      interfacesNames should not contain "Ethernet1/1"
      interfacesNames should not contain "mgmt0"
    }
  }

}
