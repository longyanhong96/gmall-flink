package com.myself.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 4:36 下午
 */
public class PropertiesUtils {

    public Map<String, String> fromYamlText(String filePath) {
        Map<String, String> props = new HashMap<String, String>(20);
        Map<String, Map<String, Object>> map = new Yaml().loadAs(ResourceUtil.getResourceObj(filePath).getStream(), Map.class);
        map.forEach((key, value) -> eachAppendYamlItem("", key, value, props));
        return props;
    }

    private void eachAppendYamlItem(String prefix, String key, Object value, Map<String, String> proper) {
        if (value instanceof Map) {
            ((Map<String, Object>) value).forEach((k, v) -> {
                if (prefix.isEmpty()) {
                    eachAppendYamlItem(key, k, v, proper);
                } else {
                    eachAppendYamlItem(String.format("%s.%s", prefix, key), k, v, proper);
                }
            });
        } else {
            String v;
            if (value == null) {
                v = "";
            } else {
                v = value.toString();
            }

            if (prefix.isEmpty()) {
                proper.put(key, v);
            } else {
                proper.put(String.format("%s.%s", prefix, key), v);
            }
        }
    }
}
