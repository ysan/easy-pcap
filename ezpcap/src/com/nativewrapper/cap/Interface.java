package com.nativewrapper.cap;

public class Interface {
	private String mName;
	private String mDesc;
	private boolean mIsLoopback;

	private String mAddr;
	private String mNetmask;
	private String mBroadAddr;
	private String mDestAddr;

	private String mAddr_v6;
	private String mNetmask_v6;
	private String mBroadAddr_v6;
	private String mDestAddr_v6;


	public String getName () {
		return mName;
	}
	
	public String getDescription () {
		return mDesc;
	}

	public boolean isLoopback () {
		return mIsLoopback;
	}


	public String getAddress () {
		return mAddr;
	}

	public String getSubnetmask () {
		return mNetmask;
	}

	public String getBroadcastAddress () {
		return mBroadAddr;
	}

	public String getDestinationAddress () {
		return mDestAddr;
	}


	public String getAddress_v6 () {
		return mAddr_v6;
	}

	public String getSubnetmask_v6 () {
		return mNetmask_v6;
	}

	public String getBroadcastAddress_v6 () {
		return mBroadAddr_v6;
	}

	public String getDestinationAddress_v6 () {
		return mDestAddr_v6;
	}

	@Override
	public String toString () {
		String name = "mName=[" + mName + "] mDesc=[" + mDesc + "] mIsLoopback=[" + mIsLoopback + "]\n";
		String addr = "mAddr=[" + mAddr + "] mNetmask=[" + mNetmask + "] mBroadAddr=[" + mBroadAddr + "] mDestAddr=[" + mDestAddr + "]\n";
		String addrv6 = "mAddr_v6=[" + mAddr_v6 + "] mNetmask_v6=[" + mNetmask_v6 + "] mBroadAddr_v6=[" + mBroadAddr_v6 + "] mDestAddr_v6=[" + mDestAddr_v6 + "]";

		return name + " " + addr + " " + addrv6;
	}
}
