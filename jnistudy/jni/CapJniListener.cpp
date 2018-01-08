#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include "CapJniListener.h"


CCapJniListener::CCapJniListener (void)
	:mpGlobalJavaVM (NULL)
	,mListenerId (-1)
{
	for (int i = 0; i < LISTENER_ID_MAX; ++ i) {
		mListenerIdInd [i] = false;
	}
}

CCapJniListener::~CCapJniListener (void)
{
}

CCapJniListener *CCapJniListener::getInstance (void)
{
	static CCapJniListener singleton;
	return &singleton;
}

void CCapJniListener::setGlobalJavaVM (JNIEnv *env)
{
	if (env) {
		env->GetJavaVM (&mpGlobalJavaVM);
	}
}

JavaVM* CCapJniListener::getGlobalJavaVM (void)
{
	return mpGlobalJavaVM;
}

int CCapJniListener::registerListener (JNIEnv *env, jobject listener)
{
	std::lock_guard<std::recursive_mutex> lock (mMutexListenerMap);

	int id = allocateListenerId ();
	if (id == -1) {
		return -1;
	}

	jclass clArg = env->FindClass ("com/jnistudy/cap/Packet");

	ST_LISTENER_INFO stInfo;
	stInfo.id = id;
	stInfo.listener = env->NewGlobalRef (listener);
	stInfo.argClass = (jclass)env->NewGlobalRef (clArg);
	mListenerMap.insert (pair<int, ST_LISTENER_INFO>(id, stInfo));

	env->DeleteLocalRef (clArg);

	return id;
}

bool CCapJniListener::unregisterListener (JNIEnv *env, int id)
{
	std::lock_guard<std::recursive_mutex> lock (mMutexListenerMap);

	map <int, ST_LISTENER_INFO>::iterator iter = mListenerMap.find (id);
	if (iter != mListenerMap.end()) {
		env->DeleteGlobalRef (iter->second.listener);
		env->DeleteGlobalRef (iter->second.argClass);
		mListenerMap.erase (id);
		releaseListenerId (id);
		return true;

	} else {
		return false;
	}
}

bool CCapJniListener::hasListenerClient (int id)
{
	std::lock_guard<std::recursive_mutex> lock (mMutexListenerMap);

	if (mListenerMap.find(id) != mListenerMap.end()) {
		return true;
	} else {
		return false;
	}
}

bool CCapJniListener::hasListener (void)
{
	std::lock_guard<std::recursive_mutex> lock (mMutexListenerMap);

	bool rtn = false;
	map <int, ST_LISTENER_INFO>::iterator iter = mListenerMap.begin();
	if (iter != mListenerMap.end()) {
		rtn = true;
	}

	return rtn;
}

int CCapJniListener::allocateListenerId (void)
{
	int i = 0;
	for (i = 0; i < LISTENER_ID_MAX; ++ i) {
		if (!mListenerIdInd[i]) {
			mListenerIdInd[i] = true;
			break;
		}
	}

	if (i == LISTENER_ID_MAX) {
		return -1;
	}
	
	mListenerId = i;
	return mListenerId;
}

void CCapJniListener::releaseListenerId (int id)
{
	if ((id < 0) || (id >= LISTENER_ID_MAX)) {
		return ;
	}

	mListenerIdInd [id] = false;
}

void CCapJniListener::onReceivedPacket (const ST_PACKET_INFO *pInfo)
{
	if (!pInfo) {
		return;
	}

	if (!CCapJniListener::getInstance()->hasListener()) {
		return;
	}

	JNIEnv *env = NULL;
	if (getGlobalJavaVM()) {
		getGlobalJavaVM()->AttachCurrentThread ((void**)&env, NULL);
	} else {
		printf ("Err: JavaVM is NULL\n");
		return;
	}


	map <int, ST_LISTENER_INFO>::iterator iter = mListenerMap.begin();
	while (iter != mListenerMap.end()) {
		jobject listener = iter->second.listener;
		jclass clArg = iter->second.argClass;

		// new argobj
		jmethodID argCtorId = env->GetMethodID (clArg, "<init>", "()V");
		jobject objArg = env->NewObject (clArg, argCtorId);

		jfieldID fidArg1 = env->GetFieldID (clArg, "mTimestamp", "Ljava/util/Date;");
		jfieldID fidArg2 = env->GetFieldID (clArg, "mRaw", "[B");
		jfieldID fidArg3 = env->GetFieldID (clArg, "mLength", "I");


		// mTimestamp - Date
		jclass clDate = env->FindClass ("java/util/Date");
		jmethodID ctorIdDate = env->GetMethodID (clDate, "<init>", "(J)V");
		jobject objDate = env->NewObject (clDate, ctorIdDate, (pInfo->ts.tv_sec * 1000) + (pInfo->ts.tv_usec / 1000));
		env->SetObjectField (objArg, fidArg1, objDate);

		// mRaw - byte array
		jbyteArray jbary = env->NewByteArray (pInfo->len);
		jbyte *jb = env->GetByteArrayElements (jbary, NULL);
		memcpy (jb, pInfo->pRaw, pInfo->len);
		env->ReleaseByteArrayElements (jbary, jb, 0);
		env->SetObjectField (objArg, fidArg2, jbary);

		// mLength - int
		env->SetIntField (objArg, fidArg3, (jint)pInfo->len);


		// callback listener
		jclass cl = env->GetObjectClass (listener);
		jmethodID mid = env->GetMethodID (cl, "onReceivedPacket", "(Lcom/jnistudy/cap/Packet;)V" );
		env->CallVoidMethod (listener, mid, objArg);

		env->DeleteLocalRef (objArg);

		++ iter ;
	}


	if (getGlobalJavaVM()) {
		getGlobalJavaVM()->DetachCurrentThread ();
	}
}
