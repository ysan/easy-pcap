package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpTimeExceededCode {

	// netinet/ip_icmp.h
	TTL       (0, "TTL count exceeded"),
	FRAGTIME  (1, "Fragment Reass time exceeded");


	private int mCode;
	private String mDesc;
 
	private IcmpTimeExceededCode (int type, String desc) {
		mCode = type;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> descMap = new HashMap <Integer, String>();
	static {
		for (IcmpTimeExceededCode entry : values()) {
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
