package com.myself.process.dwd;

import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.alibaba.fastjson.JSONObject;
import com.myself.bean.kafka.mysql.DwdMysqlConfigTable;
import com.myself.connector.utils.JdbcUtils;
import com.myself.utils.MapStateDescriptorUtils;
import com.myself.utils.OutputTagUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author longyh
 * @Description: mysql的动态配置处理，主要是来区别kafka source的各种数据sink到hbase还是kafka
 * @analysis:
 * @date 2022/3/2 22:39
 */
@Slf4j
public class MysqlConfigBroadcastProcessFunction extends BroadcastProcessFunction<String, String, String> {


    private String phoenixConfigPath;
    private Properties phoenixProp;
    private DataSource phoenixDataSource;
    private Connection phoenixConnection;


    @Override
    public void open(Configuration parameters) throws Exception {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        phoenixConnection = DriverManager.getConnection("jdbc:phoenix:node1:2181");
    }

    @Override
    public void close() throws Exception {
        if (phoenixConnection != null) {
            phoenixConnection.close();
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
            returnJson.put("value", valueJson.toJSONString());

            // todo:魔法值修改
            if (dwdMysqlConfigTable.getSinkType().equals("hbase")) {
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
                    dwdMysqlConfigTable.getSinkExtend() != null) {
                if (!JdbcUtils.validateTableExist(phoenixConnection, dwdMysqlConfigTable.getSinkTable().toUpperCase())) {
                    String createTableSql = dwdMysqlConfigTable.getSinkExtend();
                    PreparedStatement ps = phoenixConnection.prepareStatement(createTableSql);
                    ps.execute();
                } else {
                    log.info("{} is exists!!!!", dwdMysqlConfigTable.getSinkTable());
                }
            }
        }

    }
}
