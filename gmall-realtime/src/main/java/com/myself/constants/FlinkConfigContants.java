package com.myself.constants;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/24 6:04 下午
 */
public class FlinkConfigContants {

    public final static String FLINK_DEFAULT_PARALLELISM = "flink.default.parallelism";

    public final static String FLINK_STATE_BACKEND_TYPE = "flink.state.backend.type";
    public final static String FLINK_STATE_BACKEND_INCREMENTAL = "flink.state.backend.incremental";
    public final static String FLINK_STATE_CHECKPOINTS_INTERVAL = "flink.state.checkpoints.interval";
    public final static String FLINK_STATE_CHECKPOINTS_DIR = "flink.state.checkpoints.dir";
    public final static String FLINK_STATE_CHECKPOINTS_CONCURRENT_CHECKPOINTS= "flink.state.checkpoints.concurrentCheckpoints";
    public final static String FLINK_STATE_CHECKPOINT_STORAGE = "flink.state.checkpoint-storage";
    public final static String FLINK_STATE_CHECKPOINTS_MIN_PAUSE_BETWEEN = "flink.state.checkpoints.min.pause.between";
    public final static String FLINK_STATE_CHECKPOINTS_TIMEOUT = "flink.state.checkpoints.timeout";
    public final static String FLINK_STATE_CHECKPOINTS_TOLERABLE_CHECKPOINT_FAILURE_NUMBER = "flink.state.checkpoints.tolerable.checkpoint.failure.number";
}
