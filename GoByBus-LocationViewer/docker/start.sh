#!/usr/bin/env bash
/etc/init.d/filebeat start
java -server \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14096 \
-Dsun.net.inetaddr.ttl=30 \
-Xms300m -Xmx300m -XX:MaxMetaspaceSize=60m \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/goByBus-locationviewer/logs/ \
-XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"/tmp/goByBus-locationviewer/logs/goByBus-locationviewer-gc-log.log" \
-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=4001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false \
-jar /app.jar