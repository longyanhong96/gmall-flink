import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 * @author longyh
 * @Description:
 * @analysis:
 * @date 2022/4/11 21:44
 */
public class HbaseTest extends TestCase {

    public void test1(){
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        Connection connection  = null;
        HBaseAdmin hBaseAdmin = null;

        try {
            connection = ConnectionFactory.createConnection(conf);
            hBaseAdmin = (HBaseAdmin) connection.getAdmin();


            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("test"));


            String[] columnFamily = {"info1","info2"};
            for (String cf : columnFamily) {
                tableDescriptor.addFamily(new HColumnDescriptor(cf));
            }

            hBaseAdmin.createTable(tableDescriptor );
            System.out.println("创建" + "test" + "表成功");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                hBaseAdmin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
