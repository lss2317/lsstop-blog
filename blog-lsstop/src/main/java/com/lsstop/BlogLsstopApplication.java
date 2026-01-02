package com.lsstop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lsstop.mapper")
@EnableScheduling
public class BlogLsstopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogLsstopApplication.class, args);
    }

}
