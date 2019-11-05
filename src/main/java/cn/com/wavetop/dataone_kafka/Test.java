package cn.com.wavetop.dataone_kafka;

import cn.com.wavetop.dataone_kafka.entity.SysDbinfo;
import cn.com.wavetop.dataone_kafka.utils.HttpClientKafkaUtil;
import cn.com.wavetop.dataone_kafka.utils.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yongz
 * @Date 2019/11/1、11:05
 */
public class Test {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        SysDbinfo source = SysDbinfo.builder()
                .host("47.103.108.82")
                .dbname("test")
                .user("root")
                .password("Aa123456.")
                .port(3306L)
                .type(2L)
                .build();

        Map<String, Object> name = new HashMap<>();
        Map<String, String> config = new HashMap<>();
        name.put("name", "file_source_test");
        name.put("config", config);
        config.put("connector.class", "FileStreamSource");
        config.put("tasks.max", "1");
        config.put("file", "/usr/local/soft/kafka_2.12-2.3.0/test/user.sql");
        config.put("topic", "file_source_test");

        String data = JSONUtil.toJSONString(name);

        String delete = HttpClientKafkaUtil.deleteConnectors("192.168.1.187", 8083, "file_source_test");
        System.out.println(delete);

        String connector = HttpClientKafkaUtil.createConnector("192.168.1.187", 8083, data);
        System.out.println(connector);
//        String s1 = HttpClientKafkaUtil.getConnectStatus("192.168.1.187", 8083, "test-a-file-source_21");
//        System.out.println(s1);
        String  s = HttpClientKafkaUtil.getConnectorsDetails("192.168.1.187", 8083);
        System.out.println(s);

        s = HttpClientKafkaUtil.getConnectorsDetails("192.168.1.187", 8083);
        System.out.println(s);

    }
}
