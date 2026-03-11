package com.hs.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 后端启动类
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("=================================");
        System.out.println("厨师上门服务平台 - 后端服务启动成功！");
        System.out.println("访问地址：http://localhost:8080");
        System.out.println("=================================");
    }
}
