package com.myself.sink.dwd;

import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/25 5:57 下午
 */
public class PhoenixSinkFunction extends RichSinkFunction<String> {

    private String phoenixConfigPath;
    private DataSource phoenixDataSource;
    private Connection phoenixConnection;


    @Override
    public void open(Configuration parameters) throws Exception {
        // 连接phoenix
        Setting setting = new Setting(phoenixConfigPath);
        phoenixDataSource = DSFactory.create(setting).getDataSource();
        phoenixConnection = phoenixDataSource.getConnection();
    }

    @Override
    public void invoke(String value, Context context) throws Exception {

    }

    @Override
    public void close() throws Exception {
        if (phoenixConnection != null) {
            phoenixConnection.close();
        }
    }
}
