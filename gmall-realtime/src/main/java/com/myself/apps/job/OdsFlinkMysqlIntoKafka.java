package com.myself.apps.job;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.myself.bean.kafka.KafkaProducerRecord;
import com.myself.constants.BaseConstants;
import com.myself.constants.KafkaConstants;
import com.myself.constants.MysqlConstants;
import com.myself.process.ods.MysqlJsonStringDeserializationSchema;
import com.myself.utils.KafkaUtils;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupMode;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/24 10:31 下午
 */
public class OdsFlinkMysqlIntoKafka extends AbstractApp {

    private Props kafkaConsumerProps;
    private String kafkaTopic;

    private Props mysqlProps;
    private String mysqlDatabase;
    private String mysqlTables;
    private StartupOptions startupOption;

    @Override
    protected void process(StreamExecutionEnvironment env) throws Exception {
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

        env.addSource(mysqlSourceFunction)
                .addSink(KafkaUtils.getKafkaSink(kafkaTopic, kafkaConsumerProps));
//                .addSink(KafkaUtils.getKafkaSinkBySchema(new KafkaSerializationSchema<String>() {
//
//                    private String topic = "ods_base_db";
//
//                    @Override
//                    public ProducerRecord<byte[], byte[]> serialize(String s, @Nullable Long aLong) {
//                        return new ProducerRecord<byte[], byte[]>(topic, s.getBytes());
//                    }
//                }, kafkaConsumerProps, kafkaTopic));
//                .map(new MapFunction<String, KafkaProducerRecord<String>>() {
//                    @Override
//                    public KafkaProducerRecord<String> map(String s) throws Exception {
//                        return KafkaProducerRecord.of(s);
//                    }
//                })
//                .addSink(KafkaUtils.getProducer(kafkaTopic, kafkaConsumerProps));
    }

    @Override
    protected void parseConfig(Dict dict) {
        /**
         * kafka配置
         */
        kafkaConsumerProps = PropsUtil.get(dict.getByPath(KafkaConstants.KAFKA_PRODUCER_CONFIG_PATH, String.class));
        kafkaTopic = dict.getByPath(KafkaConstants.KAFKA_TOPICS, String.class);

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
    }
}
