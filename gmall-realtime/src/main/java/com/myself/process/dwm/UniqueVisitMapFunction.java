package com.myself.process.dwm;

import com.myself.bean.ods.CommonPageLog;
import com.myself.bean.ods.StartLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/19 5:59 下午
 */
public class UniqueVisitMapFunction extends ProcessFunction<CommonPageLog, CommonPageLog> {

    private ValueState<String> midTsValueState;

    @Override
    public void open(Configuration parameters) throws Exception {
        midTsValueState = getRuntimeContext().getState(new ValueStateDescriptor<String>("mid-ts", String.class));


    }


    @Override
    public void processElement(CommonPageLog commonPageLog, Context context, Collector<CommonPageLog> collector) throws Exception {
        String mid = commonPageLog.getCommon().getMid();

        if (StringUtils.isBlank(commonPageLog.getPage().getLastPageId())) {
            String midTsValue = midTsValueState.value();

            if (midTsValue == null){

            }
        }
    }
}
