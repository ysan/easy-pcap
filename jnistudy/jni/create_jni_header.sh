#!/bin/sh

PATH_PREFIX=/home/yoshi/prog/ws_swingtest/jnistudy/

javah -classpath ${PATH_PREFIX}/bin -d `pwd` com.jnistudy.cap.CapJni

