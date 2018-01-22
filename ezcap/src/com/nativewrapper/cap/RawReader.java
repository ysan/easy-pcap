package com.nativewrapper.cap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.nativewrapper.types.*;

public final class RawReader {
	private RawReader () {}

	public static int toStruct (Object obj, byte[] data, int offset) {
		int off = offset;
		int len = 0;
		Field [] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
//			System.out.println (field);

			if (field.getType().isArray()) {
//				System.out.println (field + " array!!!");
				int arrLen = 0;
				try {
					arrLen = Array.getLength(field.get(obj));
//					System.out.println ("arrLen: " + arrLen);
				} catch (IllegalAccessException e) {
					System.out.println (e);
					e.printStackTrace();
				}

				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {

					if (annotation.annotationType().equals(Uint8.class)) {
						for (int idx = 0; idx < arrLen; ++ idx) {
							int value = getIntValue (data, Defines.UINT8_SIZE, Defines.UINT8_ENDIAN, off);
							off += Defines.UINT8_SIZE;
							len += Defines.UINT8_SIZE;
							try {
								Array.set (field.get(obj), idx, value);
							} catch (IllegalAccessException e) {
								System.out.println (e);
								e.printStackTrace();
							}
						}

					} else if (annotation.annotationType().equals(Uint16.class)) {
						for (int idx = 0; idx < arrLen; ++ idx) {
							int value = getIntValue (data, Defines.UINT16_SIZE, Defines.UINT16_ENDIAN, off);
							off += Defines.UINT16_SIZE;
							len += Defines.UINT16_SIZE;
							try {
								Array.set (field.get(obj), idx, value);
							} catch (IllegalAccessException e) {
								System.out.println (e);
								e.printStackTrace();
							}
						}

					} else if (annotation.annotationType().equals(Uint32.class)) {
						for (int idx = 0; idx < arrLen; ++ idx) {
							long value = getLongValue (data, Defines.UINT32_SIZE, Defines.UINT32_ENDIAN, off);
							off += Defines.UINT32_SIZE;
							len += Defines.UINT32_SIZE;
							try {
								Array.set (field.get(obj), idx, value);
							} catch (IllegalAccessException e) {
								System.out.println (e);
								e.printStackTrace();
							}
						}

					} else if (annotation.annotationType().equals(Struct.class)) {
						//TODO
					}
				}

			} else {
				// not array

				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {

					if (annotation.annotationType().equals(Uint8.class)) {
						int value = getIntValue (data, Defines.UINT8_SIZE, Defines.UINT8_ENDIAN, off);
						off += Defines.UINT8_SIZE;
						len += Defines.UINT8_SIZE;
						try {
							field.set(obj, value);
						} catch (IllegalAccessException e) {
							System.out.println (e);
							e.printStackTrace();
						}

					} else if (annotation.annotationType().equals(Uint16.class)) {
						int value = getIntValue (data, Defines.UINT16_SIZE, Defines.UINT16_ENDIAN, off);
						off += Defines.UINT16_SIZE;
						len += Defines.UINT16_SIZE;
						try {
							field.set(obj, value);
						} catch (IllegalAccessException e) {
							System.out.println (e);
							e.printStackTrace();
						}

					} else if (annotation.annotationType().equals(Uint32.class)) {
						long value = getLongValue (data, Defines.UINT32_SIZE, Defines.UINT32_ENDIAN, off);
						off += Defines.UINT32_SIZE;
						len += Defines.UINT32_SIZE;
						try {
							field.set(obj, value);
						} catch (IllegalAccessException e) {
							System.out.println (e);
							e.printStackTrace();
						}

					} else if (annotation.annotationType().equals(Struct.class)) {
						try {
							int rtn = RawReader.toStruct (field.get(obj), data, off);
							off += rtn;
							len += rtn;
//							System.out.println ("reentrant RawToStruct.convert " + rtn + " " + off + " "+ len);
						} catch (IllegalAccessException e) {
							System.out.println (e);
							e.printStackTrace();
						}

					} else if (annotation.annotationType().equals(Length.class)) {
						// Length.class must be last
						try {
							field.set(obj, len);
						} catch (IllegalAccessException e) {
							System.out.println (e);
							e.printStackTrace();
						}
					}
				}
			}

		}

		return len;
	}

	public static int getIntValue (byte[] data, int size, ByteOrder ENDIAN, int off) {
		if ((size != Defines.UINT8_SIZE) && (size != Defines.UINT16_SIZE)) {
			throw new IllegalArgumentException ("invalid argument.");
		}

		int value = 0;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream (new ByteArrayInputStream(data, off, data.length));
			if (size == Defines.UINT8_SIZE) {
				value = dis.readByte() & 0xff;
			} else {
				// Defines.UINT16_SIZE
				byte[] buffer = new byte [size];
				int bytesRead = dis.read(buffer);
				if (bytesRead != size) {
					throw new IOException("Unexpected End of Stream");
				}

				value = ByteBuffer.wrap(buffer).order(ENDIAN).getShort() & 0xffff;
			}

		} catch (IOException e) {
			System.out.println (e);
			e.printStackTrace();
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					System.out.println (e);
					e.printStackTrace();
				}
			}
		}

		return value;
	}

	public static long getLongValue (byte[] data, int size, ByteOrder ENDIAN, int off) {
		if (size != Defines.UINT32_SIZE) {
			throw new IllegalArgumentException ("invalid argument.");
		}

		long value = 0;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream (new ByteArrayInputStream(data, off, data.length));
			byte[] buffer = new byte [size];
			int bytesRead = dis.read(buffer);
			if (bytesRead != size) {
				throw new IOException("Unexpected End of Stream");
			}

			value = ByteBuffer.wrap(buffer).order(ENDIAN).getInt() & 0xffffffff;

		} catch (IOException e) {
			System.out.println (e);
			e.printStackTrace();
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					System.out.println (e);
					e.printStackTrace();
				}
			}
		}

		return value;
	}

}
