package com.myself.apps;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.myself.apps.job.AbstractApp;
import com.myself.constants.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author longyh
 * @Description: 总体的启动类
 * @analysis:
 * @date 2022/2/24 4:17 下午
 */
@Slf4j
public class App {

    /**
     * 项目全部 modelName
     */
    private static final Set<String> MODEL_NAME_SET = getAllMoleNames();
    /**
     * 项目 model 包路径
     */
    public static final String PACKAGE = "com.myself.apps.job.dwd";

    public static void main(String[] args) throws Exception {
        // 解析输入参数
        log.info("======= begin parse input args =========");
        ParameterTool params = ParameterTool.fromArgs(args);
        log.info("======= parse input args completed ========");
        String modelName = params.get(BaseConstants.MODEL_NAME);
        log.info("======= parse param modelName:{}", modelName);

        String className = PACKAGE + "." + modelName;
        if (!MODEL_NAME_SET.contains(className)) {
            throw new IllegalArgumentException("项目不包含该 model：" + modelName);
        }

        // 反射获取 model 类
        AbstractApp app = ReflectUtil.newInstance(className);
        app.setParams(params);
        app.processApp();
    }

    private static Set<String> getAllMoleNames() {
        Set<Class<?>> classes = ClassUtil.scanPackage(PACKAGE);
        return classes.stream().map(Class::getName)
                .filter(name -> !"BaseApp".endsWith(name))
                .collect(Collectors.toSet());
    }
}
