#!/usr/bin/env bash

export HADOOP_USER_NAME=hdfs
APP=test
hdfs_data_path=/origin_data/test/db

if [ -n "$2" ] ;then
    do_date=$1
else
    do_date=`date -d '-1 day' +%F`
fi

function getsql() {
  data_path=$hdfs_data_path/$1/$do_date
  echo $data_path
  echo 'load data path '\'$data_path\'' into table '$APP'.ods_'$1' patition(dt='$do_date');'
}

load_ods_activity_info(){
	getsql activity_info
}
load_ods_activity_rule(){
	getsql activity_rule
}
load_ods_base_category1(){
	getsql base_category1
}
load_ods_base_category2(){
	getsql base_category2
}
load_ods_base_category3(){
	getsql base_category3
}
load_ods_base_dic(){
	getsql base_dic
}
load_ods_base_province(){
	getsql base_province
}
load_ods_base_region(){
	getsql base_region
}
load_ods_base_trademark(){
	getsql base_trademark
}
load_ods_cart_info(){
	getsql cart_info
}
load_ods_comment_info(){
	getsql comment_info
}
load_ods_coupon_info(){
	getsql coupon_info
}
load_ods_coupon_use(){
	getsql coupon_use
}
load_ods_favor_info(){
	getsql favor_info
}
load_ods_order_detail(){
	getsql order_detail
}
load_ods_order_detail_activity(){
	getsql order_detail_activity
}
load_ods_order_detail_coupon(){
	getsql order_detail_coupon
}
load_ods_order_info(){
	getsql order_info
}
load_ods_order_refund_info(){
	getsql order_refund_info
}
load_ods_order_status_log(){
	getsql order_status_log
}
load_ods_payment_info(){
	getsql payment_info
}
load_ods_refund_payment(){
	getsql refund_payment
}
load_ods_sku_attr_value(){
	getsql sku_attr_value
}
load_ods_sku_info(){
	getsql sku_info
}
load_ods_sku_sale_attr_value(){
	getsql sku_sale_attr_value
}
load_ods_spu_info(){
	getsql spu_info
}
load_ods_user_info(){
	getsql user_info
}

case $1 in
  "ods_activity_info")
    load_ods_activity_info
    ;;
  "ods_activity_rule")
    load_ods_activity_rule
    ;;
  "ods_base_category1")
    load_ods_base_category1
    ;;
  "ods_base_category2")
    load_ods_base_category2
    ;;
  "ods_base_category3")
    load_ods_base_category3
    ;;
  "ods_base_dic")
    load_ods_base_dic
    ;;
  "ods_base_province")
    load_ods_base_province
    ;;
  "ods_base_region")
    load_ods_base_region
    ;;
  "ods_base_trademark")
    load_ods_base_trademark
    ;;
  "ods_cart_info")
    load_ods_cart_info
    ;;
  "ods_comment_info")
    load_ods_comment_info
    ;;
  "ods_coupon_info")
    load_ods_coupon_info
    ;;
  "ods_coupon_use")
    load_ods_coupon_use
    ;;
  "ods_favor_info")
    load_ods_favor_info
    ;;
  "ods_order_detail")
    load_ods_order_detail
    ;;
  "ods_order_detail_activity")
    load_ods_order_detail_activity
    ;;
  "ods_order_detail_coupon")
    load_ods_order_detail_coupon
    ;;
  "ods_order_info")
    load_ods_order_info
    ;;
  "ods_order_refund_info")
    load_ods_order_refund_info
    ;;
  "ods_order_status_log")
    load_ods_order_status_log
    ;;
  "ods_payment_info")
    load_ods_payment_info
    ;;
  "ods_refund_payment")
    load_ods_refund_payment
    ;;
  "ods_sku_attr_value")
    load_ods_sku_attr_value
    ;;
  "ods_sku_info")
    load_ods_sku_info
    ;;
  "ods_sku_sale_attr_value")
    load_ods_sku_sale_attr_value
    ;;
  "ods_spu_info")
    load_ods_spu_info
    ;;
  "ods_user_info")
    load_ods_user_info
    ;;
  "all")
    load_ods_activity_info
    load_ods_activity_rule
    load_ods_base_category1
    load_ods_base_category2
    load_ods_base_category3
    load_ods_base_dic
    load_ods_base_province
    load_ods_base_region
    load_ods_base_trademark
    load_ods_cart_info
    load_ods_comment_info
    load_ods_coupon_info
    load_ods_coupon_use
    load_ods_favor_info
    load_ods_order_detail
    load_ods_order_detail_activity
    load_ods_order_detail_coupon
    load_ods_order_info
    load_ods_order_refund_info
    load_ods_order_status_log
    load_ods_payment_info
    load_ods_refund_payment
    load_ods_sku_attr_value
    load_ods_sku_info
    load_ods_sku_sale_attr_value
    load_ods_spu_info
    load_ods_user_info
    ;;