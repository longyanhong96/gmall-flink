package com.myself.apps.job.dwm;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSONObject;
import com.myself.apps.job.AbstractApp;
import com.myself.bean.ods.CommonPageLog;
import com.myself.utils.KafkaUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:48 下午
 */
public class DwmFlinkUniqueVisit extends AbstractApp {

    private Props kafkaConsumerProps;
    private List<String> consumerKafkaTopic;
    private Props kafkaProducerProps;

    @Override
    protected void process(StreamExecutionEnvironment env) throws Exception {
        DataStreamSource<String> kafkaSourceDataStream = env.addSource(KafkaUtils.getConsumer(kafkaConsumerProps, consumerKafkaTopic));

        SingleOutputStreamOperator<CommonPageLog> pageLogStream = kafkaSourceDataStream.map(str -> JSONObject.parseObject(str, CommonPageLog.class));

        pageLogStream.keyBy(commonPageLog -> commonPageLog.getCommon().getMid())
                .map(new RichMapFunction<CommonPageLog, CommonPageLog>() {
                    @Override
                    public CommonPageLog map(CommonPageLog commonPageLog) throws Exception {
                        return null;
                    }
                });

    }

    @Override
    protected void parseConfig(Dict dict) {

    }
}
