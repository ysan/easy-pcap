#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include "Cap.h"



CCap::CCap (void)
	:mpCap (NULL)
	,mpIf (NULL)
	,mpPacketHandler (NULL)
{
	memset (&mBpfProg, 0x00, sizeof (mBpfProg));
	memset (mInterface, 0x00, sizeof (mInterface));
	memset (mFilter, 0x00, sizeof (mFilter));
}

CCap::~CCap (void)
{
}

CCap* CCap::getInstance (void)
{
	static CCap singleton;
	return &singleton;
}

void CCap::setPacketHandler (IPacketHandler *pHandler)
{
	if (pHandler) {
		mpPacketHandler = pHandler;
	}
}

CCap::IPacketHandler* CCap::getPacketHandler (void)
{
	return mpPacketHandler;
}

bool CCap::createCbThread (void)
{
	return create ();
}

void CCap::destroyCbThread (void)
{
	reqDestroy();
	waitDestroy();
}

void CCap::setInterface (const char* p, int len)
{
	if (p && (len > 0)) {
		memset (mInterface, 0x00, sizeof(mInterface));
		strncpy (mInterface, p, len > (INTERFACE_NAME_LEN -1) ? (INTERFACE_NAME_LEN -1) : len);
	}
}

bool CCap::setFilter (const char* p, int len)
{
	if (!p || (len == 0)) {
		return false;
	}

	// only members(mFilter) set
	memset (mFilter, 0x00, sizeof(mFilter));
	strncpy (mFilter, p, len > (FILTER_STRING_LEN -1) ? (FILTER_STRING_LEN -1) : len);

	if ((mpCap) && (strlen(mFilter) > 0)) {
		if (!setFilter()) {
			return false;
		}
	}

	return true;
}

bool CCap::setFilter (void)
{
	if (!mpCap) {
		return false;
	}

	if (pcap_compile (mpCap, &mBpfProg, mFilter, 0, PCAP_NETMASK_UNKNOWN) == -1) {
		printf ("Could not parse filter. if=%s, error=%s\n", mInterface, pcap_geterr(mpCap));
		return false;
	}

	if (pcap_setfilter (mpCap, &mBpfProg) == -1) {
		printf("Could not install filter. if=%s, error=%s\n", mInterface, pcap_geterr(mpCap));
		return false;
	}

	return true;
}

bool CCap::clearFilter (void)
{
	pcap_freecode (&mBpfProg);
	memset (&mBpfProg, 0x00, sizeof(mBpfProg));
	memset (mFilter, 0x00, sizeof(mFilter));

	return setFilter ();
}

bool CCap::open (void)
{
	if ((int)strlen(mInterface) == 0) {
		printf ("mInterface is not set.\n");
		return false;
	}
	if (mpCap) {
		printf ("already open.\n");
		return true;
	}


	char errbuf [PCAP_ERRBUF_SIZE] = {0};

	// promiscuous mode
	// wait forever
	mpCap = pcap_open_live (mInterface, 65535, true, 10, errbuf);
	if (!mpCap) {
		printf ("Could not open device. if=%s, error=%s\n", mInterface, errbuf);
		return false;
	}

//	printf ("pcap_snapshot %d\n", pcap_snapshot (mpCap));
//
//	bpf_u_int32 addr = 0;
//	bpf_u_int32 netmask = 0;
//	char szAdrstr [32] = {0};
//	if (pcap_lookupnet (mInterface, &addr, &netmask, errbuf) == 0) {
//		memset (szAdrstr, 0x00, sizeof(szAdrstr));
//		printf ("netaddr[%s]\n", inet_ntop(AF_INET, &addr, szAdrstr, sizeof(szAdrstr)));
//		memset (szAdrstr, 0x00, sizeof(szAdrstr));
//		printf ("netmask[%s]\n", inet_ntop(AF_INET, &netmask, szAdrstr, sizeof(szAdrstr)));
//	}

	return true;
}

void CCap::close (void)
{
	if (!mpCap) {
		return ;
	}

	pcap_close (mpCap);
	mpCap = NULL;
}

void CCap::start (void)
{
	if (!isAlive()) {
		printf ("callback thread is not up.\n");
		return;
	}

	ST_REQ_QUEUE *p = getNowQueue ();
	if (p) {
		if (p->isUsed && (p->msg == EN_MSG_START_CAPTURE)) {
			printf ("now capturing...\n");
			return;
		}
	}

	request (EN_MSG_START_CAPTURE);
}

