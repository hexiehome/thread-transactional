#!/bin/sh

APP_NAME="thread-transactional-v1.0.0.jar"
JAVA_OPTS="-Xms512m -Xmx512m -Xss256k -Duser.timezone=UTC -Dfile.encoding=UTF-8"

echo "startup : ${APP_NAME}"
echo "step 1 : stop alive process"
pidlist=`ps -ef|grep ${APP_NAME}|grep -v grep|awk '{print $2}'`
if [ "$pidlist" = "" ]
then
    echo "no process alive"
else
    echo "process pid list : $pidlist"
    kill -9 $pidlist
fi

echo "step 2 : startup process"
executeDir=`dirname $0`
cd "$executeDir"/../dist_lib || exit 1
setsid java ${JAVA_OPTS} -jar -Dspring.config.location=./../config/application.properties -Dlogging.config=./../config/log4j2.xml ./${APP_NAME} >/dev/null &
echo "startup success"
