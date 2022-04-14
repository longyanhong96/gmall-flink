package com.myself.connector.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 6:26 下午
 */
public class JdbcUtils {

    private static HikariDataSource hikariDataSource = null;

    public static Connection getConnect(Properties properties) throws SQLException {
        if (hikariDataSource != null) {
            return hikariDataSource.getConnection();
        }
        // todo : 优化 模仿streamx
        HikariConfig hikariConfig = new HikariConfig(properties);
        hikariDataSource = new HikariDataSource(hikariConfig);

        return hikariDataSource.getConnection();
    }

    public static String getPhoenixInsertUpdateSql(Iterable<String> keys, Iterable<Object> values, String table) {
        String insertSql = "upsert into {} ({}) values('{}')";
        String keyColumns = CollUtil.join(keys, ",");
        String value = CollUtil.join(values, "','");

        return StrFormatter.format(insertSql, table, keyColumns, value);
    }

    public static String getPhoenixInsertUpdateSql(JSONObject jsonObject, String table) {
        return getPhoenixInsertUpdateSql(jsonObject.keySet(), IterUtil.asIterable(jsonObject.values().iterator()), table);
    }

    public static boolean validateTableExist(Connection connection, String tableName) {
        boolean flag = false;
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            flag = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

}
