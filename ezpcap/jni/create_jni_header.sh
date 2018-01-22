#!/bin/sh

PATH_PREFIX=../

javah -classpath ${PATH_PREFIX}/bin -d `pwd` com.nativewrapper.cap.CapJni

