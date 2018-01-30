libpcap wrapper
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

How to execute
--------

	$ cd easy-pcap/ezcap
	$ sudo ./run.sh
	
Platforms
------------
Linux generally will be ok. (confirmed worked on Fedora20)

