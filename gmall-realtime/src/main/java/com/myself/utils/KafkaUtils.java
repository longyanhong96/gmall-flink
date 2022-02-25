package com.myself.utils;

import com.myself.bean.kafka.KafkaProducerRecord;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/25 11:53 上午
 */
public class KafkaUtils {

    public static <T> FlinkKafkaProducer<T> getProducer(Properties props,String topic) {
        return new FlinkKafkaProducer<T>(topic
                , new KafkaStringSerialization(topic)
                , props
                , FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    public static <T> FlinkKafkaProducer<T> getKafkaSinkBySchema(KafkaSerializationSchema<T> kafkaSerializationSchema, Properties props, String topic) {
        return new FlinkKafkaProducer<T>(topic,
                kafkaSerializationSchema, props, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
    }

    public static FlinkKafkaProducer<String> getKafkaSink(String topic, Properties props) {
        return new FlinkKafkaProducer<String>(topic, new SimpleStringSchema(), props);
    }
}

@NoArgsConstructor
@AllArgsConstructor
class KafkaStringSerialization<T> implements KafkaSerializationSchema<KafkaProducerRecord<T>> {

    private String topic;

    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaProducerRecord<T> kafkaProducerRecord, @Nullable Long aLong) {
        return new ProducerRecord<byte[], byte[]>(
                topic,
                Objects.requireNonNull(JsonUtil.format(kafkaProducerRecord.getValue())).getBytes(StandardCharsets.UTF_8)
        );
    }
}

