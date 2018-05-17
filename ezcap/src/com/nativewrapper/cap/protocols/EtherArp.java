package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class EtherArp {

	// -- netinet/if_ether.h --
	// struct  ether_arp {
	//   struct  arphdr ea_hdr;      /* fixed-size header */
	//   u_int8_t arp_sha[ETH_ALEN]; /* sender hardware address */
	//   u_int8_t arp_spa[4];        /* sender protocol address */
	//   u_int8_t arp_tha[ETH_ALEN]; /* target hardware address */
	//   u_int8_t arp_tpa[4];        /* target protocol address */
	// };

	@Struct
	private ArpHeader m_ea_hdr = new ArpHeader();

	@Uint8
	private int [] m_arp_sha = new int [ETH_ALEN];

	@Uint8
	private int [] m_arp_spa = new int [4];

	@Uint8
	private int [] m_arp_tha = new int [ETH_ALEN];

	@Uint8
	private int [] m_arp_tpa = new int [4];

	@StructSize
	private int mStructSize;

	private static final int ETH_ALEN = 6;


	public ArpHeader getArpHeader () {
		return m_ea_hdr;
	}

	public int [] getSenderHardAddr () {
		return m_arp_sha;
	}

	public int [] getSenderProtoAddr () {
		return m_arp_spa;
	}

	public int [] getTargetHardAddr () {
		return m_arp_tha;
	}

	public int [] getTargetProtoAddr () {
		return m_arp_tpa;
	}

	public int structSize () {
		return mStructSize;
	}

	@Override
	public String toString () {
		String sha = "m_arp_sha=[";
		for (int i = 0; i < ETH_ALEN; ++ i) {
			if (i != 0) {
				sha += ":";
			}
			sha += String.format ("%02x", m_arp_sha[i]);
		}
		sha += "]";
		String spa = "m_arp_spa=[";
		for (int i = 0; i < 4; ++ i) {
			if (i != 0) {
				spa += ".";
			}
			spa += String.format ("%d", m_arp_spa[i]);
		}
		spa += "]";
		String tha = "m_arp_tha=[";
		for (int i = 0; i < ETH_ALEN; ++ i) {
			if (i != 0) {
				tha += ":";
			}
			tha += String.format ("%02x", m_arp_tha[i]);
		}
		tha += "]";
		String tpa = "m_arp_tpa=[";
		for (int i = 0; i < 4; ++ i) {
			if (i != 0) {
				tpa += ".";
			}
			tpa += String.format ("%d", m_arp_tpa[i]);
		}
		tpa += "]";
		return m_ea_hdr + "\n" + sha + " " + spa + " " + tha + " " + tpa + " mStructSize=[" + mStructSize + "]";
	}
}
