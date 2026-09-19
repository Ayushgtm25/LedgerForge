/**
 * ExpenseService is the Spring Boot entry point for the Expense Service.
 *
 * This service is responsible for managing expense records in the SpendSmart platform.
 * It integrates with the Budget Service for real-time spent amount tracking and enforces
 * strict data isolation via JWT-based user context.
 *
 * The service is registered with Eureka Service Discovery and sits behind an API Gateway.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class ExpenseService {

    /**
     * Configure and provide a RestTemplate bean for inter-service communication.
     * This bean is used to make synchronous HTTP calls to the budget-service.
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ExpenseService.class, args);
    }

}
