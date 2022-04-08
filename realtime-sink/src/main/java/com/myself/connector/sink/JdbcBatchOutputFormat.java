package com.myself.connector.sink;

import com.myself.connector.function.SqlFromFunction;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;

import java.io.Flushable;
import java.io.IOException;
import java.util.Properties;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 6:19 下午
 */
public class JdbcBatchOutputFormat<T> extends RichOutputFormat<T> implements Flushable {

    private Properties jdbcProper;
    private SqlFromFunction<T> sqlFromFunction;

    @Override
    public void configure(Configuration configuration) {
    }

    @Override
    public void open(int i, int i1) throws IOException {

    }

    @Override
    public void writeRecord(T t) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
}
