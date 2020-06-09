package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring-cloud-aws-config.xml")
public class S3FileExchangeApp {

    public static void main(final String[] args) {
        SpringApplication.run(S3FileExchangeApp.class, args);
    }

}

