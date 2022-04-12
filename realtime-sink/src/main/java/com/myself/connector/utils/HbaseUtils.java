package com.myself.connector.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.sql.parser.hive.ddl.SqlAlterHiveTableSerDe;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/12 21:42
 */
@Slf4j
public class HbaseUtils {

    public static boolean isTableExist(HBaseAdmin hBaseAdmin, String tableName) throws IOException {
        return hBaseAdmin.tableExists(TableName.valueOf(tableName));
    }

    public static void createTable(HBaseAdmin hBaseAdmin, String tableName, String... columnFamily) throws IOException {
        if (isTableExist(hBaseAdmin, tableName)) {
            log.info("{} is exists;", tableName);
        } else {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));

            for (String column : columnFamily) {
                tableDescriptor.addFamily(new HColumnDescriptor(column));
            }
            log.info("create table {} ,columnFamily: {}", tableName, columnFamily);

            hBaseAdmin.createTable(tableDescriptor);
            log.info("create table {} success!!!", tableName);
        }
    }


    public static void addRowData(String tableName) throws IOException {
        Connection connection = ConnectionFactory.createConnection();

        BufferedMutator bufferedMutator = connection.getBufferedMutator(new BufferedMutatorParams(TableName.valueOf(tableName)));
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));



//        table.
    }

}
