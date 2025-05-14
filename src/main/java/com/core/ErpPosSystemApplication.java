package com.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 스케줄링 활성화
public class ErpPosSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpPosSystemApplication.class, args);
    }

}
