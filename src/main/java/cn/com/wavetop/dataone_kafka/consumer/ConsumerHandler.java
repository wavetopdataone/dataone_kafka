package cn.com.wavetop.dataone_kafka.consumer;

import cn.com.wavetop.dataone_kafka.entity.vo.Message;
import cn.com.wavetop.dataone_kafka.utils.JSONUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
 
public class ConsumerHandler {
    static Logger log = Logger.getLogger(ConsumerHandler.class);
    // 本例中使用一个consumer将消息放入后端队列，你当然可以使用前一种方法中的多实例按照某张规则同时把消息放入后端队列
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executors;
    private Properties props;
 
    public ConsumerHandler(String servers,String commit,String intervalms,String timeoutms,String groupId, String topic) {

        props = new Properties();
        System.out.println(servers);
        props.put("bootstrap.servers", servers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", commit);
        props.put("auto.commit.interval.ms", intervalms);

        props.put("auto.offset.reset", "earliest");
//        props.put("session.timeout.ms", timeoutms);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
    }
 
    public void execute(String topic, JdbcTemplate jdbcTemplate) throws Exception {
        while (true) {
            //kafka为空重连
            if(consumer!=null){
                ConsumerRecords<String, String> records = consumer.poll(200);
 
                for (final ConsumerRecord record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    String value = (String)record.value();
                    Message message = JSONUtil.parseObject(value, Message.class);
                    System.out.println(message.getPayload());
                    try {
                        jdbcTemplate.update(message.getPayload());

                    } catch (DataAccessException e) {
                        e.printStackTrace();
                        log.error(message.getPayload());
                    }
                }
            }else{
                consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Arrays.asList(topic));
                System.out.println("hehe");
                return;
            }
        }
    }

    public void stop(){
        if (consumer != null) {
            consumer.wakeup();
        }
    }
 
    public void shutdown() {
        if (consumer != null) {
            consumer.close();
        }
        if (executors != null) {
            executors.shutdown();
        }
        try {
            if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timeout.... Ignore for this case");
            }
        } catch (InterruptedException ignored) {
            System.out.println("Other thread interrupted this shutdown, ignore for this case.");
            Thread.currentThread().interrupt();
        }
    }

//    修改密码
 
}
