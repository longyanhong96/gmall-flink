package com.myself.apps.job.dwd;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.alibaba.fastjson.JSONObject;
import com.myself.apps.job.AbstractApp;
import com.myself.bean.kafka.mysql.DwdMysqlConfigTable;
import com.myself.connector.function.impl.PhoenixSqlFunction;
import com.myself.connector.sink.JdbcBatchOutputFormat;
import com.myself.connector.sink.JdbcSinkFunction;
import com.myself.constants.HbaseConstants;
import com.myself.constants.KafkaConstants;
import com.myself.constants.MysqlConstants;
import com.myself.process.dwd.MysqlConfigBroadcastProcessFunction;
import com.myself.process.ods.MysqlJsonStringDeserializationSchema;
import com.myself.sink.dwd.PhoenixSinkFunction;
import com.myself.utils.KafkaUtils;
import com.myself.utils.MapStateDescriptorUtils;
import com.myself.utils.OutputTagUtil;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Properties;

/**
 * @author longyh
 * @Description: 分流，把维度表的数据写到hbase phoenix上，把事实表的数据写到kafka
 * @analysis: 用广播类，监控MySQL的变化，实现实时分流
 * @date 2022/3/2 22:15
 */
public class DwdFlinkDbIntoHbaseKafka extends AbstractApp {

    private Props kafkaConsumerProps;
    private List<String> consumerKafkaTopic;
    private Props kafkaProducerProps;

    private Props mysqlProps;
    private String mysqlDatabase;
    private String mysqlTables;
    private StartupOptions startupOption;

    private String phoenixConfigPath;
    private Properties phoenixProp;


    @Override
    protected void process(StreamExecutionEnvironment env) throws Exception {
        // 广播配置内容
        DebeziumSourceFunction<String> mysqlSourceFunction = MySqlSource.<String>builder()
                .hostname(mysqlProps.getProperty(MysqlConstants.MYSQL_HOST))
                .port(mysqlProps.getInt(MysqlConstants.MYSQL_PORT))
                .username(mysqlProps.getProperty(MysqlConstants.MYSQL_USERNAME))
                .password(mysqlProps.getProperty(MysqlConstants.MYSQL_PASSWORD))
                .databaseList(mysqlDatabase)
                .deserializer(new MysqlJsonStringDeserializationSchema())
//                .tableList(mysqlTables)
                .startupOptions(startupOption)
                .build();

        BroadcastStream<String> mysqlBroadcastStream = env.addSource(mysqlSourceFunction).setParallelism(1).broadcast(MapStateDescriptorUtils.DWD_MYSQL_STATE);

        SingleOutputStreamOperator<String> sinkKafkaStream = env.addSource(KafkaUtils.getConsumer(kafkaConsumerProps, consumerKafkaTopic))
                .connect(mysqlBroadcastStream)
//                .process(new BroadcastProcessFunction<String, String, String>() {
//
//
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//
//                    }
//
//                    @Override
//                    public void processElement(String s, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {
////                        collector.collect(s);
//                    }
//
//                    @Override
//                    public void processBroadcastElement(String mysqlSource, Context context, Collector<String> collector) throws Exception {
////                        collector.collect(s);
//                        BroadcastState<String, String> broadcastState = context.getBroadcastState(MapStateDescriptorUtils.DWD_MYSQL_STATE);
//
//                        JSONObject mysqlSourceJson = JSONObject.parseObject(mysqlSource);
//                        DwdMysqlConfigTable dwdMysqlConfigTable = JSONObject.parseObject(mysqlSourceJson.getString("data"), DwdMysqlConfigTable.class);
//                        // 保存配置
//                        String sourceTable = dwdMysqlConfigTable.getSourceTable();
//                        broadcastState.put(sourceTable, JSONObject.toJSONString(dwdMysqlConfigTable));
//
//                        String s = JSONObject.toJSONString(dwdMysqlConfigTable);
//                        collector.collect(s);
//
//                    }
//                });
                .process(new MysqlConfigBroadcastProcessFunction(phoenixProp)).setParallelism(1);

        sinkKafkaStream.print();

//        DataStream<String> sinkHbaseStream = sinkKafkaStream.getSideOutput(OutputTagUtil.DWD_DB_DIM_OUTPUT_HBASE);
//
//
//        sinkKafkaStream.print();
//
//        sinkHbaseStream.addSink(new JdbcSinkFunction<>(new JdbcBatchOutputFormat<>(phoenixProp, new PhoenixSqlFunction(), 5)));
    }

    @Override
    protected void parseConfig(Dict dict) {
        /**
         * kafka配置
         */
        kafkaConsumerProps = PropsUtil.get(dict.getByPath(KafkaConstants.KAFKA_CONSUMER_CONFIG_PATH, String.class));
        kafkaConsumerProps.put("group.id", dict.getByPath(KafkaConstants.KAFKA_GROUP_ID, String.class));
        consumerKafkaTopic = dict.getByPath(KafkaConstants.KAFKA_CONSUMER_TOPICS, List.class);

        kafkaProducerProps = PropsUtil.get(dict.getByPath(KafkaConstants.KAFKA_PRODUCER_CONFIG_PATH, String.class));

        /**
         * mysql配置
         */
        mysqlProps = PropsUtil.get(dict.getByPath(MysqlConstants.MYSQL_BASE_CONFIG_PATH, String.class));
        mysqlDatabase = dict.getByPath(MysqlConstants.MYSQL_DATABASE, String.class);
        mysqlTables = dict.getByPath(MysqlConstants.MYSQL_TABLES, String.class);

        switch (dict.getByPath(MysqlConstants.MYSQL_CDC_STARTUP_OPTIONS, String.class)) {
            case "INITIAL":
                startupOption = StartupOptions.initial();
                break;
            case "EARLIEST_OFFSET":
                startupOption = StartupOptions.earliest();
                break;
            default:
                startupOption = StartupOptions.latest();
                break;
        }


        /**
         * phoenix配置
         */
//        phoenixConfigPath = dict.getByPath(HbaseConstants.PHOENIX_CONFIG_PATH, String.class);
        phoenixProp = PropsUtil.get(dict.getByPath(HbaseConstants.PHOENIX_CONFIG_PATH, String.class));

    }
}
