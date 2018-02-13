package com.nativewrapper.cap.protocols;

import com.nativewrapper.types.*;

public class Ip {

	// -- netinet/ip.h --
	// struct ip
	//   {
	//     unsigned int ip_v:4;        /* version */  // __BIG_ENDIAN
	//     unsigned int ip_hl:4;       /* header length */  // __BIG_ENDIAN
	//     u_int8_t ip_tos;            /* type of service */
	//     u_short ip_len;         /* total length */
	//     u_short ip_id;          /* identification */
	//     u_short ip_off;         /* fragment offset field */
	//     u_int8_t ip_ttl;            /* time to live */
	//     u_int8_t ip_p;          /* protocol */
	//     u_short ip_sum;         /* checksum */
	//     struct in_addr ip_src, ip_dst;  /* source and dest address */
	//   };

	@Uint8
	private int m_ip_v__ip_hl;

	@Uint8
	private int m_ip_tos;

	@Uint16
	private int m_ip_len;

	@Uint16
	private int m_ip_id;

	@Uint16
	private int m_ip_off;

	@Uint8
	private int m_ip_ttl;

	@Uint8
	private int m_ip_p;

	@Uint16
	private int m_ip_sum;

	@Struct
	private InAddr m_ip_src = new InAddr();

	@Struct
	private InAddr m_ip_dst = new InAddr();

	@Length
	private int mLength;


	public int getVer () {
		return (m_ip_v__ip_hl >> 4) & 0xf;
	}

	public int getHeaderLen () {
		return m_ip_v__ip_hl & 0xf;
	}

	public int getTos () {
		return m_ip_tos;
	}

	public int getTotalLen () {
		return m_ip_len;
	}

	public int getId () {
		return m_ip_id;
	}

	public int getOffset () {
		return m_ip_off;
	}

	public int getTtl () {
		return m_ip_ttl;
	}

	public int getProto () {
		return m_ip_p;
	}

	public int getSum () {
		return m_ip_sum;
	}

	public InAddr getSrc () {
		return m_ip_src;
	}

	public InAddr getDst () {
		return m_ip_dst;
	}

	public int length () {
		return mLength;
	}

	@Override
	public String toString () {
		String a = String.format ("m_ip_v__ip_hl=[v%d][hl:%d*4]", (m_ip_v__ip_hl >> 4) & 0xf, m_ip_v__ip_hl & 0xf);
		String b = String.format ("m_ip_tos=[%d]", m_ip_tos);
		String c = String.format ("m_ip_len=[%d]", m_ip_len);
		String d = String.format ("m_ip_id=[%d]", m_ip_id);
		String e = String.format ("m_ip_off=[flags:0x%02x][offset:%d]", (m_ip_off >> 13) & 0x07, m_ip_off & 0x1fff);
		String f = String.format ("m_ip_ttl=[%d]", m_ip_ttl);
		String g = String.format ("m_ip_p=[%d](%s)", m_ip_p, IpProtoNumber.getName(m_ip_p));
		String h = String.format ("m_ip_sum=[0x%04x]", m_ip_sum);
		return a + " " + b + " " + c + " " + d + " " + e + " " + f + " " + g + " " + h + " m_ip_src=[" + m_ip_src + "]" + " m_ip_dst=[" + m_ip_dst + "]" + " mLength=[" + mLength + "]";
	}
}
