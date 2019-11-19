package cn.com.wavetop.dataone_kafka;

import cn.com.wavetop.dataone_kafka.config.SpringContextUtil;
import cn.com.wavetop.dataone_kafka.consumer.ConsumerHandler;
import cn.com.wavetop.dataone_kafka.entity.SysDbinfo;
import cn.com.wavetop.dataone_kafka.thread.Action;
import cn.com.wavetop.dataone_kafka.utils.SpringJDBCUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DataoneKafkaApplication {


    @Autowired
    private static RestTemplate restTemplate;

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(DataoneKafkaApplication.class, args);
        new SpringContextUtil().setApplicationContext(context);  //获取bean  为了注入kafkaTemplate


        Action action = new Action();
        action.start();

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
