package com.wu.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import com.wu.service.utils.KeyGenerator;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wu"})
@MapperScan("com.wu.service.mapper")
public class AllApplication {
    public static void main(String[] args) throws Exception {
        // 生成服务器公私钥
        KeyGenerator keyGenerator =new KeyGenerator();
        keyGenerator.generateAndStoreKeys();
        SpringApplication.run(AllApplication.class, args);
    }



}

