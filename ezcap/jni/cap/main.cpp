#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>

#include "Cap.h"

using namespace std;

void test (void)
{
	CCap *pCap = CCap::getInstance();

	if (!pCap->findDevs ()) {
		return ;
	}
	unique_ptr<vector<unique_ptr<ST_INTERFACE>>> vif = pCap->getInterfaceList ();
	if (vif.get()->size() == 0) {
		pCap->unrefDevs ();
		return;
	}
	pCap->dumpInterfaceList (vif.get());
	pCap->unrefDevs ();


	if (!pCap->createCbThread()) {
		return ;
	}

	pCap->setInterface ("ens33", strlen("ens33"));

	char buf[1024] = {0};
	while (1) {
		memset (buf, 0x00, sizeof(buf));
		fgets (buf, sizeof(buf)-1, stdin);
		buf [strlen(buf) -1] = 0x00; // delete LF

		if ((strlen(buf) == 1) && (strncmp(buf, "quit", strlen(buf)) == 0)) {
			// quit
			break;

		} else if ((strlen(buf) == strlen("v")) && (strncmp(buf, "v", strlen(buf)) == 0)) {
			printf ("%s\n", pCap->getVersion ());

		} else if ((strlen(buf) == strlen("getf")) && (strncmp(buf, "getf", strlen(buf)) == 0)) {
			printf ("now filter [%s]\n", pCap->getFilter ());

		} else if ((strlen(buf) == strlen("clearf")) && (strncmp(buf, "clearf", strlen(buf)) == 0)) {
			pCap->clearFilter ();

		} else if ((strlen(buf) == strlen("start")) && (strncmp(buf, "start", strlen(buf)) == 0)) {
			if (!pCap->open()) {
				return;
			}
			pCap->setFilter();
			pCap->start ();

		} else if ((strlen(buf) == strlen("stop")) && (strncmp(buf, "stop", strlen(buf)) == 0)) {
			pCap->stop ();
			pCap->close();

		} else {
			pCap->setFilter (buf, strlen(buf));
		}
	}

	pCap->stop ();
	pCap->close();
	pCap->clearFilter ();


	pCap->destroyCbThread();
}

int main (void)
{
	test ();
	exit (EXIT_SUCCESS);
}
