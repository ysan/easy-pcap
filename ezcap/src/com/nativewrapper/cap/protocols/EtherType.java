package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum EtherType {

	// net/ethernet.h
	PUP       (0x0200, "Xerox PUP"),
	SPRITE    (0x0500, "Sprite"),
	IP        (0x0800, "IP"),
	ARP       (0x0806, "Address resolution"),
	REVARP    (0x0835, "Reverse ARP"),
	AT        (0x809B, "AppleTalk protocol"),
	AARP      (0x80F3, "AppleTalk ARP"),
	VLAN      (0x8100, "IEEE 802.1Q VLAN tagging"),
	IPX       (0x8137, "IPX"),
	IPV6      (0x86dd, "IP protocol version 6"),
	LOOPBACK  (0x9000, "used to test interfaces");


	private int mType;
	private String mDesc;
 
	private EtherType (int type, String desc) {
		mType = type;
		mDesc = desc;
	}

	public int getType () {
		return mType;
	}
 

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (EtherType entry : values()) {
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
