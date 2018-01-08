#ifndef _CAP_JNI_LISTENER_H_
#define _CAP_JNI_LISTENER_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <pthread.h>

#include <map>
#include <vector>
#include <mutex>

#include <jni.h>

#include "Cap.h"


using namespace std;


#define LISTENER_ID_MAX		(10)

typedef struct listener_info {
	listener_info (void)
		:id (-1)
		,listener (NULL)
		,argClass (NULL)
	{};
	~listener_info (void) {};

	int id;
	jobject listener;
	jclass argClass;

} ST_LISTENER_INFO;


class CCapJniListener : public CCap::IPacketHandler
{
private:
	CCapJniListener (void);
	virtual ~CCapJniListener (void);

public:
	static CCapJniListener* getInstance (void);

	void setGlobalJavaVM (JNIEnv *env);
	JavaVM* getGlobalJavaVM (void);

	int registerListener (JNIEnv *env, jobject listener);
	bool unregisterListener (JNIEnv *env, int id);


private:
	bool hasListenerClient (int id);
	bool hasListener (void);

	int allocateListenerId (void);
	void releaseListenerId (int id);

	void onReceivedPacket (const ST_PACKET_INFO *pInfo);


	JavaVM* mpGlobalJavaVM;

	int mListenerId;
	bool mListenerIdInd [0x10];

	map<int, ST_LISTENER_INFO> mListenerMap;
	std::recursive_mutex mMutexListenerMap;

};

#endif
