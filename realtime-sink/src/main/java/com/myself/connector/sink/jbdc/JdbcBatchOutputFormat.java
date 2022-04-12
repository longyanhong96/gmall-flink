package com.myself.connector.sink.jbdc;

import com.myself.connector.function.SqlFromFunction;
import com.myself.connector.utils.JdbcUtils;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.util.ExecutorThreadFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/8 6:19 下午
 */
public class JdbcBatchOutputFormat<T> extends RichOutputFormat<T> {

    private Properties jdbcProper;
    private SqlFromFunction<T> sqlFromFunction;
    private long batch;
    private long batchIntervalMs = 0;
    private boolean closed;


    public JdbcBatchOutputFormat(Properties jdbcProper, SqlFromFunction<T> sqlFromFunction, long batch) {
        this.jdbcProper = jdbcProper;
        this.sqlFromFunction = sqlFromFunction;
        this.batch = batch;
    }

    private Connection connection;
    private Statement statement;
    private volatile AtomicLong numPendingRow;
    private transient ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> scheduledFuture;

    @Override
    public void configure(Configuration configuration) {
    }

    @Override
    public void open(int i, int i1) throws IOException {
        try {
            connection = JdbcUtils.getConnect(jdbcProper);

            if (connection.isClosed()) {
                throw new IOException("connect is fail!!!!");
            }

            connection.setAutoCommit(false);
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        numPendingRow = new AtomicLong();

        if (batch != 1 && batchIntervalMs != 0) {
            this.scheduler = Executors.newScheduledThreadPool(1, new ExecutorThreadFactory(
                    "jdbc-write-output-format"));
            this.scheduledFuture = this.scheduler.scheduleWithFixedDelay(() -> {
                        synchronized (JdbcBatchOutputFormat.this) {
                            if (!closed) {
                                commit();
                            }
                        }
                    },
                    batchIntervalMs,
                    batchIntervalMs,
                    TimeUnit.MILLISECONDS);
        }
    }

    private synchronized void commit() {
        if (numPendingRow.get() > 0) {
            long pendingRow = numPendingRow.get();
            numPendingRow.compareAndSet(pendingRow, 0);

            try {
                int count = statement.executeBatch().length;
                statement.clearBatch();
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void writeRecord(T t) throws IOException {
        String insertSql = sqlFromFunction.transformSql(t);
        try {
            statement.addBatch(insertSql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (numPendingRow.incrementAndGet() >= batch) {
            commit();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (!closed) {
            closed = true;
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                scheduler.shutdown();
            }

            commit();

            try {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}
