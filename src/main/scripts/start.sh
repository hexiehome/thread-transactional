#!/bin/sh

export JAVA_OPTS="-Xms1024m -Xmx1024m -Xss256k"
export JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Chongqing -Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8"

echo "startup : canal-dbsync-v1.0.0.jar"
echo "step 1 : shutdown alive canal-dbsync server"
pidlist=`ps -ef|grep canal-dbsync-v1.0.0.jar|grep -v grep|awk '{print $2}'`
if [ "$pidlist" = "" ]
	then
		echo "no canal-dbsync server pid alive !"
else
	echo "canal-dbsync server pid list : $pidlist"
	kill -9 $pidlist
	echo "kill canal-dbsync server success $pidlist"
fi
echo "step 2 : startup canal-dbsync server"
executeDir=`dirname $0`
echo "executeDir : $executeDir"
cd $executeDir
cd ../dist_lib
setsid java -jar -Dspring.config.location=./../config/application.properties -Dlogging.config=./../config/log4j2.xml ./canal-dbsync-v1.0.0.jar >/dev/null &
echo "startup canal-dbsync server success"