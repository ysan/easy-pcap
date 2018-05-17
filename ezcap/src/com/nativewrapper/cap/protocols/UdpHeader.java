package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class UdpHeader {

	// -- netinet/udp.h --
	// struct udphdr
	// {
	//    __extension__ union
	//   {
	//     struct
	//     {
	//       u_int16_t uh_sport;       /* source port */
	//       u_int16_t uh_dport;       /* destination port */
	//       u_int16_t uh_ulen;        /* udp length */
	//       u_int16_t uh_sum;     /* udp checksum */
	//     };
	//     struct
	//     {
	//       u_int16_t source;
	//       u_int16_t dest;
	//       u_int16_t len;
	//       u_int16_t check;
	//     };
	//   };
	// };


	@Uint16
	private int m_source;

	@Uint16
	private int m_dest;

	@Uint16
	private int m_len;

	@Uint16
	private int m_check;

	@StructSize
	private int mStructSize;


	public int getSrcPort () {
		return m_source;
	}

	public int getDstPort () {
		return m_dest;
	}

	public int getTotalLen () {
		return m_len;
	}

	public int getCksum () {
		return m_check;
	}

	public int structSize () {
		return mStructSize;
	}


	@Override
	public String toString () {
		String a = String.format ("m_source=[%d]", m_source);
		String b = String.format ("m_dest=[%d]", m_dest);
		String c = String.format ("m_len=[%d]", m_len);
		String d = String.format ("m_check=[%d]", m_check);
		return a + " " + b + " " + c + " " + d + " mStructSize=[" + mStructSize + "]";
	}
}
