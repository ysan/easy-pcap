package com.jnistudy.cap.types;

import java.nio.ByteOrder;

public final class Defines {
	private Defines () {}

	public static final int UINT8_SIZE = 1;
	public static final ByteOrder UINT8_ENDIAN = ByteOrder.BIG_ENDIAN;

	public static final int UINT16_SIZE = 2;
	public static final ByteOrder UINT16_ENDIAN = ByteOrder.BIG_ENDIAN;

	public static final int UINT32_SIZE = 4;
	public static final ByteOrder UINT32_ENDIAN = ByteOrder.BIG_ENDIAN;
}
