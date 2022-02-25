package com.myself.process.ods;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/25 12:08 上午
 */
public class MysqlJsonStringDeserializationSchema implements DebeziumDeserializationSchema<String> {
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
        Struct value = (Struct) sourceRecord.value();
        Struct afterValue = value.getStruct("after");
        if (afterValue != null) {
            Schema schema = afterValue.schema();
            JSONObject dataJson = new JSONObject();

            for (Field field : schema.fields()) {
                dataJson.put(field.name(), afterValue.get(field.name()));
            }

            JSONObject resultJson = new JSONObject();
            Struct sourceValue = value.getStruct("source");
            String operation = value.getString("op").toLowerCase();
            String database = sourceValue.getString("db");
            String tableName = sourceValue.getString("table");

            resultJson.put("operation", operation);
            resultJson.put("data", dataJson);
            resultJson.put("database", database);
            resultJson.put("table", tableName);

            collector.collect(resultJson.toJSONString());
        }
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
