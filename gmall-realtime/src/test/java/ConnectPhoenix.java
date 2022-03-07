import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.ds.druid.DruidDSFactory;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.db.ds.tomcat.TomcatDSFactory;
import cn.hutool.setting.Setting;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/3/4 23:18
 */
public class ConnectPhoenix {

    @Test
    public void test1() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");

            String url = "jdbc:phoenix:192.168.106.101:2181";
            conn = DriverManager.getConnection(url);

//            String sql = "CREATE TABLE user02 (id varchar PRIMARY KEY,INFO.name varchar ,INFO.passwd varchar)";
//
//            PreparedStatement ps = conn.prepareStatement(sql);
//
//             execute
//            ps.execute();

            System.out.println("create success...");


            String insertSql = "upsert into user01(id, INFO.name, INFO.passwd) values('001', 'admin', 'admin')";

            PreparedStatement ps = conn.prepareStatement(insertSql);

            // execute upsert
            String msg = ps.executeUpdate() > 0 ? "insert success..."
                    : "insert fail...";

            // you must commit
            conn.commit();
            System.out.println(msg);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void test2() throws SQLException {
        Setting setting = new Setting("E:\\GitHub\\my\\gmall-flink\\gmall-realtime\\src\\main\\resources\\phoenix.properties");
        DataSource ds = DSFactory.create(setting).getDataSource();

        Connection conn = ds.getConnection();
        try {
            conn = ds.getConnection();

            String insertSql = "upsert into user01(id, INFO.name, INFO.passwd) values('002', 'admin', 'admin')";

            PreparedStatement ps = conn.prepareStatement(insertSql);

            // execute upsert
            String msg = ps.executeUpdate() > 0 ? "insert success..."
                    : "insert fail...";

            // you must commit
            conn.commit();
            System.out.println(msg);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
