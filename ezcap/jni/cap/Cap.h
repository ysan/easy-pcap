#ifndef _CAP_H_
#define _CAP_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include <netinet/in.h>
#include <netpacket/packet.h>
#include <net/ethernet.h>
#include <net/if.h>
#include <netinet/if_ether.h>
#include <netinet/ip.h>
#include <netinet/ip_icmp.h>
#include <sys/ioctl.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>

#include <pcap.h>
#include <pcap/pcap.h>

#include <vector>
#include <memory>


#include "ProxyThread.h"


using namespace ProxyThread;


#define INTERFACE_NAME_LEN		(32)
#define FILTER_STRING_LEN		(512)

typedef enum {
	EN_MSG_START_CAPTURE = 0,
} EN_MSG;

typedef struct _interface {
	_interface (void)
		:pName (NULL)
		,pDesc (NULL)
		,isLoopback (false)
		,pAddr (NULL)
		,pNetmask (NULL)
		,pBroadAddr (NULL)
		,pDestAddr (NULL)
		,pAddr_v6 (NULL)
		,pNetmask_v6 (NULL)
		,pBroadAddr_v6 (NULL)
		,pDestAddr_v6 (NULL)
	{
//		puts("new");
	}
	~_interface (void) {
//		puts("del");
	}

	char *pName;
	char *pDesc;
	bool isLoopback;

	struct sockaddr_in* pAddr;
	struct sockaddr_in* pNetmask;
	struct sockaddr_in* pBroadAddr;
	struct sockaddr_in* pDestAddr;
	
	struct sockaddr_in* pAddr_v6;
	struct sockaddr_in* pNetmask_v6;
	struct sockaddr_in* pBroadAddr_v6;
	struct sockaddr_in* pDestAddr_v6;

} ST_INTERFACE;

//typedef vector<unique_ptr<ST_INTERFACE>> VEC_INTERFACE;

typedef struct _packet_info {
	struct timeval ts; // time stamp
	const unsigned char *pRaw;
	int len;
	bool isValid;
} ST_PACKET_INFO;


class CCap : public CProxyThread
{
public:
	class IPacketHandler
	{
	public:
		virtual ~IPacketHandler (void) {};
		virtual void onReceivedPacket (const ST_PACKET_INFO *pInfo) = 0;
	};

private:
	CCap (void);
	virtual ~CCap (void);

public:
	static CCap* getInstance (void);

	void setPacketHandler (IPacketHandler *handler);
	IPacketHandler* getPacketHandler (void);

	bool createCbThread (void);
	void destroyCbThread (void);

	const char* getVersion (void);
	void setInterface (const char* p, int len);
	bool setFilter (const char* p, int len);
	bool setFilter (void);
	char* getFilter (void);
	bool clearFilter (void);

	bool open (void);
	void close (void);

	// live caputure
	void start (void);
	void stop (void);

	bool findDevs (void); // pcap_findalldevs
	void unrefDevs (void); // pcap_freealldevs
	void getInterfaceList (vector<unique_ptr<ST_INTERFACE>> *pIfList);
	unique_ptr<vector<unique_ptr<ST_INTERFACE>>> getInterfaceList (void);
	void dumpInterfaceList (vector<unique_ptr<ST_INTERFACE>> *pIfList) const;


private:
	void onExec (int msg, const ST_DATA *pData);
	static void cbRecvPacket (unsigned char *p, const struct pcap_pkthdr *pkthdr, const unsigned char *packet);


	pcap_t *mpCap;
	struct bpf_program mBpfProg;
	char mInterface [INTERFACE_NAME_LEN];
	char mFilter [FILTER_STRING_LEN];
	pcap_if_t *mpIf;

	IPacketHandler *mpPacketHandler;

};

#endif
