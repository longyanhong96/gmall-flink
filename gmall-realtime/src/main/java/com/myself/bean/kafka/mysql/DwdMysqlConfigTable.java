package com.myself.bean.kafka.mysql;

import lombok.Data;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/2 22:51
 * <p>
 * msyql 的建表语句
 * <p>
 * CREATE TABLE `table_process` (
 * `source_table` varchar(200) NOT NULL COMMENT '来源表',
 * `operate_type` varchar(200) NOT NULL COMMENT '操作类型 insert,update,delete',
 * `sink_type` varchar(200) DEFAULT NULL COMMENT '输出类型 hbase kafka',
 * `sink_table` varchar(200) DEFAULT NULL COMMENT '输出表(主题)',
 * `sink_columns` varchar(2000) DEFAULT NULL COMMENT '输出字段',
 * `sink_columns_key` varchar(200) DEFAULT NULL COMMENT '主键字段',
 * `sink_extend` varchar(200) DEFAULT NULL COMMENT '建表扩展',
 * PRIMARY KEY (`source_table`,`operate_type`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8
 */
@Data
public class DwdMysqlConfigTable {

    /**
     * 数据来源表
     */
    private String sourceTable;

    /**
     * 操作
     */
    private String operateType;

    /**
     * 写入的类型（hbase kafka）
     */
    private String sinkType;

    /**
     * 写入的 hbase table 或者 kafka topic
     */
    private String sinkTable;


    /**
     * 数据源写入到下游的字段
     */
    private String sinkColumns;

    /**
     * 数据源写入到下游的主键
     */
    private String sinkPk;

    /**
     * 建表扩展字段
     *
     * 主要是写入到hbase的，动态建表
     */
    private String sinkExtend;
}
