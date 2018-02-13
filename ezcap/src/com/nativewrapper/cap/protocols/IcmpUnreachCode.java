package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpUnreachCode {

	// netinet/ip_icmp.h
	NET_UNREACH     ( 0, "Network Unreachable Reply"),
	HOST_UNREACH    ( 1, "Host Unreachable"),
	PROT_UNREACH    ( 2, "Protocol Unreachable"),
	PORT_UNREACH    ( 3, "Port Unreachable"),
	FRAG_NEEDED     ( 4, "Fragmentation Needed/DF set"),
	SR_FAILED       ( 5, "Source Route failed"),
	NET_UNKNOWN     ( 6, ""),
	HOST_UNKNOWN    ( 7, ""),
	HOST_ISOLATED   ( 8, ""),
	NET_ANO         ( 9, ""),
	HOST_ANO        (10, ""),
	NET_UNR_TOS     (11, ""),
	HOST_UNR_TOS    (12, ""),
	PKT_FILTERED    (13, "Packet filtered"),
	PREC_VIOLATION  (14, "Precedence violation"),
	PREC_CUTOFF     (15, "Precedence cut off");


	private int mCode;
	private String mDesc;
 
	private IcmpUnreachCode (int type, String desc) {
		mCode = type;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> descMap = new HashMap <Integer, String>();
	static {
		for (IcmpUnreachCode entry : values()) {
			descMap.put (entry.mCode, entry.mDesc);
		}
	}

	public static String getDesc (int type) {
		if (descMap.containsKey(type)) {
			return descMap.get (type);
		} else {
			return "unknown.";
		}
	}
}
