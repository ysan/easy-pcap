package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpUnreachCode {

	// http://www.infraexpert.com/info/5.0adsl.htm
	// ICMP Type 3 ： Detination Unreachable
	NET_UNREACH     ( 0, "Net Unreachable"),
	HOST_UNREACH    ( 1, "Host Unreachable"),
	PROT_UNREACH    ( 2, "Protocol Unreachable"),
	PORT_UNREACH    ( 3, "Port Unreachable"),
	FRAG_NEEDED     ( 4, "Fragmentation Needed and DF was Set"),
	SR_FAILED       ( 5, "Source Route Failed"),
	NET_UNKNOWN     ( 6, "Destination Network Unknown"),
	HOST_UNKNOWN    ( 7, "Destination Host Unknown "),
	HOST_ISOLATED   ( 8, "Source Host Isolated"),
	NET_ANO         ( 9, "Communication with Destination Network is Administratively Prohibited"),
	HOST_ANO        (10, "Communication with Destinaltion Host is Administratively Prohibited"),
	NET_UNR_TOS     (11, "Destination Network Unreachable for ToS"),
	HOST_UNR_TOS    (12, "Destination Host Unreachable for ToS"),
	PKT_FILTERED    (13, "Communication Administratively Prohibited"),
	PREC_VIOLATION  (14, "Host Precedence Violatio"),
	PREC_CUTOFF     (15, "Precedence cutoff in effect");


	private int mCode;
	private String mDesc;
 
	private IcmpUnreachCode (int code, String desc) {
		mCode = code;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IcmpUnreachCode entry : values()) {
			mDescMap.put (entry.mCode, entry.mDesc);
		}
	}
	public static Map get () {
		return mDescMap;
	}

	public static String getDesc (int code) {
		if (mDescMap.containsKey(code)) {
			return mDescMap.get (code);
		} else {
			return "unknown.";
		}
	}
}
