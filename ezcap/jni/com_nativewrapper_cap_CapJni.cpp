#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include <mutex>

#include "com_nativewrapper_cap_CapJni.h"
#include "CapJniListener.h"
#include "Cap.h"


using namespace std;

std::mutex g_mutex;

static void convCharArrayToJstring (JNIEnv* env, const char* p, jstring &jstrout);
static void convJstringToCharArray (JNIEnv* env, jstring jstrin, char* pout, int size);

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeInit
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_nativewrapper_cap_CapJni_nativeInit
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	CCapJniListener *pCapJL = CCapJniListener::getInstance();

	if (!pCap->createCbThread()) {
		return false;
	}

	pCapJL->setGlobalJavaVM (env);
	pCap->setPacketHandler (pCapJL);

	return true;
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeFin
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_nativewrapper_cap_CapJni_nativeFin
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	pCap->stop ();
	pCap->close();
	pCap->clearFilter ();
	pCap->destroyCbThread();
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeGetVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_nativewrapper_cap_CapJni_nativeGetVersion
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	const char *pVer = pCap->getVersion();
	if (!pVer) {
		return (jstring)NULL;
	}

	jstring jstrVer = NULL;
	convCharArrayToJstring (env, pVer, jstrVer);

	return jstrVer;
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeSetInterface
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_nativewrapper_cap_CapJni_nativeSetInterface
  (JNIEnv *env, jobject thiz, jstring name)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();

	char szName [32] = {0};
	convJstringToCharArray (env, name, szName, sizeof(szName));

	pCap->setInterface (szName, strlen(szName));
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeSetFilter
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_nativewrapper_cap_CapJni_nativeSetFilter
  (JNIEnv *env, jobject thiz, jstring filter)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();

	char szFilter [256] = {0};
	convJstringToCharArray (env, filter, szFilter, sizeof(szFilter));

	return pCap->setFilter (szFilter, strlen(szFilter));
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeGetFilter
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_nativewrapper_cap_CapJni_nativeGetFilter
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	const char *pFilter = pCap->getFilter();
	if (!pFilter) {
		return (jstring)NULL;
	}

	jstring jstrFilter = NULL;
	convCharArrayToJstring (env, pFilter, jstrFilter);

	return jstrFilter;
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeClearFilter
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_nativewrapper_cap_CapJni_nativeClearFilter
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	return pCap->clearFilter ();
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeStart
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_nativewrapper_cap_CapJni_nativeStart
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	if (!pCap->open()) {
		return false;
	}

	pCap->setFilter();
	pCap->start ();

	return true;
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeStop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_nativewrapper_cap_CapJni_nativeStop
  (JNIEnv *env, jobject thiz)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	pCap->stop ();
	pCap->close();
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeRegisterPacketListener
 * Signature: (Lcom/nativewrapper/cap/IPacketListener;)I
 */
JNIEXPORT jint JNICALL Java_com_nativewrapper_cap_CapJni_nativeRegisterPacketListener
  (JNIEnv *env, jobject thiz, jobject listener)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCapJniListener *pCapJL = CCapJniListener::getInstance();
	int rtn = pCapJL->registerListener (env, listener);
	if (rtn < 0) {
		return -1;
	} else {
		return rtn;
	}
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeUnregisterPacketListener
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_nativewrapper_cap_CapJni_nativeUnregisterPacketListener
  (JNIEnv *env, jobject thiz, jint id)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCapJniListener *pCapJL = CCapJniListener::getInstance();
	return pCapJL->unregisterListener (env, (int)id);
}

