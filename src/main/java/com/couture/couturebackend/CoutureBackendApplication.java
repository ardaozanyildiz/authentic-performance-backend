package com.couture.couturebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CoutureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoutureBackendApplication.class, args);
    }
}