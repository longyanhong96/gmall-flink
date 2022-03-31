package com.myself.sink.dwd;

import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.alibaba.fastjson.JSONObject;
import com.myself.utils.DbUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.checkpoint.Checkpoint;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/25 5:57 下午
 */
public class PhoenixSinkFunction extends RichSinkFunction<String> implements CheckpointedFunction {

    private String phoenixConfigPath;
    private DataSource phoenixDataSource;
    private Connection phoenixConnection;
    private Statement phoenixStatement;
    private AtomicLong batchSize;


    @Override
    public void open(Configuration parameters) throws Exception {
        // 连接phoenix
        Setting setting = new Setting(phoenixConfigPath);
        phoenixDataSource = DSFactory.create(setting).getDataSource();
        phoenixConnection = phoenixDataSource.getConnection();
        phoenixStatement = phoenixConnection.createStatement();

        batchSize = new AtomicLong(0);
    }

    @Override
    public void invoke(String value, Context context) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(value);

    }

    @Override
    public void close() throws Exception {
        if (phoenixConnection != null) {
            phoenixConnection.close();
        }

        if (phoenixStatement != null) {
            phoenixStatement.close();
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {

    }

    @Override
    public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {

    }

    public void commit() {

    }
}
