package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpRouterAdvertisementCode {

	// http://www.infraexpert.com/info/5.0adsl.htm
	// ICMP Type 9 ：Router Advertisement
	ROUTER_ADV  ( 0, "Normal router advertisement"),
	NOT_ROUTE   (16, "Does not route common traffic");


	private int mCode;
	private String mDesc;
 
	private IcmpRouterAdvertisementCode (int code, String desc) {
		mCode = code;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IcmpRouterAdvertisementCode entry : values()) {
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
