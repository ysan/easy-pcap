package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class ArpHeader {

	// -- net/if_arp.h --
	// struct arphdr
	// {
	//   unsigned short int ar_hrd;      /* Format of hardware address.  */
	//   unsigned short int ar_pro;      /* Format of protocol address.  */
	//   unsigned char ar_hln;       /* Length of hardware address.  */
	//   unsigned char ar_pln;       /* Length of protocol address.  */
	//   unsigned short int ar_op;       /* ARP opcode (command).  */
	// };

	@Uint16
	private int m_ar_hrd;

	@Uint16
	private int m_ar_pro;

	@Uint8
	private int m_ar_hln;

	@Uint8
	private int m_ar_pln;

	@Uint16
	private int m_ar_op;

	@Length
	private int mLength;


	public int getHard () {
		return m_ar_hrd;
	}

	public int getProto () {
		return m_ar_pro;
	}

	public int getHardLen () {
		return m_ar_hln;
	}

	public int getProtoLen () {
		return m_ar_pln;
	}

	public int getOpcode () {
		return m_ar_op;
	}

	public int length () {
		return mLength;
	}

	@Override
	public String toString () {
		String a = String.format ("m_ar_hrd=[%d]", m_ar_hrd);
		String b = String.format ("m_ar_pro=[0x%04x]", m_ar_pro);
		String c = String.format ("m_ar_hln=[%d]", m_ar_hln);
		String d = String.format ("m_ar_pln=[%d]", m_ar_pln);
		String e = String.format ("m_ar_op=[%d](%s)", m_ar_op, ArpOpcode.getDesc(m_ar_op));
		return a + " " + b + " " + c + " " + d + " " + e + " mLength=[" + mLength + "]";
	}
}
