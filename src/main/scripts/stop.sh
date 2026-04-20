#!/bin/sh

APP_NAME="thread-transactional-v1.0.0.jar"
echo "shutdown : ${APP_NAME}"

pidlist=`ps -ef|grep ${APP_NAME}|grep -v grep|awk '{print $2}'`
if [ "$pidlist" = "" ]
then
    echo "no process alive"
else
    echo "process pid list : $pidlist"
    kill -9 $pidlist
    echo "shutdown success"
fi
