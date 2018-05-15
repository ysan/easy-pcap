package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class TcpHeader {

	// -- netinet/tcp.h --
	// struct tcphdr
	// {
	//   __extension__ union
	//   {
	//     .
	//     .
	//     .
	//     struct
	//     {
	//       u_int16_t source;
	//       u_int16_t dest;
	//       u_int32_t seq;
	//       u_int32_t ack_seq;
	// # if __BYTE_ORDER == __LITTLE_ENDIAN
	//       u_int16_t res1:4;
	//       u_int16_t doff:4;
	//       u_int16_t fin:1;
	//       u_int16_t syn:1;
	//       u_int16_t rst:1;
	//       u_int16_t psh:1;
	//       u_int16_t ack:1;
	//       u_int16_t urg:1;
	//       u_int16_t res2:2;
	// # elif __BYTE_ORDER == __BIG_ENDIAN
	//       u_int16_t doff:4;
	//       u_int16_t res1:4;
	//       u_int16_t res2:2;
	//       u_int16_t urg:1;
	//       u_int16_t ack:1;
	//       u_int16_t psh:1;
	//       u_int16_t rst:1;
	//       u_int16_t syn:1;
	//       u_int16_t fin:1;
	// # else
	// #     error "Adjust your <bits/endian.h> defines"
	// # endif
	//       u_int16_t window;
	//       u_int16_t check;
	//       u_int16_t urg_ptr;
	//     };
	//   };
	// };

	@Uint16
	private int m_source;

	@Uint16
	private int m_dest;

	@Uint32
	private long m_seq;

	@Uint32
	private long m_ack_seq;

	@Uint16
	private int m_code;

	@Uint16
	private int m_window;

	@Uint16
	private int m_check;

	@Uint16
	private int m_urg_ptr;

	@Length
	private int mLength;


	public int getSrcPort () {
		return m_source;
	}

	public int getDstPort () {
		return m_dest;
	}

	public long getSeq () {
		return m_seq;
	}

	public long getAckSeq () {
		return m_ack_seq;
	}

	public int getRes1 () {
		return (m_code >> 12) & 0xf;
	}

	public int getDoff () {
		return (m_code >> 8) & 0xf;
	}

	public int getFin () {
		return (m_code >> 7) & 0x1;
	}

	public int getSyn () {
		return (m_code >> 6) & 0x1;
	}

	public int getRst () {
		return (m_code >> 5) & 0x1;
	}

	public int getPsh () {
		return (m_code >> 4) & 0x1;
	}

	public int getAck () {
		return (m_code >> 3) & 0x1;
	}

	public int getUrg () {
		return (m_code >> 2) & 0x1;
	}

	public int getRes2 () {
		return m_code & 0x3;
	}

	public int getWinSize () {
		return m_window;
	}

	public int getCksum () {
		return m_check;
	}

	public int getUrgPtr () {
		return m_urg_ptr;
	}

	@Override
	public String toString () {
		String a = String.format ("m_source=[%d]", m_source);
		String b = String.format ("m_dest=[%d]", m_dest);
		String c = String.format ("m_seq=[%d]", m_seq);
		String d = String.format ("m_ack_seq=[%d]", m_ack_seq);
		String e = String.format (
			"m_code=[0x%04x](doff:%d, codeBit:%s%s%s%s%s%s)",
			m_code,
			getDoff(),
			getFin() == 1 ? "FIN," : "",
			getSyn() == 1 ? "SYN," : "",
			getRst() == 1 ? "RST," : "",
			getPsh() == 1 ? "PSH," : "",
			getAck() == 1 ? "ACK," : "",
			getUrg() == 1 ? "URG," : ""
		);
		String f = String.format ("m_window=[%d]", m_window);
		String g = String.format ("m_check=[0x%04x]", m_check);
		String h = String.format ("m_urg_ptr=[%d]", m_urg_ptr);
		return a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " mLength=[" + mLength + "]";
	}
}
