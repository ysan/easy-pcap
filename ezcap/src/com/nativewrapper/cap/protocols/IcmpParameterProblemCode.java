package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpParameterProblemCode {

	// http://www.infraexpert.com/info/5.0adsl.htm
	// ICMP Type 12 ：Parameter Problem
	PONTER_ERROR     (0, "Pointer indicates the error"),
	MISS_REQ_OPTION  (1, "Missing a Required Option"),
	BAD_LENGTH       (2, "Bad Length");


	private int mCode;
	private String mDesc;
 
	private IcmpParameterProblemCode (int code, String desc) {
		mCode = code;
		mDesc = desc;
	}

	public int getCode () {
		return mCode;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IcmpParameterProblemCode entry : values()) {
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
