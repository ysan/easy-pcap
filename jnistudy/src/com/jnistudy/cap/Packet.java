package com.jnistudy.cap;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Packet {
	private Date mTimestamp;
	private byte [] mRaw;
	private int mLength;


	public Date getTimestamp () {
		return mTimestamp;
	}

	public byte [] getRaw () {
		return mRaw;
	}

	public int length () {
		return mLength;
	}

	@Override
	public String toString () {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return "mTimestamp=[" + sdf.format(mTimestamp) + "] mLength=[" + mLength + "]";
	}
}
