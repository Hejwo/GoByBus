#!/usr/bin/env bash
/etc/init.d/filebeat start
java -server \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=14096 \
-Dsun.net.inetaddr.ttl=30 \
-Xms700m -Xmx700m -XX:MaxMetaspaceSize=100m \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/goByBus-web/logs/ \
-XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"/tmp/goByBus-web/logs/goByBus-web-gc-log.log" \
-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=4001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false \
-jar /app.jar