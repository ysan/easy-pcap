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

					} else if (annotation.annotationType().equals(StructSize.class)) {
						// StructSize.class must be last
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

	private static int getIntValue (byte[] data, int size, ByteOrder ENDIAN, int off) {
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

	private static long getLongValue (byte[] data, int size, ByteOrder ENDIAN, int off) {
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

	public static void hexDump (String s) {
		if (s == null || s.isEmpty()) {
			return ;
		}

		byte [] arr = s.getBytes ();
		hexDump (arr);
	}

	public static void hexDump (byte [] inArr) {
		if ((inArr == null) || (inArr.length == 0)) {
			return;
		}

		byte [] arr = inArr; 

		int remain = arr.length;
		int line = 0;
		while (remain >= 16) {
			//line header
			System.out.print (String.format ("0x%08x: ", line));
			
			String lineStr = String.format (
					"%02x %02x %02x %02x %02x %02x %02x %02x  %02x %02x %02x %02x %02x %02x %02x %02x", 
					arr[line*16+0], arr[line*16+1], arr[line*16+2], arr[line*16+3], arr[line*16+4], arr[line*16+5], arr[line*16+6], arr[line*16+7],
					arr[line*16+8], arr[line*16+9], arr[line*16+10], arr[line*16+11], arr[line*16+12], arr[line*16+13], arr[line*16+14], arr[line*16+15]
					);
			System.out.print (lineStr);
			System.out.print ("  ");
			
			// ascii
			System.out.print ("|");
			for (int k = 0; k < 16; ++ k) {
				System.out.print (String.format ("%c", (arr[line*16+k] > 0x1f) && (arr[line*16+k] < 0x7f) ? arr[line*16+k] : '.'));
			}
			System.out.print ("|");
			System.out.println ();
			
			++ line;
			remain -= 16;
		}
		
		int j = 0;
		if (remain > 0) {
			// line header
			System.out.print (String.format ("0x%08x: ", line));
			
			while (j < 16) {
				if (j < remain) {
					System.out.print (String.format("%02x", arr[line*16+j]));

					if (j == 7) {
						System.out.print ("  ");
					} else if (j == 15) {
					} else {
						System.out.print (" ");
					}
					
				} else {
					System.out.print ("  ");

					if (j == 7) {
						System.out.print ("  ");
					} else if (j == 15) {
					} else {
						System.out.print (" ");
					}
				}

				++ j;
			}
			
			// ascii
			System.out.print ("  ");
			System.out.print ("|");
			int k = 0;
			while (k < remain) {
				System.out.print (String.format ("%c", (arr[line*16+k] > 0x1f) && (arr[line*16+k] < 0x7f) ? arr[line*16+k] : '.'));
				++ k;
			}
			for (int i = 0; i < (16 - remain); ++ i) {
				System.out.print (" ");
			}
			System.out.print ("|");
		}
		
		System.out.println();
	}

}
