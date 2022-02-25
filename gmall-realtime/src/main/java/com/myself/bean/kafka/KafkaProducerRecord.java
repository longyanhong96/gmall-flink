package com.myself.bean.kafka;

import lombok.Data;

import java.io.Serializable;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/25 2:02 下午
 */
@Data
public class KafkaProducerRecord<T> implements Serializable {
    private Integer partition;
    private String key;
    private T value;

//    public static <E> KafkaProducerRecord<E> of(E value) {
//        KafkaProducerRecord<E> record = new KafkaProducerRecord<>();
//        record.setValue(value);
//        return record;
//    }
//
//    public static <E> KafkaProducerRecord<E> of(String key, E value) {
//        KafkaProducerRecord<E> record = new KafkaProducerRecord<>();
//        record.setKey(key);
//        record.setValue(value);
//        return record;
//    }
//
//    public static <E> KafkaProducerRecord<E> of(Integer partition, String key, E value) {
//        KafkaProducerRecord<E> record = new KafkaProducerRecord<>();
//        record.setPartition(partition);
//        record.setKey(key);
//        record.setValue(value);
//        return record;
//    }
}
