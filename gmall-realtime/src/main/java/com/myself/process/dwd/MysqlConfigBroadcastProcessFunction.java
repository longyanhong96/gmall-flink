package com.myself.process.dwd;

import com.alibaba.fastjson.JSONObject;
import com.myself.bean.kafka.mysql.DwdMysqlConfigTable;
import com.myself.connector.utils.HbaseUtils;
import com.myself.utils.MapStateDescriptorUtils;
import com.myself.utils.OutputTagUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;


/**
 * @author longyh
 * @Description: mysql的动态配置处理，主要是来区别kafka source的各种数据sink到hbase还是kafka
 * @analysis:
 * @date 2022/3/2 22:39
 */
@Slf4j
public class MysqlConfigBroadcastProcessFunction extends BroadcastProcessFunction<String, String, String> {

    private Connection connection = null;
    private HBaseAdmin hBaseAdmin = null;

    @Override
    public void open(Configuration parameters) throws Exception {

        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        connection = ConnectionFactory.createConnection(conf);
        hBaseAdmin = (HBaseAdmin) connection.getAdmin();
    }

    @Override
    public void close() throws Exception {
        if (hBaseAdmin != null) {
            hBaseAdmin.close();
        }

        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void processElement(String sourceData, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {

        ReadOnlyBroadcastState<String, String> broadcastState = readOnlyContext.getBroadcastState(MapStateDescriptorUtils.DWD_MYSQL_STATE);

        JSONObject sourceJson = JSONObject.parseObject(sourceData);
        String table = sourceJson.getString("table");
        if (broadcastState.contains(table)) {

            JSONObject returnJson = new JSONObject();
            JSONObject valueJson = new JSONObject();
            JSONObject keyJson = new JSONObject();
            JSONObject dataJson = sourceJson.getJSONObject("data");

            DwdMysqlConfigTable dwdMysqlConfigTable = JSONObject.parseObject(broadcastState.get(table), DwdMysqlConfigTable.class);

            String sinkColumns = dwdMysqlConfigTable.getSinkColumns();
            String[] columns = sinkColumns.split(",");

            for (int i = 0; i < columns.length; i++) {
                String column = columns[i];
                valueJson.put(column, dataJson.get(column));
            }


            if (dwdMysqlConfigTable.getSinkPk().contains(",")) {
                String[] keyColumns = dwdMysqlConfigTable.getSinkPk().split(",");
                for (int i = 0; i < keyColumns.length; i++) {
                    String column = columns[i];
                    keyJson.put(column, dataJson.get(column));
                }
            } else {
                keyJson.put(dwdMysqlConfigTable.getSinkPk(), dataJson.get(dwdMysqlConfigTable.getSinkPk()));
            }

            returnJson.put("operateType", dwdMysqlConfigTable.getOperateType());
            returnJson.put("sinkType", dwdMysqlConfigTable.getSinkType());
            returnJson.put("sinkTable", dwdMysqlConfigTable.getSinkTable());
            returnJson.put("sinkKey", keyJson.toJSONString());
            returnJson.put("valueKey", valueJson.toJSONString());

            // todo:魔法值修改
            if (dwdMysqlConfigTable.getSinkType().equals("hbase")) {
                returnJson.put("column_family", dwdMysqlConfigTable.getTableFamily());
                readOnlyContext.output(OutputTagUtil.DWD_DB_DIM_OUTPUT_HBASE, returnJson.toJSONString());
            } else {
                collector.collect(returnJson.toJSONString());
            }
        }
    }

    /**
     * 处理的MySQL的动态变化
     *
     * @param mysqlSource
     * @param context
     * @param collector
     * @throws Exception
     */
    @Override
    public void processBroadcastElement(String mysqlSource, Context context, Collector<String> collector) throws Exception {
        BroadcastState<String, String> broadcastState = context.getBroadcastState(MapStateDescriptorUtils.DWD_MYSQL_STATE);

        JSONObject mysqlSourceJson = JSONObject.parseObject(mysqlSource);

        DwdMysqlConfigTable dwdMysqlConfigTable = JSONObject.parseObject(mysqlSourceJson.getString("data"), DwdMysqlConfigTable.class);
        // 保存配置
        String sourceTable = dwdMysqlConfigTable.getSourceTable();
        broadcastState.put(sourceTable, JSONObject.toJSONString(dwdMysqlConfigTable));

        // 根据数据的类型，判断是否要去hbase建表
        // todo：魔法值需要修改
        String operation = mysqlSourceJson.getString("operation");
        // 如果数据是初始都过来，或者后面的创建
        if (operation.equals("c") || operation.equals("r")) {
            if (dwdMysqlConfigTable.getSinkType().equals("hbase") &&
                    dwdMysqlConfigTable.getTableFamily() != null) {
                String columnsFamily = dwdMysqlConfigTable.getTableFamily();
                HbaseUtils.createTable(hBaseAdmin, dwdMysqlConfigTable.getSinkTable(), columnsFamily);
            }
        }
    }
}
