package com.myself.utils;

import org.apache.flink.util.OutputTag;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/2/27 14:46
 */
public class OutputTagUtil {

    public static OutputTag<String> DWD_START_LOG_OUTPUT = new OutputTag<String>("start-log") {
    };

    public static OutputTag<String> DWD_DISPLAY_LOG_OUTPUT = new OutputTag<String>("display") {
    };


    public static OutputTag<String> DWD_DB_DIM_OUTPUT_HBASE = new OutputTag<String>("db-dim_hbase") {
    };
}
