package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

import com.nativewrapper.types.*;

public class Icmp {

	private static final Map <Integer, Map> mCodeMap = new HashMap <Integer, Map>();
	static {
		mCodeMap.put ( 3, IcmpUnreachCode.get());
		mCodeMap.put ( 5, IcmpRedirectCode.get());
		mCodeMap.put ( 9, IcmpRouterAdvertisementCode.get());
		mCodeMap.put (11, IcmpTimeExceededCode.get());
		mCodeMap.put (12, IcmpParameterProblemCode.get());
	}
	private String getDesc (int type, int code) {
		if (mCodeMap.containsKey(type)) {
			return (String)mCodeMap.get(type).get(code);
		} else {
			return "-";
		}
	}


	// -- netinet/ip_icmp.h --
	// struct icmp
	// {
	//   u_int8_t  icmp_type;	/* type of message, see below */
	//   u_int8_t  icmp_code;	/* type sub code */
	//   u_int16_t icmp_cksum;	/* ones complement checksum of struct */
	//   .
	//   .
	//   .
	// };

	@Uint8
	private int m_icmp_type;

	@Uint8
	private int m_icmp_code;

	@Uint16
	private int m_icmp_cksum;

	@Length
	private int mLength;


	public int getType () {
		return m_icmp_type;
	}

	public int getCode () {
		return m_icmp_code;
	}

	public int getCksum () {
		return m_icmp_cksum;
	}

	@Override
	public String toString () {
		String a = String.format ("m_icmp_type=[%d](%s)", m_icmp_type, IcmpType.getDesc(m_icmp_type));
		String b = String.format ("m_icmp_code=[%d](%s)", m_icmp_code, getDesc (m_icmp_type, m_icmp_code));
		String c = String.format ("m_icmp_cksum=[%d]", m_icmp_cksum);
		return a + " " + b + " " + c + " mLength=[" + mLength + "]";
	}
}
