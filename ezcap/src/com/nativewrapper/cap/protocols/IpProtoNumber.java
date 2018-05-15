package com.nativewrapper.cap.protocols;

import java.util.Map;
import java.util.HashMap;

public enum IpProtoNumber {

	// http://www.infraexpert.com/study/tea11.htm
	ICMP       (  1, "ICMP", "Internet Control Message"),
	IGMP       (  2, "IGMP", "Internet Group Management"),
	IP         (  4, "IP", "IP in IP ( encapsulation )"),
	TCP        (  6, "TCP", "Transmission Control"),
	CBT        (  7, "CBT", "CBT"),
	EGP        (  8, "EGP", "Exterior Gateway Protocol"),
	IGP        (  9, "IGP", "any private interior gateway"),
	UDP        ( 17, "UDP", "User Datagram"),
	IPv6       ( 41, "IPv6", "IPv6"),
	IPv6_Route ( 43, "IPv6-Route", "Routing Header for IPv6"),
	IPv6_Frag  ( 44, "IPv6-Frag", "Fragment Header for IPv6"),
	IDRP       ( 45, "IDRP", "Inter-Domain Routing Protocol"),
	RSVP       ( 46, "RSVP", "Reservation Protocol"),
	GRE        ( 47, "GRE", "General Routing Encapsulation"),
	ESP        ( 50, "ESP", "Encap Security Payload"),
	AH         ( 51, "AH", "Authentication Header"),
	MOBILE     ( 55, "MOBILE", "IP Mobility"),
	IPv6_ICMP  ( 58, "IPv6-ICMP", "ICMP for IPv6"),
	IPv6_NoNxt ( 59, "IPv6-NoNxt", "No Next Header for IPv6"),
	IPv6_Opts  ( 60, "IPv6-Opts", "Destination Options for IPv6"),
	EIGRP      ( 88, "EIGRP", "EIGRP"),
	OSPF       ( 89, "OSPF", "OSPF"),
	IPIP       ( 94, "IPIP", "IP-within-IP Encapsulation Protocol"),
	PIM        (103, "PIM", "Protocol Independent Multicast"),
	VRRP       (112, "VRRP", "Virtual Router Redundancy Protocol"),
	PGM        (113, "PGM", "PGM Reliable Transport Protocol"),
	L2TP       (115, "L2TP", "Layer Two Tunneling Protocol");

	private int mProtoNum;
	private String mName;
	private String mDesc;
 
	private IpProtoNumber (int protoNum, String name, String desc) {
		mProtoNum = protoNum;
		mName = name;
		mDesc = desc;
	}

	public int getProtoNum () {
		return mProtoNum;
	}
 

	private static final Map <Integer, String> nameMap = new HashMap <Integer, String>();
	static {
		for (IpProtoNumber entry : values()) {
			nameMap.put (entry.mProtoNum, entry.mName);
		}
	}

	private static final Map <Integer, String> mDescMap = new HashMap <Integer, String>();
	static {
		for (IpProtoNumber entry : values()) {
			mDescMap.put (entry.mProtoNum, entry.mDesc);
		}
	}

	public static String getName (int protoNum) {
		if (nameMap.containsKey(protoNum)) {
			return nameMap.get (protoNum);
		} else {
			return "unknown.";
		}
	}

	public static String getDesc (int protoNum) {
		if (mDescMap.containsKey(protoNum)) {
			return mDescMap.get (protoNum);
		} else {
			return "unknown.";
		}
	}
}
