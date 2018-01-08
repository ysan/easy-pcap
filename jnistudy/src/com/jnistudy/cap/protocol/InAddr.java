package com.jnistudy.cap.protocol;

import com.jnistudy.cap.types.*;

public class InAddr {

	// -- netinet/in.h --
	// typedef uint32_t in_addr_t;
	// struct in_addr
	//   {
	//     in_addr_t s_addr;
	//   };

	@Uint32
	private long m_s_addr;

	@Length
	private int mLength;


	public long getAddr () {
		return m_s_addr;
	}

	public int length () {
		return mLength;
	}

	@Override
	public String toString () {
		String str = String.format (
							"m_s_addr=[%d.%d.%d.%d]",
							(m_s_addr >> 24) & 0xff,
							(m_s_addr >> 16) & 0xff,
							(m_s_addr >>  8) & 0xff,
							m_s_addr & 0xff
						);
		return str + " mLength=[" + mLength + "]";
	}
}
