package com.myself.connector.utils;

import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 22:23
 */
public class JdbcUtilsTest extends TestCase {

    public void testGetConnect() throws SQLException {
//        Props props = PropsUtil.get("E:\\GitHub\\my\\gmall-flink\\gmall-realtime\\src\\main\\resources\\phoenix.properties");
//        Connection connect = JdbcUtils.getConnect(props);

//        String sql = "CREATE TABLE user02 (id varchar PRIMARY KEY,name varchar ,passwd varchar)";
//
//        PreparedStatement ps = connect.prepareStatement(sql);
//
//        ps.execute();

//        System.out.println("create success...");

//        System.out.println("connect.isClosed() = " + connect.isClosed());

//        connect.close();
    }

    public void test1(){
        String str = "{\"database\":\"gmall\",\"data\":{\"name\":\"其他\",\"id\":1099,\"category2_id\":113},\"operation\":\"c\",\"table\":\"base_category3\"}";

        JSONObject jsonObject = JSONObject.parseObject(str);
        String phoenixInsertUpdateSql = JdbcUtils.getPhoenixInsertUpdateSql(jsonObject.getJSONObject("data"), "base_category3");
        System.out.println("phoenixInsertUpdateSql = " + phoenixInsertUpdateSql);
    }
}