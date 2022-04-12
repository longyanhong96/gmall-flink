package com.myself.connector.sink.hbase;

import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/12 22:43
 */
public class HBaseBatchOutputFormat<T> extends RichOutputFormat<T> {

    private long batch;
    private long batchIntervalMs = 0;
    private boolean closed;

    private Connection connection;

    @Override
    public void configure(Configuration configuration) {

    }

    @Override
    public void open(int i, int i1) throws IOException {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        connection = ConnectionFactory.createConnection(conf);

    }

    @Override
    public void writeRecord(T t) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
