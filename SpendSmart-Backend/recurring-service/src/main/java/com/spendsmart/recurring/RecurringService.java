package com.spendsmart.recurring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RecurringService {

    public static void main(String[] args) {
        SpringApplication.run(RecurringService.class, args);
    }
}
