package com.myself.bean.kafka;

import com.myself.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/14 23:06
 */
@NoArgsConstructor
@AllArgsConstructor
public
class KafkaStringSerialization<T> implements KafkaSerializationSchema<KafkaProducerRecord<T>> {

    private String topic;

    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaProducerRecord<T> kafkaProducerRecord, @Nullable Long aLong) {
        return new ProducerRecord<byte[], byte[]>(
                kafkaProducerRecord.getTopic(),
                Objects.requireNonNull(JsonUtil.format(kafkaProducerRecord.getKey())).getBytes(StandardCharsets.UTF_8),
                Objects.requireNonNull(JsonUtil.format(kafkaProducerRecord.getValue())).getBytes(StandardCharsets.UTF_8)
        );
    }

    // todo : 模仿streamX sqlFromFunction,对不同的数据又不同的解析
}