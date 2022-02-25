package com.myself.apps.job;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.myself.constants.BaseConstants;
import com.myself.constants.FlinkConfigContants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/24 4:32 下午
 */
@Slf4j
public abstract class AbstractApp {

    protected ParameterTool params;
    protected Dict dict;


    public void setParams(ParameterTool params) {
        this.params = params;
    }

    public void processApp() throws Exception {
        // 初始化应用配置文件
        initParameter();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置flink env环境信息
//        initEnv(env);
        // 运行任务
        process(env);
        // 启动
        String jobName = dict.getByPath(BaseConstants.JOB_NAME, String.class);
        env.execute(jobName);

    }

    private void initEnv(StreamExecutionEnvironment env) {
        StateBackend stateBackend;
        switch (dict.getByPath(FlinkConfigContants.FLINK_STATE_BACKEND_TYPE, String.class)) {
            case "rocksdb":
                Boolean incremental = dict.getByPath(FlinkConfigContants.FLINK_STATE_BACKEND_INCREMENTAL, Boolean.class);
                stateBackend = new EmbeddedRocksDBStateBackend(incremental);
                break;
            default:
                stateBackend = new HashMapStateBackend();
                break;
        }
        env.setStateBackend(stateBackend);

        // CheckPoint interval
        env.enableCheckpointing(Time.seconds(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_INTERVAL, Integer.class)).toMilliseconds());

        // CheckPoint Mode
        CheckpointingMode checkpointingMode = CheckpointingMode.valueOf(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINT_STORAGE, String.class).toUpperCase());
        env.getCheckpointConfig().setCheckpointingMode(checkpointingMode);

        // CheckPoint Dir
        env.getCheckpointConfig().setCheckpointStorage(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_DIR, String.class));

        // CheckPoint Min Pause Between, default 100ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_MIN_PAUSE_BETWEEN, Integer.class));

        // CheckPoint Max Timeout, default 600s
        env.getCheckpointConfig().setCheckpointTimeout(Time.seconds(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_TIMEOUT, Integer.class)).toMilliseconds());

        // CheckPoint Max Concurrent, default 1
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_CONCURRENT_CHECKPOINTS, Integer.class));

        // CheckPoint Max Failed Number, default 3
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(dict.getByPath(FlinkConfigContants.FLINK_STATE_CHECKPOINTS_TOLERABLE_CHECKPOINT_FAILURE_NUMBER, Integer.class));

        env.setParallelism(dict.getByPath(FlinkConfigContants.FLINK_DEFAULT_PARALLELISM, Integer.class));
        env.setMaxParallelism(128);
    }

    private void initParameter() {
        if (StringUtils.isBlank(params.get(BaseConstants.CONFIG_PATH_PARAM))) {
            throw new IllegalArgumentException("缺少必要参数：" + BaseConstants.CONFIG_PATH_PARAM);
        }
        String configPath = params.get(BaseConstants.CONFIG_PATH_PARAM);
        log.debug("configPath: {}", configPath);
        dict = YamlUtil.loadByPath(configPath);
        parseConfig(dict);
    }

    /**
     * 不同类的主要运行的方法
     *
     * @param env flink-env
     * @throws Exception
     */
    protected abstract void process(StreamExecutionEnvironment env) throws Exception;

    /**
     * @param dict 解析出配置
     */
    protected abstract void parseConfig(Dict dict);
}
