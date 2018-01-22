#!/bin/sh

JAR_NAME="cap.jar"

export LD_LIBRARY_PATH=`pwd`/jni

java -jar ${JAR_NAME}

