package com.myself.utils;

import com.myself.bean.kafka.KafkaProducerRecord;
import com.myself.bean.kafka.KafkaStringSerialization;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/25 11:53 上午
 */
public class KafkaUtils {

    private static String DEFAULT_TOPIC = "dwd_default_topic";

    public static <T> FlinkKafkaProducer<T> getProducer(Properties props) {
        return new FlinkKafkaProducer<T>(DEFAULT_TOPIC
                , new KafkaStringSerialization()
                , props
                , FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    public static <T> FlinkKafkaProducer<T> getKafkaSinkBySchema(KafkaSerializationSchema<T> kafkaSerializationSchema, Properties props) {
        return new FlinkKafkaProducer<T>(DEFAULT_TOPIC,
                kafkaSerializationSchema, props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    public static FlinkKafkaProducer<String> getKafkaSink(String topic, Properties props) {
        return new FlinkKafkaProducer<String>(topic, new SimpleStringSchema(), props);
    }


    public static FlinkKafkaConsumer<String> getConsumer(Properties properties, List<String> topics) {
        return new FlinkKafkaConsumer<>(topics, new SimpleStringSchema(), properties);
    }


    public static <T> FlinkKafkaProducer<T> getKafkaSink(KafkaSerializationSchema<T>
                                                                 kafkaSerializationSchema, Properties props) {

        return new FlinkKafkaProducer<T>(DEFAULT_TOPIC,
                kafkaSerializationSchema,
                props,
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE);
    }
}



