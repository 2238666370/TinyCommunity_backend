package com.community;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * ClassName: Application
 * Package: com.community
 * Description:
 *
 * @Author wth
 * @Create 2026/3/3 23:49
 * @Version 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }
}