void CCap::stop (void)
{
	if (!mpCap) {
		return ;
	}

	struct pcap_stat ps = {0};
	if (pcap_stats (mpCap, &ps) == 0) {
		printf ("%d packets received.\n", ps.ps_recv);
		printf ("%d packets dropped.\n", ps.ps_drop);
	}

	pcap_breakloop (mpCap);
}

bool CCap::findDevs (void)
{
	if (!mpIf) {
		char errbuf [PCAP_ERRBUF_SIZE] = {0};
		int rtn = pcap_findalldevs (&mpIf, errbuf);
		if (rtn < 0) {
			printf ("pcapErr: %s\n", errbuf);
			return false;
		}
		if (!mpIf) {
			printf ("Err: mpIf is null.\n");
			return false;
		}
	}

	return true;
}

void CCap::unrefDevs (void)
{
	if (mpIf) {
		pcap_freealldevs (mpIf);
		mpIf = NULL;
	}
}

void CCap::getInterfaceList (vector<unique_ptr<ST_INTERFACE>> *pIfList)
{
	if (!pIfList) {
		return;
	}

	pcap_if_t *pIf = NULL;

	if (mpIf) {
		pIf = mpIf;

	} else {
		printf ("Err: please do after running findDevs.\n");
		return;
	}

	pcap_addr_t *pAdr = NULL;
	struct sockaddr_in* p = NULL;

	while (pIf) {
		if (!pIf->name || (strlen(pIf->name) == 0)) {
			continue;
		}

		unique_ptr<ST_INTERFACE> upIf (new ST_INTERFACE());

		upIf.get()->pName = pIf->name;
		upIf.get()->pDesc = pIf->description;
		upIf.get()->isLoopback = (pIf->flags == PCAP_IF_LOOPBACK) ? true: false;

		pAdr = pIf->addresses;
		while (pAdr) {
			if (pAdr->addr) {
				p = (struct sockaddr_in*)pAdr->addr;
				if ((p->sin_family != PF_INET) && (p->sin_family != PF_INET6)) {
					pAdr = pAdr->next;
					continue;
				}
			} else {
				pAdr = pAdr->next;
				continue;
			}

			if (pAdr->addr) {
				p = (struct sockaddr_in*)pAdr->addr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pAddr = (struct sockaddr_in*)pAdr->addr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pAddr_v6 = (struct sockaddr_in*)pAdr->addr;
				}
			}
			if (pAdr->netmask) {
				p = (struct sockaddr_in*)pAdr->netmask;
				if (p->sin_family == PF_INET) {
					upIf.get()->pNetmask = (struct sockaddr_in*)pAdr->netmask;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pNetmask_v6 = (struct sockaddr_in*)pAdr->netmask;
				}
			}
			if (pAdr->broadaddr) {
				p = (struct sockaddr_in*)pAdr->broadaddr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pBroadAddr = (struct sockaddr_in*)pAdr->broadaddr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pBroadAddr_v6 = (struct sockaddr_in*)pAdr->broadaddr;
				}
			}
			if (pAdr->dstaddr) {
				p = (struct sockaddr_in*)pAdr->dstaddr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pDestAddr = (struct sockaddr_in*)pAdr->dstaddr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pDestAddr_v6 = (struct sockaddr_in*)pAdr->dstaddr;
				}
			}

			pAdr = pAdr->next;
		}

		pIf = pIf->next;

		pIfList->push_back (move(upIf));
	}
}

