package cn.com.wavetop.dataone_kafka.thread;

import cn.com.wavetop.dataone_kafka.config.SpringContextUtil;
import cn.com.wavetop.dataone_kafka.consumer.ConsumerHandler;
import cn.com.wavetop.dataone_kafka.entity.SysDbinfo;
import cn.com.wavetop.dataone_kafka.utils.SpringJDBCUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @Author yongz
 * @Date 2019/11/18、19:07
 */
public class JobConsumerThread extends Thread {
    // 任务id
    private Integer jodId;

    // 关闭线程的标识
    private boolean stopMe = true;

    // 注入
    RestTemplate restTemplate = (RestTemplate) SpringContextUtil.getBean("restTemplate");

    public JobConsumerThread(Integer jodId) {
        this.jodId = jodId;
    }

    @Override
    public void run() {

//        restTemplate.getForObject("192.168.1.1");

        SysDbinfo source = restTemplate.getForObject("http://192.168.1.226:8000/toback/findById?jobId=" + jodId, SysDbinfo.class);
        System.out.println(source);

        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = SpringJDBCUtils.register(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConsumerHandler consumers = new ConsumerHandler("192.168.1.187:9092,192.168.1.170:9092,192.168.1.145:9092", "true", "60", "-1", "JodId_" + jodId, "JodId_" + jodId);

        try {
            while (stopMe) {
                consumers.execute(jdbcTemplate, "JodId_" + jodId, jodId);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (stopMe) {

        }
    }

    // 关闭当前线程
    public void stopMe() {
        stopMe = false;
    }

    public static void main(String[] args) {
        new JobConsumerThread(80).start();
    }
}
