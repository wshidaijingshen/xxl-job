#!/bin/bash
jar_name=xxl-job-admin
monitor_file1=/opt/target/$jar_name.jar
monitor_file2=/opt/$jar_name.sh
monitor_file3=/opt/inotify.sh
log_file=/opt/logs/inotify.log
sleep 25
echo $(date +"%Y-%m-%d %H:%M:%S") 监听中 $monitor_file1,$monitor_file2,$monitor_file3 >> $log_file
inotifywait  -o $log_file  -e attrib,create \
  --timefmt '%Y-%m-%d %H:%M:%S' --format '%T %w%f %e' \
  $monitor_file1 $monitor_file2 $monitor_file3
pid=$(ps -ef | grep 'java' | grep 'jar' | grep $jar_name | grep -v grep | awk '{print $1}')
echo $(date +"%Y-%m-%d %H:%M:%S") 'kill -15 '$pid >> $log_file
kill -15 $pid
# 参考 rsync 同步 https://www.jianshu.com/p/468b9eee006d
