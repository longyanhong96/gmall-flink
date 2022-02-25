package com.myself.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/25 2:17 下午
 */
@Slf4j
public class JsonUtil {

    private static SerializeConfig config;
    private static ParserConfig parserConfig;

    static {
        parserConfig = new ParserConfig();
        parserConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;

        config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    public static String format(Object o) {
        return JSONObject.toJSONString(o, config);
    }

    public static <T> T parse(String value, Class<T> clazz) {
        return JSONObject.parseObject(value, clazz, parserConfig);
    }
}
