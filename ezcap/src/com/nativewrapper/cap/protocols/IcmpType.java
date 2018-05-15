package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IcmpType {

	// netinet/ip_icmp.h
	ECHOREPLY       ( 0, "Echo Reply"),
	DEST_UNREACH    ( 3, "Destination Unreachable"),
	SOURCE_QUENCH   ( 4, "Source Quench"),
	REDIRECT        ( 5, "Redirect (change route)"),
	ECHO            ( 8, "Echo Request"),
	TIME_EXCEEDED   (11, "Time Exceeded"),
	PARAMETERPROB   (12, "Parameter Problem"),
	TIMESTAMP       (13, "Timestamp Request"),
	TIMESTAMPREPLY  (14, "Timestamp Reply"),
	INFO_REQUEST    (15, "Information Request"),
	INFO_REPLY      (16, "Information Reply"),
	ADDRESS         (17, "Address Mask Request"),
	ADDRESSREPLY    (18, "Address Mask Reply");


	private int mType;
	private String mDesc;
 
	private IcmpType (int type, String desc) {
		mType = type;
		mDesc = desc;
	}

	public int getType () {
		return mType;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IcmpType entry : values()) {
			mDescMap.put (entry.mType, entry.mDesc);
		}
	}

	public static String getDesc (int type) {
		if (mDescMap.containsKey(type)) {
			return mDescMap.get (type);
		} else {
			return "unknown.";
		}
	}
}
