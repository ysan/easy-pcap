easy pcap
===============

libpcap wrapper (study to overcome the painful jni.) 


How to build
--------
JAVA ant build

	$ cd easy-pcap/ezcap
	$ ant

JNI build

	$ cd easy-pcap/ezcap/jni
	$ make

It may be necessary to adjust the include path of jni.h to your environment.  
Please edit the Makefile.

	(snip)
	CFLAGS      := -Wall -O0 -MD -std=c++11
	INCLUDES    := \
		-I./ \
		-I./cap \
		-I/usr/lib/jvm/java-1.8.0/include \        <-- modify here
		-I/usr/lib/jvm/java-1.8.0/include/linux \  <-- modify here
	
	LIBS        := -lpthread -lpcap
	(snip)

Usage
--------
Please specify the target interface name after starting run.sh.

	$ cd easy-pcap/ezcap
	$ sudo ./run.sh
	.
	.
	.
	Enter interface name: eth0   <-- Please specify the target interface name
	[eth0]
	listener id:0
	### start ###
	mReqQueueVector.size()=[1]
	mReqQueueVector.erase
	pcap_loop start. if=enp24s0
	ezcap >                      <-- console start

capture start and console start.

Console command
------------
	start
&nbsp;&nbsp;&nbsp;&nbsp;capture start/restart

	stop
&nbsp;&nbsp;&nbsp;&nbsp;capture stop

	quit
&nbsp;&nbsp;&nbsp;&nbsp;process exit

	setf expression
&nbsp;&nbsp;&nbsp;&nbsp;set pcap filter ***expression***   
&nbsp;&nbsp;&nbsp;&nbsp;selects which packets will be dumped. If no expression is given, all packets on  
&nbsp;&nbsp;&nbsp;&nbsp;the net will be dumped. Otherwise, only packets for which expression is true  
&nbsp;&nbsp;&nbsp;&nbsp;will be dumped.

	clearf
&nbsp;&nbsp;&nbsp;&nbsp;clear pcap filter

Platforms
------------
Generic Linux will be ok. (confirmed worked on Fedora20)  
Require is libpcap.
