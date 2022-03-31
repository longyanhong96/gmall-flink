package com.myself.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/25 6:07 下午
 */
public class DbUtils {
    public static String getPhoenixInsertUpdateSql(Iterable<String> keys, Iterable<Object> values, String table) {
        String insertSql = "upsert into {} ({}) values({})";
        String keyColumns = CollUtil.join(keys, ",");
        String value = CollUtil.join(values, ",");

        return StrFormatter.format(insertSql, table, keyColumns, value);
    }

    public static String getPhoenixInsertUpdateSql(JSONObject jsonObject, String table) {
        return getPhoenixInsertUpdateSql(jsonObject.keySet(), IterUtil.asIterable(jsonObject.values().iterator()), table);
    }
}
