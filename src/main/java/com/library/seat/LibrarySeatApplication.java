package com.library.seat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 图书馆座位预约系统 - 启动类
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.library.seat.mapper")
@EnableScheduling
public class LibrarySeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySeatApplication.class, args);
        System.out.println("========================================");
        System.out.println("  图书馆座位预约系统 启动成功!");
        System.out.println("  访问地址: http://localhost:8080/library");
        System.out.println("  后台管理: http://localhost:8080/library/admin");
        System.out.println("  默认账号: admin / admin123");
        System.out.println("========================================");
    }
}
