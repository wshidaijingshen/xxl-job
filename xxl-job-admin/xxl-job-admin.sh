#!/bin/sh
# -server -Xms200m -Xmx200m -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=40m
# -server 参数会导致 启动失败，64位机器默认就是该参数，jdk7机器默认是-client 模式 https://segmentfault.com/a/1190000041114158
# -XX:+PrintCommandLineFlags 列出被用户或者JVM优化设置过参数
# GC 打印最佳实践 https://blog.nowcoder.net/n/baa65a5fe88044398cf65b6375a556c7?from=nowcoder_improve
# JVM参数详解 https://juejin.cn/post/7054567953181704200

JAVA_OPTS="-Djava.awt.headless=true \
-Dfile.encoding=UTF-8 \
-Duser.timezone=GMT+08 \
-Dmaven.test.skip=true \
-XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+PrintGCDateStamps  -XX:+PrintHeapAtGC -XX:+PrintGCApplicationStoppedTime \
-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=2m -Xloggc:./logs/gc-%t.log \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/dump.hprof -XX:ErrorFile=./logs/fatal-error.log"

PARAMS="--spring.profiles.active=prod"
sh ./inotify.sh &
echo '' >> /opt/logs/inotify.log
echo $( date +"%Y-%m-%d %H:%M:%S") 启动中 xxl-job-admin >> /opt/logs/inotify.log
java "${JAVA_OPTS}" -jar ./target/xxl-job-admin.jar ${PARAMS}

