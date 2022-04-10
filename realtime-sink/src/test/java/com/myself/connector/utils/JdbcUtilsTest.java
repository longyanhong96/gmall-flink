package com.myself.connector.utils;

import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
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
        Props props = PropsUtil.get("E:\\GitHub\\my\\gmall-flink\\gmall-realtime\\src\\main\\resources\\phoenix.properties");
        Connection connect = JdbcUtils.getConnect(props);

//        String sql = "CREATE TABLE user02 (id varchar PRIMARY KEY,name varchar ,passwd varchar)";
//
//        PreparedStatement ps = connect.prepareStatement(sql);
//
//        ps.execute();

//        System.out.println("create success...");

        System.out.println("connect.isClosed() = " + connect.isClosed());

        connect.close();
    }
}