#!/bin/sh
jar_name=xxl-job-executor-sample-springboot

monitor_file1=/opt/target/$jar_name.jar
monitor_file2=/opt/$jar_name.sh
monitor_file3=/opt/inotify.sh
monitor_path=/opt/config/

log_file=/opt/logs/inotify.log

echo $(date +"%Y-%m-%d %H:%M:%S") 等待中 $monitor_file1,$monitor_file2,$monitor_file3,$monitor_path >> $log_file
sleep 15
echo $(date +"%Y-%m-%d %H:%M:%S") 监听中 $monitor_file1,$monitor_file2,$monitor_file3,$monitor_path >> $log_file

inotifywait  -o $log_file  -e attrib,create \
  --timefmt '%Y-%m-%d %H:%M:%S' --format '%T %w%f %e' \
  -r $monitor_file1 $monitor_file2 $monitor_file3 $monitor_path

pid=$(ps -ef | grep 'java' | grep 'jar' | grep $jar_name | grep -v grep | awk '{print $1}')
echo $(date +"%Y-%m-%d %H:%M:%S") 'kill -15 '$pid >> $log_file
kill -15 $pid
# 参考 rsync 同步 https://www.jianshu.com/p/468b9eee006d
