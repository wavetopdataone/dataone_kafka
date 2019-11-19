package cn.com.wavetop.dataone_kafka.consumer;

import cn.com.wavetop.dataone_kafka.entity.vo.Message;
import cn.com.wavetop.dataone_kafka.utils.JSONUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsumerHandler {

    private static Logger log = LoggerFactory.getLogger(ConsumerHandler.class); // 日志

    // 本例中使用一个consumer将消息放入后端队列，你当然可以使用前一种方法中的多实例按照某张规则同时把消息放入后端队列
    private KafkaConsumer<String, String> consumer;
    private ExecutorService executors;
    private Properties props;

    public ConsumerHandler(String servers, String commit, String intervalms, String timeoutms, String groupId, String topic) {

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

    /**
     * 以前直接读file文件
     *
     * @param topic
     * @param jdbcTemplate
     * @throws Exception
     */
    public void execute(String topic, JdbcTemplate jdbcTemplate, int jobId) throws Exception {
        while (true) {
            //kafka为空重连
            if (consumer != null) {
                ConsumerRecords<String, String> records = consumer.poll(200);

                for (final ConsumerRecord record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    String value = (String) record.value();
                    Message message = JSONUtil.parseObject(value, Message.class);
                    System.out.println(message.getPayload());
                    try {
                        jdbcTemplate.update(message.getPayload());

                    } catch (DataAccessException e) {
                        e.printStackTrace();
                        log.error(message.getPayload());
                    }
                }
            } else {
                consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Arrays.asList(topic));
                System.out.println("hehe");
                return;
            }
        }
    }

    /**
     * 最新的重载  用java直接写入topic的，然后执行
     *
     * @param jdbcTemplate
     * @param topic
     * @throws Exception
     */
    public void execute(JdbcTemplate jdbcTemplate, String topic, int jobId) throws Exception {
        //kafka为空重连
        if (consumer != null) {
            ConsumerRecords<String, String> records = consumer.poll(200);

            for (final ConsumerRecord record : records) {
//                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
//                String value = (String) record.value();
//                System.out.println(value);
                try {
                    jdbcTemplate.update((String) record.value());
                    log.info("The consumer_job" + jobId + " Thread, message is :" + record.value());
                } catch (Exception e) {
                    log.error(e.getMessage() + "::" + record.value());
                }
            }
        } else {
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(topic));
            log.error("kafka连接失败！————ConsumerHandler--102");
            return;
        }
    }

    public void stop() {
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
