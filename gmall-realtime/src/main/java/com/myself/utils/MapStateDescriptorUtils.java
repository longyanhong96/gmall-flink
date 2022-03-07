package com.myself.utils;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/2 22:33
 */
public class MapStateDescriptorUtils {

    public static final MapStateDescriptor<String, String> DWD_MYSQL_STATE = new MapStateDescriptor<>(
            "mysqlDwdMapState",
            BasicTypeInfo.STRING_TYPE_INFO,
            BasicTypeInfo.STRING_TYPE_INFO
    );
}
