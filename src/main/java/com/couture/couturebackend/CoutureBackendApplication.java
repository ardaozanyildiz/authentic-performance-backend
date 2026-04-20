package com.couture.couturebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // <--- TRÈS IMPORTANT : Active l'arrière-plan
public class CoutureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoutureBackendApplication.class, args);
    }
}