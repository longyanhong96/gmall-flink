package com.myself.process.dwd;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/26 21:50
 */
public class CheckNewUserMapFunction extends RichMapFunction<JSONObject, JSONObject> {

    private ValueState<String> midValueState;

    @Override
    public void open(Configuration parameters) throws Exception {
        midValueState = getRuntimeContext().getState(new ValueStateDescriptor<String>("new-mid", String.class));
    }

    @Override
    public JSONObject map(JSONObject jsonObject) throws Exception {

        String mid = jsonObject.getJSONObject("common").getString("mid");
        if ("1".equals(mid)) {
            String midValue = midValueState.value();
            String midTsValue = jsonObject.getString("ts");
            if (midValue != null) {
                jsonObject.getJSONObject("common").put("is_new", "0");
            }
            midValueState.update(midTsValue);
        }

        return jsonObject;
    }
}
