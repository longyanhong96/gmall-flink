package com.myself.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/25 6:07 下午
 */
public class DbUtils {
    public static String getPhoenixInsertUpdateSql(List<String> keys, List<String> values, String table) {
        String insertSql = "upsert into {} ({}) values({})";
        String keyColumns = String.join(",", keys);
        String value = String.join(",", values);

        return String.format(insertSql, table, keyColumns, value);
    }

//    public static String getPhoenixInsertUpdateSql(JSONObject jsonObject,String table){
////        return getPhoenixInsertUpdateSql(jsonObject)
//
//    }
}
