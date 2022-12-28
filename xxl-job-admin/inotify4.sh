#!/bin/bash
inotifywait -mrq --timefmt '%y/%m/%d %H:%M' --format '%T %w%f %e' --event delete,modify,create,attrib /opt/xxl-job-admin.sh |while read event;
do
  echo $event
done