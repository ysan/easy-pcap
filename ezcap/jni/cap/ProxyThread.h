#ifndef _PROXY_THREAD_H_
#define _PROXY_THREAD_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <pthread.h>

#include <vector>

using namespace std;


namespace ProxyThread {

#define REQ_QUEUE_MAX		(5)
#define DATA_SIZE			(32)

typedef enum {
	EN_REQ_TYPE_EXEC = 0,
	EN_REQ_TYPE_DESTROY,

	EN_REQ_TYPE_BLANK,
} EN_REQ_TYPE;

typedef struct {
	unsigned char raw [DATA_SIZE];
	int size;
} ST_DATA;

typedef struct req_queue {
public:
	req_queue (void)
		:msg (-1)
		,enReqType (EN_REQ_TYPE_BLANK)
		,isUsed (false)
	{
		memset (&data, 0x00, sizeof(data));
	}
	~req_queue (void) {}

#if 0
private:
	void operator=(const req_queue& obj) {}
	req_queue (const req_queue& obj) {}
#endif

public:
	int msg ;
	ST_DATA data;
	EN_REQ_TYPE enReqType;
	bool isUsed;

} ST_REQ_QUEUE;

typedef vector<ST_REQ_QUEUE> REQ_QUEUE_VECTOR;


typedef void* (*P_THREAD_HANDLER) (void* args);

class CProxyThread
{
private:
	class CScopedMutex
	{
	public:
		CScopedMutex (pthread_mutex_t* pMutex) : mpMutex(NULL) {
			if (pMutex) {
				mpMutex = pMutex;
				pthread_mutex_lock (mpMutex);
			}
		}

		~CScopedMutex (void) {
			if (mpMutex) {
				pthread_mutex_unlock (mpMutex);
			}
		}

	private:
		pthread_mutex_t *mpMutex;
	};

protected:
	CProxyThread (void);
	virtual ~CProxyThread (void);

	bool create (bool isJoinable=false);
	void waitDestroy (void);
	pthread_t getId (void);
	bool isAlive (void);
	bool request (int msg=0, const ST_DATA *pData=NULL);
	bool reqDestroy (void);
	ST_REQ_QUEUE *getNowQueue (void);

protected:
	virtual void onSetup (void);
	virtual void onTeardown (void);
	virtual void onExec (int msg, const ST_DATA *pData) = 0;


private:
	static void *threadHandler (void *args);
	void run (void);
	void mainRoutine (void);
	bool enQueue (int msg, const ST_DATA *pData, EN_REQ_TYPE enReqType);
	ST_REQ_QUEUE deQueue (bool isPeep=false);


	pthread_t mThreadId;
	pthread_mutex_t mMutex;

	pthread_cond_t mCond;
	pthread_mutex_t mCondMutex;

	pthread_mutex_t mMutexReqQueue;

	bool mIsJoinable;
	bool mIsCreated;

	REQ_QUEUE_VECTOR mReqQueueVector;
	ST_REQ_QUEUE *mpNowReqQue;

};

} // namespace ProxyThread

#endif
