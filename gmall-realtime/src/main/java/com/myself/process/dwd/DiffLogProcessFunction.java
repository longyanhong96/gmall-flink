package com.myself.process.dwd;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myself.utils.OutputTagUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/26 22:35
 */
public class DiffLogProcessFunction extends ProcessFunction<JSONObject, String> {

    @Override
    public void processElement(JSONObject inputValue, Context context, Collector<String> collector) throws Exception {
        String start = inputValue.getString("start");

        if (start != null) {
            context.output(OutputTagUtil.DWD_START_LOG_OUTPUT, inputValue.toJSONString());
        } else {
            JSONArray displaysJsonArray = inputValue.getJSONArray("displays");

            if (displaysJsonArray != null) {
                for (int i = 0; i < displaysJsonArray.size(); i++) {
                    JSONObject displayJson = displaysJsonArray.getJSONObject(i);
                    displayJson.put("page_id",
                            inputValue.getJSONObject("page").getString("page_id"));
                    context.output(OutputTagUtil.DWD_DISPLAY_LOG_OUTPUT, displayJson.toJSONString());
                }
            }
            collector.collect(inputValue.toJSONString());
        }
    }
}
