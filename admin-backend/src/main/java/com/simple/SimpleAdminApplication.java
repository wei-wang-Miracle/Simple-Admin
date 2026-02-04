package com.simple;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Simple Admin 应用启动类
 * 
 * SpringBoot 2.7.12 + JDK 1.8 传统 MVC 架构
 */
@SpringBootApplication
@MapperScan("com.simple.modules.*.mapper.*")
@EnableCaching
@EnableAsync
@EnableScheduling
public class SimpleAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleAdminApplication.class, args);
    }
}
