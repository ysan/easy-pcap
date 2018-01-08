package com.jnistudy.cap.protocol;

import java.util.Map;
import java.util.HashMap;

public enum EtherType {

	// net/ethernet.h
	ETHERTYPE_PUP       (0x0200, "Xerox PUP"),
	ETHERTYPE_SPRITE    (0x0500, "Sprite"),
	ETHERTYPE_IP        (0x0800, "IP"),
	ETHERTYPE_ARP       (0x0806, "Address resolution"),
	ETHERTYPE_REVARP    (0x0835, "Reverse ARP"),
	ETHERTYPE_AT        (0x809B, "AppleTalk protocol"),
	ETHERTYPE_AARP      (0x80F3, "AppleTalk ARP"),
	ETHERTYPE_VLAN      (0x8100, "IEEE 802.1Q VLAN tagging"),
	ETHERTYPE_IPX       (0x8137, "IPX"),
	ETHERTYPE_IPV6      (0x86dd, "IP protocol version 6"),
	ETHERTYPE_LOOPBACK  (0x9000, "used to test interfaces");


	private int mType;
	private String mDesc;
 
	private EtherType (int type, String desc) {
		mType = type;
		mDesc = desc;
	}

	public int getType () {
		return mType;
	}
 

	private static final Map <Integer, String> descMap = new HashMap <Integer, String>();
	static {
		for (EtherType entry : values()) {
			descMap.put (entry.mType, entry.mDesc);
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
