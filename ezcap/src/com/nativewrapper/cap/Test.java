package com.nativewrapper.cap;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.nativewrapper.cap.CapJni;
import com.nativewrapper.cap.IPacketListener;
import com.nativewrapper.cap.Interface;
import com.nativewrapper.cap.Packet;
import com.nativewrapper.cap.protocols.*;
import com.nativewrapper.types.*;


public class Test {
	
	static private CapJni mCap = new CapJni ();
	static private IPacketListener pktListener = new IPacketListener () {
		@Override
		public void onReceivedPacket (Packet packet) {
			System.out.println ("--------------------------");
			System.out.println (packet);

			EtherHeader eh = new EtherHeader();
			RawReader.toStruct (eh, packet.getRaw(), 0);
			System.out.println (eh);

			if (eh.getType() == EtherType.ETHERTYPE_ARP.getType()) {
				EtherArp ea = new EtherArp();
				RawReader.toStruct (ea, packet.getRaw(), eh.length());
				System.out.println (ea);

			} else if (eh.getType() == EtherType.ETHERTYPE_IP.getType()) {
				Ip ip = new Ip();
				RawReader.toStruct (ip, packet.getRaw(), eh.length());
				System.out.println (ip);
			}

			System.out.println ("hexDump:");
			RawReader.hexDump (packet.getRaw());
		}
	};


	public static void main (String[] args) {

		BufferedReader stdReader = new BufferedReader (new InputStreamReader(System.in));

		if (!mCap.init()) {
			System.exit(1);
		}

		List<Interface> ifList = mCap.getInterfaceList();
		if (ifList.size() == 0) {
			System.exit(1);
		} else {
			for (int i = 0; i < ifList.size(); ++ i) {
				System.out.println ("nic" + i + ": " + ifList.get(i).getName() + " " + ifList.get(i).getAddress());
			}
		}

		System.out.print ("\nenter network interface: ");
		String nic = "";
		try {
			while (true) { 
				nic = stdReader.readLine();
				if (nic.equals("")) {
					continue;
				} else {
					System.out.println ("[" + nic + "]");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		mCap.setInterface (nic);
//		mCap.setFilter ("tcp");

		int id = mCap.registerPacketListener (pktListener);
		System.out.println ("listener id:" + id);


		String line = "";
		try {
			while (true) { 
				line = stdReader.readLine();
				if (line.equals("")) {

				} else if (line.equals("q")) {
					System.out.println ("### quit ###");
					break;

				} else if (line.equals("start")) {
					System.out.println ("### start ###");
					if (!mCap.start()) {
						System.exit(1);
					}

				} else if (line.equals("stop")) {
					System.out.println ("### stop ###");
					mCap.stop();

				} else if (line.equals("clear")) {
					System.out.println ("### clear filter ###");
					mCap.clearFilter();

				} else  {
					System.out.println ("### set filter ### --> [" + line + "]");
					mCap.setFilter (line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		mCap.stop();
		mCap.unregisterPacketListener (id);
		mCap.fin();

	}
}
