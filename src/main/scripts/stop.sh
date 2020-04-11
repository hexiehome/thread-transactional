#!/bin/sh

echo "shutdown : canal-dbsync-v1.0.0.jar"
pidlist=`ps -ef|grep canal-dbsync-v1.0.0.jar|grep -v grep|awk '{print $2}'`
if [ "$pidlist" = "" ]
	then
		echo "no canal-dbsync server pid alive !"
else
	echo "canal-dbsync server pid list : $pidlist"
	kill -9 $pidlist
	echo "kill canal-dbsync server success $pidlist"
	echo "shutdown canal-dbsync server success"
fi