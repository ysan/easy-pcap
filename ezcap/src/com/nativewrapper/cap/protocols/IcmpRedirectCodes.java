package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpRedirectCodes {

	// netinet/ip_icmp.h
	NET      (0, "Redirect Net"),
	HOST     (1, "Redirect Host"),
	NETTOS   (2, "Redirect Net for TOS"),
	HOSTTOS  (3, "Redirect Host for TOS");


	private int mCode;
	private String mDesc;
 
	private IcmpRedirectCodes (int type, String desc) {
		mCode = type;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> descMap = new HashMap <Integer, String>();
	static {
		for (IcmpRedirectCodes entry : values()) {
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
