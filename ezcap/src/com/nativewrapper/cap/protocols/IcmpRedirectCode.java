package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpRedirectCode {

	// http://www.infraexpert.com/info/5.0adsl.htm
	// ICMP Type 5 ： Redirect
	NET      (0, "Redirect Datagram for the Network (or subnet) "),
	HOST     (1, "Redirect Datagram for the Host"),
	NETTOS   (2, "Redirect Datagram for the ToS and Network"),
	HOSTTOS  (3, "Redirect Datagram for the ToS and Host");


	private int mCode;
	private String mDesc;
 
	private IcmpRedirectCode (int code, String desc) {
		mCode = code;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IcmpRedirectCode entry : values()) {
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
