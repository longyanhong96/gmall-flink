package com.myself.apps.job.dwd;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.alibaba.fastjson.JSONObject;
import com.myself.apps.job.AbstractApp;
import com.myself.constants.KafkaConstants;
import com.myself.process.dwd.CheckNewUserMapFunction;
import com.myself.process.dwd.DiffLogProcessFunction;
import com.myself.utils.KafkaUtils;
import com.myself.utils.OutputTagUtil;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * @author longyh
 * @Description: 用户数据写入到不同的topic
 * @analysis:
 * @date 2022/2/26 21:14
 */
public class DwdFlinkLogIntoKafka extends AbstractApp {

    private Props kafkaConsumerProps;
    private List<String> consumerKafkaTopic;

    private Props kafkaProducerProps;
    private String startLogTopic;
    private String displayLogTopic;
    private String pageLogTopic;

    @Override
    protected void process(StreamExecutionEnvironment env) throws Exception {
        SingleOutputStreamOperator<String> diffLogStream = env.addSource(KafkaUtils.getConsumer(kafkaConsumerProps, consumerKafkaTopic))
                .map(value -> JSONObject.parseObject(value))
                .keyBy(jsonObject -> jsonObject.getJSONObject("common").getString("mid"))
                .map(new CheckNewUserMapFunction())
                .process(new DiffLogProcessFunction());

        diffLogStream.getSideOutput(OutputTagUtil.DWD_START_LOG_OUTPUT)
                .addSink(KafkaUtils.getKafkaSink(startLogTopic, kafkaProducerProps));

        diffLogStream.getSideOutput(OutputTagUtil.DWD_DISPLAY_LOG_OUTPUT)
                .addSink(KafkaUtils.getKafkaSink(displayLogTopic, kafkaProducerProps));

        diffLogStream.addSink(KafkaUtils.getKafkaSink(pageLogTopic, kafkaProducerProps));
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


        List<String> kafkaProducerTopic = dict.getByPath(KafkaConstants.KAFKA_PRODUCER_TOPICS, List.class);
        startLogTopic = kafkaProducerTopic.get(0);
        displayLogTopic = kafkaProducerTopic.get(1);
        pageLogTopic = kafkaProducerTopic.get(2);

    }
}