/*
 * Class:     com_nativewrapper_cap_CapJni
 * Method:    nativeGetInterfaceList
 * Signature: (Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_nativewrapper_cap_CapJni_nativeGetInterfaceList
  (JNIEnv * env, jobject thiz, jobject outList)
{
	std::lock_guard<std::mutex> lock (g_mutex);

	CCap *pCap = CCap::getInstance();
	if (!pCap->findDevs()) {
		return 0;
	}

	unique_ptr<vector<unique_ptr<ST_INTERFACE>>> listIf = pCap->getInterfaceList();
	if (listIf.get()->size() == 0) {
		pCap->unrefDevs();
		return 0;
	}

	jint num = 0;
	jclass cl_interface = env->FindClass ("com/nativewrapper/cap/Interface");
	jmethodID ctorId_interface = env->GetMethodID (cl_interface, "<init>", "()V");

	jfieldID fid1 = env->GetFieldID (cl_interface, "mName", "Ljava/lang/String;");
	jfieldID fid2 = env->GetFieldID (cl_interface, "mDesc", "Ljava/lang/String;");
	jfieldID fid3 = env->GetFieldID (cl_interface, "mIsLoopback", "Z");
	jfieldID fid4 = env->GetFieldID (cl_interface, "mAddr", "Ljava/lang/String;");
	jfieldID fid5 = env->GetFieldID (cl_interface, "mNetmask", "Ljava/lang/String;");
	jfieldID fid6 = env->GetFieldID (cl_interface, "mBroadAddr", "Ljava/lang/String;");
	jfieldID fid7 = env->GetFieldID (cl_interface, "mDestAddr", "Ljava/lang/String;");
	jfieldID fid8 = env->GetFieldID (cl_interface, "mAddr_v6", "Ljava/lang/String;");
	jfieldID fid9 = env->GetFieldID (cl_interface, "mNetmask_v6", "Ljava/lang/String;");
	jfieldID fid10 = env->GetFieldID (cl_interface, "mBroadAddr_v6", "Ljava/lang/String;");
	jfieldID fid11 = env->GetFieldID (cl_interface, "mDestAddr_v6", "Ljava/lang/String;");


	struct sockaddr_in* p = NULL;
	char szAdrstr [64] = {0};
	vector<unique_ptr<ST_INTERFACE>>::iterator iter = listIf.get()->begin();

	while (iter != listIf.get()->end()) {
		ST_INTERFACE *pRaw = iter->get();

		jstring jstrName = NULL;
		jstring jstrDesc = NULL;
		jstring jstrAddr = NULL;
		jstring jstrNetmask = NULL;
		jstring jstrBroadAddr = NULL;
		jstring jstrDestAddr = NULL;
		jstring jstrAddr_v6 = NULL;
		jstring jstrNetmask_v6 = NULL;
		jstring jstrBroadAddr_v6 = NULL;
		jstring jstrDestAddr_v6 = NULL;

		convCharArrayToJstring (env, pRaw->pName, jstrName);
		if (pRaw->pDesc && (strlen(pRaw->pDesc) > 0)) {
			convCharArrayToJstring (env, pRaw->pDesc, jstrDesc);
		}
		p = pRaw->pAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrAddr);
			}
		}
		p = pRaw->pNetmask;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrNetmask);
			}
		}
		p = pRaw->pBroadAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrBroadAddr);
			}
		}
		p = pRaw->pDestAddr;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrDestAddr);
			}
		}
		p = pRaw->pAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrAddr_v6);
			}
		}
		p = pRaw->pNetmask_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrNetmask_v6);
			}
		}
		p = pRaw->pBroadAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrBroadAddr_v6);
			}
		}
		p = pRaw->pDestAddr_v6;
		if (p) {
			memset (szAdrstr, 0x00, sizeof(szAdrstr));
			inet_ntop (p->sin_family, &(p->sin_addr.s_addr), szAdrstr, sizeof(szAdrstr));
			if (strlen(szAdrstr) > 0) {
				convCharArrayToJstring (env, szAdrstr, jstrDestAddr_v6);
			}
		}

  
		jobject obj_interface = env->NewObject (cl_interface, ctorId_interface);
		env->SetObjectField (obj_interface, fid1, jstrName);
		if (jstrDesc) {
			env->SetObjectField (obj_interface, fid2, jstrDesc);
		}
		env->SetBooleanField (obj_interface, fid3, pRaw->isLoopback);
		if (jstrAddr) {
			env->SetObjectField (obj_interface, fid4, jstrAddr);
		}
		if (jstrNetmask) {
			env->SetObjectField (obj_interface, fid5, jstrNetmask);
		}

		if (jstrBroadAddr) {
			env->SetObjectField (obj_interface, fid6, jstrBroadAddr);
		}

		if (jstrDestAddr) {
			env->SetObjectField (obj_interface, fid7, jstrDestAddr);
		}

		if (jstrAddr_v6) {
			env->SetObjectField (obj_interface, fid8, jstrAddr_v6);
		}

		if (jstrNetmask_v6) {
			env->SetObjectField (obj_interface, fid9, jstrNetmask_v6);
		}

		if (jstrBroadAddr_v6) {
			env->SetObjectField (obj_interface, fid10, jstrBroadAddr_v6);
		}

		if (jstrDestAddr_v6) {
			env->SetObjectField (obj_interface, fid11, jstrDestAddr_v6);
		}


		jclass cl_arrlist = env->FindClass ("java/util/ArrayList");
		jmethodID methodId = env->GetMethodID (cl_arrlist, "add", "(Ljava/lang/Object;)Z");
		env->CallBooleanMethod (outList, methodId, obj_interface);


		env->DeleteLocalRef(jstrName);
		if (jstrDesc) {
			env->DeleteLocalRef(jstrDesc);
		}
		if (jstrAddr) {
			env->DeleteLocalRef(jstrAddr);
		}
		if (jstrNetmask) {
			env->DeleteLocalRef(jstrNetmask);
		}
		if (jstrBroadAddr) {
			env->DeleteLocalRef(jstrBroadAddr);
		}
		if (jstrDestAddr) {
			env->DeleteLocalRef(jstrDestAddr);
		}
		if (jstrAddr_v6) {
			env->DeleteLocalRef(jstrAddr_v6);
		}
		if (jstrNetmask_v6) {
			env->DeleteLocalRef(jstrNetmask_v6);
		}
		if (jstrBroadAddr_v6) {
			env->DeleteLocalRef(jstrBroadAddr_v6);
		}
		if (jstrDestAddr_v6) {
			env->DeleteLocalRef(jstrDestAddr_v6);
		}
		env->DeleteLocalRef(obj_interface);
		env->DeleteLocalRef(cl_arrlist);

		++ num;
		++ iter ;
	}

	env->DeleteLocalRef (cl_interface);
	pCap->unrefDevs ();

	return num;
}

void convCharArrayToJstring (JNIEnv* env, const char* pin, jstring &jstrout)
{
	jbyteArray bytes = env->NewByteArray (strlen(pin));
	env->SetByteArrayRegion (bytes, 0, strlen(pin), (jbyte*)pin);

	jclass cl = env->FindClass ("java/lang/String");
	jmethodID ctorId = env->GetMethodID (cl, "<init>", "([B)V");
	jstrout = (jstring) env->NewObject (cl, ctorId, bytes);

	env->DeleteLocalRef(cl);
	env->DeleteLocalRef(bytes);
}

void convJstringToCharArray (JNIEnv* env, jstring jstrin, char* pout, int size)
{
	jstring encode = env->NewStringUTF ("utf-8");

	jclass cl = env->FindClass ("java/lang/String");
	jmethodID metId = env->GetMethodID (cl, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)env->CallObjectMethod (jstrin, metId, encode);

	jsize alen = env->GetArrayLength (barr);
	jbyte* ba = env->GetByteArrayElements (barr, JNI_FALSE);
	if (alen > 0) {
		memcpy (pout, ba, (alen > size) ? size : alen);
		size = (alen > size) ? size : alen;
	}

	env->DeleteLocalRef (encode);
	env->ReleaseByteArrayElements(barr, ba, JNI_FALSE);
}
