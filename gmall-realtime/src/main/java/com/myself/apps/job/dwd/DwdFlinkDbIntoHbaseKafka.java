package com.myself.apps.job.dwd;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.myself.apps.job.AbstractApp;
import com.myself.bean.kafka.KafkaProducerRecord;
import com.myself.bean.kafka.mysql.DwdMysqlConfigTable;
import com.myself.connector.function.impl.PhoenixSqlFunction;
import com.myself.connector.sink.jbdc.JdbcBatchOutputFormat;
import com.myself.connector.sink.jbdc.JdbcSinkFunction;
import com.myself.constants.HbaseConstants;
import com.myself.constants.KafkaConstants;
import com.myself.constants.MysqlConstants;
import com.myself.process.dwd.MysqlConfigBroadcastProcessFunction;
import com.myself.process.ods.MysqlJsonStringDeserializationSchema;
import com.myself.sink.dwd.PhoenixSinkFunction;
import com.myself.utils.KafkaUtils;
import com.myself.utils.MapStateDescriptorUtils;
import com.myself.utils.OutputTagUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.DriverManager;
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
        DebeziumSourceFunction<String> mysqlSourceFunction = MySQLSource.<String>builder()
                .hostname(mysqlProps.getProperty(MysqlConstants.MYSQL_HOST))
                .port(mysqlProps.getInt(MysqlConstants.MYSQL_PORT))
                .username(mysqlProps.getProperty(MysqlConstants.MYSQL_USERNAME))
                .password(mysqlProps.getProperty(MysqlConstants.MYSQL_PASSWORD))
                .databaseList(mysqlDatabase)
                .tableList(mysqlTables)
                .deserializer(new MysqlJsonStringDeserializationSchema())
                .startupOptions(startupOption)
                .build();

        BroadcastStream<String> mysqlBroadcastStream = env.addSource(mysqlSourceFunction).setParallelism(1).broadcast(MapStateDescriptorUtils.DWD_MYSQL_STATE);

        SingleOutputStreamOperator<String> sinkKafkaStream = env.addSource(KafkaUtils.getConsumer(kafkaConsumerProps, consumerKafkaTopic))
                .connect(mysqlBroadcastStream)
                .process(new MysqlConfigBroadcastProcessFunction());

        sinkKafkaStream.map(new MapFunction<String, KafkaProducerRecord<String>>() {
            @Override
            public KafkaProducerRecord<String> map(String s) throws Exception {
                JSONObject json = JSONObject.parseObject(s);
                KafkaProducerRecord<String> record = KafkaProducerRecord.of(json.getString("sinkTable"), 0, json.getString("sinkKey"), json.getString("value"));
                return record;
            }
        }).addSink(KafkaUtils.getProducer(kafkaProducerProps));

        sinkKafkaStream.getSideOutput(OutputTagUtil.DWD_DB_DIM_OUTPUT_HBASE)//.print();
                .addSink(new JdbcSinkFunction<>(new JdbcBatchOutputFormat<>(phoenixProp, new PhoenixSqlFunction(), 5)));
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
