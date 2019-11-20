package cn.com.wavetop.dataone_kafka.thread;

import cn.com.wavetop.dataone_kafka.consumer.ConsumerHandler;
import cn.com.wavetop.dataone_kafka.utils.TestGetFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yongz
 * @Date 2019/11/17、17:01
 * 监听action目录线程
 */
public class Action extends Thread {

    // 日志
    private static Logger log = LoggerFactory.getLogger(ConsumerHandler.class); // 日志

    // dataone后台安装的监听目录
//    private final String actionDir = "/opt/dataone/sqltemp/ACTION/";
    private final String actionDir = "D:/yongz/dataone/sqltemp/ACTION/";
    private boolean stopMe = true;

    // 存放job任务
    private static Map<String, JobThread00> jobTheads = new HashMap<>();

    public void stopMe() {
        stopMe = false;
    }

    @Override
    public void run() {
        System.out.println(actionDir);
        int index = 1;
        while (stopMe) {
            for (String s : TestGetFiles.getAllFileName(actionDir)) {
                // 跳过此文件夹
                if (s.equals(".error")) {
                    continue;
                }
                BufferedReader br = null;

                Integer jobId = Integer.valueOf(s.split("_")[2]);  // 获取jobId
                String sqlPath = ""; //  sql的路径
                if (s.split("_")[1].equals("start")) {
                    // 开启任务线程
                    try {
                        br = new BufferedReader(new FileReader(actionDir + s));
                        String str;
                        while ((str = br.readLine()) != null) {//逐行读取
                            if (str.contains("sql_file_path")) {
                                sqlPath = str.split("=")[1];// 获取sql的路径
                            }
                        }
                        br.close();//别忘记，切记
                        jobTheads.put("producer_job_" + jobId, new JobThread00(jobId, sqlPath));
                        jobTheads.get("producer_job_" + jobId).start();

                        new File(actionDir + s).delete();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }


                } else if (s.split("_")[1].equals("stop")) {
                    // 关闭任务线程
                    jobTheads.get("job_" + jobId).stopMe();
                } else if (s.split("_")[1].equals("resume")) {
                    // 重启任务线程
                }


            }
            try {
                System.out.println("第" + index++ + "次结束！");
                Thread.sleep(5000);
                System.out.println("当前开启的线程数："+Thread.getAllStackTraces().keySet().size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Action action = new Action();
        action.start();
    }
}
