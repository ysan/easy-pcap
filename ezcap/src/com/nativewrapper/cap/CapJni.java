package com.nativewrapper.cap;

import java.util.List;
import java.util.ArrayList;

public class CapJni {
	static {
		System.loadLibrary ("capjni");
	}
	
	// native
	private native boolean nativeInit ();
	private native void nativeFin ();
	private native String nativeGetVersion ();
	private native void nativeSetInterface (String name);
	private native boolean nativeSetFilter (String filter);
	private native String nativeGetFilter ();
	private native boolean nativeClearFilter ();
	private native boolean nativeStart ();
	private native void nativeStop ();
	private native int nativeRegisterPacketListener (IPacketListener listener);
	private native boolean nativeUnregisterPacketListener (int id);
	private native int nativeGetInterfaceList (List<Interface> outList);


	public boolean init () {
		return nativeInit ();
	}

	public void fin () {
		nativeFin ();
	}

	public String getVersion () {
		return nativeGetVersion ();
	}

	public void setInterface (String name) {
		if (name == null) {
			return;
		}

		nativeSetInterface (name);
	}

	public boolean setFilter (String filter) {
		if (filter == null) {
			return false;
		}

		return nativeSetFilter (filter);
	}

	public String getFilter () {
		return nativeGetFilter();
	}

	public boolean clearFilter () {
		return nativeClearFilter();
	}

	public boolean start () {
		return nativeStart ();
	}

	public void stop () {
		nativeStop ();
	}

	public int registerPacketListener (IPacketListener listener) {
		if (listener == null) {
			return -1;
		}

		return nativeRegisterPacketListener (listener);
	}

	public boolean unregisterPacketListener (int id) {
		return nativeUnregisterPacketListener (id);
	}

	public List<Interface> getInterfaceList () {
		List<Interface> rtn = new ArrayList<Interface>();
		rtn.clear();
		int n = nativeGetInterfaceList (rtn);
		return rtn;
	}

}
