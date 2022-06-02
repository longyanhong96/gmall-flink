#!/usr/bin/env bash

export HADOOP_USER_NAME=hdfs
APP=test

if [ -n "$2" ] ;then
    do_date=$1
else
    do_date=`date -d '-1 day' +%F`
fi

hive -hiveconf day=${do_date} -f load_dim_table.sql