unique_ptr<vector<unique_ptr<ST_INTERFACE>>> CCap::getInterfaceList (void)
{
	unique_ptr<vector<unique_ptr<ST_INTERFACE>>> rtnVec (new vector<unique_ptr<ST_INTERFACE>>);

	pcap_if_t *pIf = NULL;

	if (mpIf) {
		pIf = mpIf;

	} else {
		printf ("Err: please do after running findDevs.\n");
		return rtnVec;
	}

	pcap_addr_t *pAdr = NULL;
	struct sockaddr_in* p = NULL;

	while (pIf) {
		if (!pIf->name || (strlen(pIf->name) == 0)) {
			continue;
		}

		unique_ptr<ST_INTERFACE> upIf (new ST_INTERFACE());

		upIf.get()->pName = pIf->name;
		upIf.get()->pDesc = pIf->description;
		upIf.get()->isLoopback = (pIf->flags == PCAP_IF_LOOPBACK) ? true: false;

		pAdr = pIf->addresses;
		while (pAdr) {
			if (pAdr->addr) {
				p = (struct sockaddr_in*)pAdr->addr;
				if ((p->sin_family != PF_INET) && (p->sin_family != PF_INET6)) {
					pAdr = pAdr->next;
					continue;
				}
			} else {
				pAdr = pAdr->next;
				continue;
			}

			if (pAdr->addr) {
				p = (struct sockaddr_in*)pAdr->addr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pAddr = (struct sockaddr_in*)pAdr->addr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pAddr_v6 = (struct sockaddr_in*)pAdr->addr;
				}
			}
			if (pAdr->netmask) {
				p = (struct sockaddr_in*)pAdr->netmask;
				if (p->sin_family == PF_INET) {
					upIf.get()->pNetmask = (struct sockaddr_in*)pAdr->netmask;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pNetmask_v6 = (struct sockaddr_in*)pAdr->netmask;
				}
			}
			if (pAdr->broadaddr) {
				p = (struct sockaddr_in*)pAdr->broadaddr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pBroadAddr = (struct sockaddr_in*)pAdr->broadaddr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pBroadAddr_v6 = (struct sockaddr_in*)pAdr->broadaddr;
				}
			}
			if (pAdr->dstaddr) {
				p = (struct sockaddr_in*)pAdr->dstaddr;
				if (p->sin_family == PF_INET) {
					upIf.get()->pDestAddr = (struct sockaddr_in*)pAdr->dstaddr;
				} else if (p->sin_family == PF_INET6) {
					upIf.get()->pDestAddr_v6 = (struct sockaddr_in*)pAdr->dstaddr;
				}
			}

			pAdr = pAdr->next;
		}

		pIf = pIf->next;

		rtnVec.get()->push_back (move(upIf));
	}
	return rtnVec;
}

void CCap::dumpInterfaceList (vector<unique_ptr<ST_INTERFACE>> *pIfList) const
{
	if (!pIfList) {
		return;
	}

	struct sockaddr_in* p = NULL;
	char szAdrstr [64] = {0};
	vector<unique_ptr<ST_INTERFACE>>::iterator iter = pIfList->begin();
	while (iter != pIfList->end()) {

		ST_INTERFACE *praw = iter->get(); // unique_ptr<ST_INTERFACE> ->get
		printf ("[%s] [%s] [%s]\n", praw->pName, praw->pDesc, praw->isLoopback ? "loopback": "");

		printf ("  ");
		p = praw->pAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pNetmask;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("netmask[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pBroadAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("broad addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pDestAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("dest addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		printf ("\n");

		printf ("  ");
		p = praw->pAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pNetmask_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("netmask[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pBroadAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("broad addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		p = praw->pDestAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			printf ("dest addr[%s]", inet_ntop(p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr)));
		}
		printf ("\n");

		++ iter ;
	}
}

void CCap::onExec (int msg, const ST_DATA *pData)
{
	switch (msg) {
	case EN_MSG_START_CAPTURE:
		if (strlen(mInterface) == 0) {
			printf ("mInterface is not set.\n");
			break ;
		}
		if (!mpCap) {
			printf ("mpCap is null.\n");
			break;
		}

		printf ("pcap_loop start. if=%s\n", mInterface);
		pcap_loop (mpCap, 0, cbRecvPacket, (unsigned char*)this);
		printf ("pcap_loop end. if=%s\n", mInterface);
		break;

	default:
		break;
	}
}

void CCap::cbRecvPacket (unsigned char *p, const struct pcap_pkthdr *pkthdr, const unsigned char *packet)
{
//	if (pkthdr->len != pkthdr->caplen) {
//		printf ("Warn: skip packet (pkthdr->len != pkthdr->caplen)\n");
//		return ;
//	}

	CCap *pCap = CCap::getInstance();
	IPacketHandler *pHandler = pCap->getPacketHandler();
	if (pHandler) {
		ST_PACKET_INFO stInfo = {
			pkthdr->ts,
			packet,
			(int)pkthdr->caplen,
			pkthdr->len == pkthdr->caplen ? true : false
		};

		pHandler->onReceivedPacket ((const ST_PACKET_INFO*)&stInfo);

	} else {
		printf (
			"%lu.%lu caplen:[%d] len:[%d]\n",
			pkthdr->ts.tv_sec,
			pkthdr->ts.tv_usec,
			pkthdr->caplen,
			pkthdr->len
		);
	}
}

