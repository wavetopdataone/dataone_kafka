package cn.com.wavetop.dataone_kafka;

import cn.com.wavetop.dataone_kafka.consumer.ConsumerHandler;
import cn.com.wavetop.dataone_kafka.entity.SysDbinfo;
import cn.com.wavetop.dataone_kafka.utils.SpringJDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DataoneKafkaApplication {

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @Autowired
    private static RestTemplate restTemplate;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DataoneKafkaApplication.class, args);
        SysDbinfo source = SysDbinfo.builder()
                .host("47.103.108.82")
                .dbname("test")
                .user("root")
                .password("Aa123456.")
                .port(3306L)
                .type(1L)
                .build();

        JdbcTemplate jdbcTemplate = SpringJDBCUtils.register(source);

        //   开启消费的方法
        ConsumerHandler consumers = new ConsumerHandler("192.168.1.187:9092,192.168.1.170:9092,192.168.1.151:9092", "false", "60", "-1", "test_11_4", "file_source_test");

        consumers.execute("file_source_test", jdbcTemplate);
//        Map map = new HashMap();
//        map.put("id", 1);
//        RestTemplate restTemplate = new RestTemplate();
//        System.out.println(restTemplate);
//        SysDbinfo sysDbinfo = restTemplate.postForObject("http://192.168.1.149:8000/sys_dbinfo/check_dbinfo", map, SysDbinfo.class);
//
//        System.out.println(sysDbinfo);

//        SpringJDBCUtils.register();


    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
