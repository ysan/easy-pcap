package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class EtherHeader {

	// -- net/ethernet.h --
	// struct ether_header
	// {
	//   u_int8_t  ether_dhost[ETH_ALEN];  /* destination eth addr */
	//   u_int8_t  ether_shost[ETH_ALEN];  /* source ether addr    */
	//   u_int16_t ether_type;             /* packet type ID field */
	// };

	@Uint8
	private int [] m_ether_dhost = new int [ETH_ALEN];

	@Uint8
	private int [] m_ether_shost = new int [ETH_ALEN];

	@Uint16
	private int m_ether_type;

	@StructSize
	private int mStructSize;

	private static final int ETH_ALEN = 6;


	public int [] getDst () {
		return m_ether_dhost;
	}

	public int [] getSrc () {
		return m_ether_shost;
	}

	public int getType () {
		return m_ether_type;
	}

	public int structSize () {
		return mStructSize;
	}

	@Override
	public String toString () {
		String dst = "m_ether_dhost=[";
		for (int i = 0; i < ETH_ALEN; ++ i) {
			if (i != 0) {
				dst += ":";
			}
			dst += String.format ("%02x", m_ether_dhost[i]);
		}
		dst += "]";
		String src = "m_ether_shost=[";
		for (int i = 0; i < ETH_ALEN; ++ i) {
			if (i != 0) {
				src += ":";
			}
			src += String.format ("%02x", m_ether_shost[i]);
		}
		src += "]";
		String type = String.format ("m_ether_type=[0x%04x](%s)", m_ether_type ,EtherType.getDesc(m_ether_type));
		return src + " " + dst + " " + type + " mStructSize=[" + mStructSize + "]";
	}
}
