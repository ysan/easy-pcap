package com.jnistudy.cap.protocol;

import java.util.Map;
import java.util.HashMap;

public enum ArpOpcode {

	// net/ethernet.h
	ARPOP_REQUEST   ( 1, "ARP request."),
	ARPOP_REPLY     ( 2, "ARP reply."),
	ARPOP_RREQUEST  ( 3, "RARP request."),
	ARPOP_RREPLY    ( 4, "RARP reply."),
	ARPOP_InREQUEST ( 8, "InARP request."),
	ARPOP_InREPLY   ( 9, "InARP reply."),
	ARPOP_NAK       (10, "(ATM)ARP NAK.");


	private int mOpcode;
	private String mDesc;
 
	private ArpOpcode (int opcode, String desc) {
		mOpcode = opcode;
		mDesc = desc;
	}

	public int getOpcode () {
		return mOpcode;
	}
 

	private static final Map <Integer, String> descMap = new HashMap <Integer, String>();
	static {
		for (ArpOpcode entry : values()) {
			descMap.put (entry.mOpcode, entry.mDesc);
		}
	}

	public static String getDesc (int opcode) {
		if (descMap.containsKey(opcode)) {
			return descMap.get (opcode);
		} else {
			return "unknown.";
		}
	}
}
