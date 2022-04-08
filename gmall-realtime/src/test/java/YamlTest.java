import cn.hutool.core.io.resource.ResourceUtil;
import junit.framework.TestCase;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 3:03 下午
 */
public class YamlTest extends TestCase {

    public void test() {
        Map<String, String> props = new HashMap<String, String>();
//        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) new Yaml().load("/Users/mininglamp/Documents/work/gmall-flink/gmall-realtime/src/main/resources/apps/application-dwd-db-into-hbasekafka.yml");

        Map<String, Map<String, Object>> map = new Yaml().loadAs(ResourceUtil.getResourceObj("/Users/mininglamp/Documents/work/gmall-flink/gmall-realtime/src/main/resources/apps/application-dwd-db-into-hbasekafka.yml").getStream(), Map.class);

//        map.entrySet().stream().flatMap(stringMapEntry -> eachAppendYamlItem(stringMapEntry.getKey(), stringMapEntry.getValue(), props))

//        map.
        map.entrySet().forEach(stringMapEntry -> eachAppendYamlItem("", stringMapEntry.getKey(), stringMapEntry.getValue(), props));

        props.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
    }

    public void eachAppendYamlItem(String prefix, String key, Object value, Map<String, String> proper) {
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

    public void test1() {
//        Double a = 2.2;
//        String s = a.toString();
//        System.out.println("s = " + s);

        String format = String.format("{%s}.{}", "abc", "qwe");
        System.out.println("format = " + format);
    }
}
