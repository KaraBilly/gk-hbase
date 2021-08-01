import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Program {
    public static void main(String[] args) throws IOException {
        StudentRepository repo = new StudentRepository();

        repo.createTable("billy:student", Arrays.asList("info","score"));
        Map<String,String> data = new HashMap<>();
        data.put("student_id","202107350101310");
        data.put("class","2");
        repo.put("billy:student","qiaoshengfei","info",data);
        repo.query("billy:student","qiaoshengfei");
        repo.deleteByRow("billy:student","qiaoshengfei");
    }
}
