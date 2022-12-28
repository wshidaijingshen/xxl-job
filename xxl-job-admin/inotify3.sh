#!/bin/sh
# https://blog.csdn.net/qq_25518029/article/details/119727921

# 监视的文件或目录
#filename=$1

# 监视发现有增、删、改后执行的脚本
#script=$2

inotifywait -mrq --format '%e' -e create,delete,modify,attrib $filename | while read event; do
  case $event in MODIFY | CREATE | DELETE | ATTRIB) sh $script ;
  esac
done