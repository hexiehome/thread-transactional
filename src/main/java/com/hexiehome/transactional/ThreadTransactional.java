package com.hexiehome.transactional;

import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

/**
 * Canal Client SpringBoot 启动类
 *
 * @author cmd
 * @date 2020/3/11
 */
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class ThreadTransactional {

    public static void main(String[] args) {
        log4jConfigure();
        SpringApplication.run(ThreadTransactional.class, args);
    }


    public static void log4jConfigure() {
        // 这里需要注意路径中不要出现中文和空格，如果存在中文，请使用url转码
        try {
            String configLocation = "./../config/log4j2.xml";
            File file = new File(configLocation);
            if (!file.exists()) {
                configLocation = "src/main/resources/log4j2.xml";
            }
            Configurator.initialize("", configLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}