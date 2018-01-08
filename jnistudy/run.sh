#!/bin/sh

PATH_PREFIX=/home/yoshi/prog/ws_swingtest/jnistudy/
JAR_NAME="cap.jar"

export LD_LIBRARY_PATH=${PATH_PREFIX}/jni

java -jar ${PATH_PREFIX}/${JAR_NAME}

