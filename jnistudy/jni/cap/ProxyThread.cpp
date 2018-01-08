#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <pthread.h>

#include "ProxyThread.h"


namespace ProxyThread {

CProxyThread::CProxyThread (void)
	:mIsJoinable (false)
	,mIsCreated (false)
	,mpNowReqQue (NULL)
{
	pthread_mutex_init (&mMutex, NULL);

	pthread_mutex_init (&mCondMutex, NULL);
	pthread_cond_init (&mCond, NULL);

	pthread_mutex_init (&mMutexReqQueue, NULL);
}

CProxyThread::~CProxyThread (void)
{
	pthread_mutex_destroy (&mMutex);

	pthread_mutex_destroy (&mCondMutex);
	pthread_cond_destroy (&mCond);

	pthread_mutex_destroy (&mMutexReqQueue);
}


bool CProxyThread::create (bool isJoinable)
{
	CScopedMutex scopedMutex (&mMutex);


	int rtn = 0;

	if (!mIsCreated) {
		mIsCreated = true;
		mIsJoinable = isJoinable;

		// create thread

		if (!isJoinable) {

			pthread_attr_t threadAttr;
			rtn = pthread_attr_init (&threadAttr);
			if (rtn != 0) {
				perror ("pthread_attr_init()");
				mIsCreated = false;
				return false;
			}

			rtn = pthread_attr_setdetachstate (&threadAttr, PTHREAD_CREATE_DETACHED);
			if (rtn != 0) {
				perror ("pthread_attr_setdetachstate()");
				mIsCreated = false;
				return false;
			}

			rtn = pthread_create (&mThreadId, &threadAttr, threadHandler, this);
			if (rtn != 0) {
				perror ("pthread_create()");
				mIsCreated = false;
				return false;
			}

		} else {

			// joinable
			rtn = pthread_create (&mThreadId, NULL, threadHandler, this);
			if (rtn != 0) {
				perror ("pthread_create()");
				mIsCreated = false;
				return false;
			}
		}
	}

	return true;
}

void CProxyThread::waitDestroy (void)
{
	if (mIsJoinable) {
		if (pthread_join (mThreadId, NULL) != 0) {
			perror ("pthread_join()");
		}

	} else {
		while (mIsCreated) {
			sleep (1);
		}
	}
}

void *CProxyThread::threadHandler (void *args)
{
	if (!args) {
		return NULL;
	}

	CProxyThread *pInstance = static_cast <CProxyThread*> (args);
	if (pInstance) {
		pInstance->run ();
	}

	return NULL;
}

void CProxyThread::run (void)
{
	mainRoutine ();

	mIsCreated = false;

	// thread end
}

void CProxyThread::mainRoutine (void)
{
	ST_REQ_QUEUE stQue;
	bool isDestroy = false;

	onSetup ();

	while (1) {
		// lock
		pthread_mutex_lock (&mCondMutex);

		memset (&stQue, 0x00, sizeof(stQue));
		stQue = deQueue ();
		mpNowReqQue = &stQue;
		if (!stQue.isUsed) {

			printf ("mainRoutine  pthread_cond_wait \n");
			pthread_cond_wait (&mCond, &mCondMutex);

			// unlock
			pthread_mutex_unlock (&mCondMutex);

		} else {
			// unlock
			pthread_mutex_unlock (&mCondMutex);


			switch (stQue.enReqType) {
			case EN_REQ_TYPE_EXEC:

				onExec (stQue.msg, &stQue.data);
				break;

			case EN_REQ_TYPE_DESTROY:
				isDestroy = true;
				break;

			default:
				printf ("BUG: req kind is invalid\n");
				break;
			}
		}

		if (isDestroy) {
			break;
		}
	}

	onTeardown ();
}

bool CProxyThread::request (int msg, const ST_DATA *pData)
{
	if (!isAlive()) {
		return false;
	}

	CScopedMutex scopedMutex (&mCondMutex);

	if (!enQueue (msg, pData, EN_REQ_TYPE_EXEC)) {
		return false;
	}

	pthread_cond_signal (&mCond);

	return true;
}

bool CProxyThread::reqDestroy (void)
{
	if (!isAlive()) {
		return false;
	}

	CScopedMutex scopedMutex (&mCondMutex);

	if (!enQueue (0, NULL, EN_REQ_TYPE_DESTROY)) {
		return false;
	}

	pthread_cond_signal (&mCond);

	return true;
}

bool CProxyThread::enQueue (int msg, const ST_DATA *pData, EN_REQ_TYPE enReqType)
{
	CScopedMutex scopedMutex (&mMutexReqQueue);


	if (mReqQueueVector.size() > REQ_QUEUE_MAX) {
		printf ("mReqQueueVector.size()=[%lu] =>maximum...\n", mReqQueueVector.size());
		return false;
	}

	ST_REQ_QUEUE stQue ;

	stQue.msg = msg;
	if ((pData) && (pData->size > 0)) {
		int cpsize = DATA_SIZE > pData->size ? pData->size : DATA_SIZE;
		memcpy (stQue.data.raw, pData->raw, cpsize);
		stQue.data.size = cpsize;
	}
	stQue.enReqType = enReqType;
	stQue.isUsed = true;

	mReqQueueVector.push_back (stQue);

	return true;
}

ST_REQ_QUEUE CProxyThread::deQueue (bool isPeep)
{
	CScopedMutex scopedMutex (&mMutexReqQueue);


	ST_REQ_QUEUE stQue ;

	printf ("mReqQueueVector.size()=[%lu]\n", mReqQueueVector.size());
	if (mReqQueueVector.size() <= 0) {
		return stQue;
	}

	REQ_QUEUE_VECTOR::iterator head = mReqQueueVector.begin();
	if (&head[0]) {
		stQue.msg = head->msg;
		if (head->data.size > 0) {
			memcpy (stQue.data.raw, head->data.raw, head->data.size);
			stQue.data.size = head->data.size;
		}
		stQue.enReqType = head->enReqType;
		stQue.isUsed = head->isUsed;
		if (!isPeep) {
			printf ("mReqQueueVector.erase\n");
			mReqQueueVector.erase(head);
		}
	}

    return stQue;
}

ST_REQ_QUEUE *CProxyThread::getNowQueue (void)
{
	return mpNowReqQue;
}

pthread_t CProxyThread::getId (void)
{
	return mThreadId;
}

bool CProxyThread::isAlive (void)
{
	return mIsCreated;
}

void CProxyThread::onSetup (void)
{
	printf ("%s\n", __PRETTY_FUNCTION__);
}

void CProxyThread::onExec (int msg, const ST_DATA *pData)
{
	printf ("%s\n", __PRETTY_FUNCTION__);
}

void CProxyThread::onTeardown (void)
{
	printf ("%s\n", __PRETTY_FUNCTION__);
}

} // namespace ProxyThread
