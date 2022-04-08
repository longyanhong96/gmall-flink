package com.myself.utils;

import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/28 3:33 下午
 */
public class DbUtilsTest extends TestCase {

    public JSONObject jsonObject;

    @Override
    protected void setUp() throws Exception {
        String json = "{\"ar\":\"110000\",\"uid\":\"12\",\"os\":\"iOS 13.3.1\",\"ch\":\"Appstore\",\"is_new\":\"1\",\"md\":\"iPhone X\",\"mid\":\"mid_15\",\"vc\":\"v2.1.132\",\"ba\":\"iPhone\"}";
        jsonObject = JSONObject.parseObject(json);
    }

    public void testGetPhoenixInsertUpdateSql() {
        String test = DbUtils.getPhoenixInsertUpdateSql(jsonObject, "test");
        System.out.println("test = " + test);
    }

    public void testTestGetPhoenixInsertUpdateSql() {
    }
}