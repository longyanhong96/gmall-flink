package com.myself.connector.sink;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 6:15 下午
 */
public class JdbcSinkFunction<T> extends RichSinkFunction<T> {

    private JdbcBatchOutputFormat<T> jdbcBatchOutputFormat;


    public JdbcSinkFunction(JdbcBatchOutputFormat<T> jdbcBatchOutputFormat) {
        this.jdbcBatchOutputFormat = jdbcBatchOutputFormat;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext ctx = getRuntimeContext();
        jdbcBatchOutputFormat.open(ctx.getIndexOfThisSubtask(), ctx.getNumberOfParallelSubtasks());
    }

    @Override
    public void close() throws Exception {
        jdbcBatchOutputFormat.close();
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        jdbcBatchOutputFormat.writeRecord(value);
    }


}
