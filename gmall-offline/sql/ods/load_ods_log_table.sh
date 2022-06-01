#!/usr/bin/env bash

export HADOOP_USER_NAME=hdfs

if [ -n "$1" ] ;then
    do_date=$1
else
    do_date=`date -d '-1 day' +%F`
fi

data_path='/data3/home_longyanhong/module/applog/log/app.'$do_date'.log'

echo $data_path

if [ -f "$data_path" ] ;then
        echo "true"
else
        echo "false"
        exit
fi

#echo 'load data local path '\'$data_path\'' into table ods_log patition(dt='$do_date');'

hive -e 'load data local inpath '\'$data_path\'' into table test.ods_log partition(dt='\'$do_date\'');'
