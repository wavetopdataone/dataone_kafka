package cn.com.wavetop.dataone_kafka.producer;

import java.util.Properties;
import java.util.concurrent.Future;

import cn.com.wavetop.dataone_kafka.entity.config.ProducerConf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    private Properties p = new Properties();
    private KafkaProducer kafkaProducer;

    public Producer(ProducerConf producerConf) {
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.187:9092,192.168.1.170:9092,192.168.1.145:9092");//kafka地址，多个地址用逗号分割
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        this.kafkaProducer = new KafkaProducer(p);
    }

    public void sendMsg(String topic, String msg) {
        kafkaProducer.send(new ProducerRecord<String, String>(topic, msg));
    }

    public void stop() {
        kafkaProducer.close();
    }

}