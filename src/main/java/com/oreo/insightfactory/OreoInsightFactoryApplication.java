package com.oreo.insightfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OreoInsightFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OreoInsightFactoryApplication.class, args);
    }
}
