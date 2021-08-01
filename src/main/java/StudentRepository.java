import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentRepository {
    private Configuration configuration = HBaseConfiguration.create();
    private Connection conn;

    public StudentRepository(){
        try {
            conn = ConnectionFactory.createConnection(configuration);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void createTable(String tb, List<String> columnFamily) throws IOException{
        Admin admin = conn.getAdmin();

        TableName tableName = TableName.valueOf(tb);
        if(admin.tableExists(tableName))
            return;

        List<ColumnFamilyDescriptor> familyDescriptors = new ArrayList<>(columnFamily.size());
        columnFamily.forEach(x->{
            familyDescriptors.add(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(x)).build());
        });

        TableDescriptor td = TableDescriptorBuilder.newBuilder(tableName)
                .setColumnFamilies(familyDescriptors)
                .build();

        admin.createTable(td);
    }

    public void put(String tableName, String rowKey, String familyName, Map<String,String> dataMap) throws IOException{
        Put put = new Put(Bytes.toBytes(rowKey));

        Table tb = conn.getTable(TableName.valueOf(tableName));
        dataMap.forEach((k,v)->{
            put.addColumn(Bytes.toBytes(familyName),Bytes.toBytes(k),Bytes.toBytes(v));
        });

        tb.put(put);
    }

    public void deleteByColumn(String tableName, String rowKey, String familyName,String columnName) throws IOException{
        Table tb = conn.getTable(TableName.valueOf(tableName));

        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumn(Bytes.toBytes(familyName),Bytes.toBytes(columnName));

        tb.delete(delete);
    }

    public void deleteByRow(String tableName, String rowKey) throws IOException{
        Table tb = conn.getTable(TableName.valueOf(tableName));

        Delete delete = new Delete(Bytes.toBytes(rowKey));

        tb.delete(delete);
    }

    public Map<String,String> query(String tableName,String rowKey) throws IOException{
        Map<String,String> result = new HashMap<>();

        Get get = new Get(Bytes.toBytes(rowKey));

        Table table = conn.getTable(TableName.valueOf(tableName));
        Result hTableResult = table.get(get);
        if (hTableResult != null && !hTableResult.isEmpty()) {
            for (Cell cell : hTableResult.listCells()) {
                result.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
        }

        return result;
    }
}
