package com.myself.connector.function.impl;


import com.alibaba.fastjson.JSONObject;
import com.myself.connector.function.SqlFromFunction;
import com.myself.connector.utils.JdbcUtils;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 23:07
 */
public class PhoenixSqlFunction implements SqlFromFunction<String> {
    @Override
    public String transformSql(String bean) {
        JSONObject json = JSONObject.parseObject(bean);
        String table = json.getString("sinkTable");
        JSONObject data = json.getJSONObject("value");

        String phoenixInsertUpdateSql = JdbcUtils.getPhoenixInsertUpdateSql(data, table);
        return phoenixInsertUpdateSql;
    }
}
