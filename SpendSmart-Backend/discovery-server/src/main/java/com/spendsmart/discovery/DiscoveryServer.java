package com.spendsmart.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * SpendSmart Discovery Server - Service Registry
 *
 * This is the central Eureka server for the SpendSmart microservices ecosystem.
 * All microservices register themselves with this server to enable service discovery
 * and load balancing across the infrastructure.
 *
 * Services that will register:
 * - auth-service
 * - expense-service
 * - income-service
 * - category-service
 * - budget-service
 * - analytics-service
 * - recurring-service
 * - notification-service
 * - spendsmart-web (API Gateway)
 *
 * Dashboard: http://localhost:8761/
 *
 * @author SpendSmart Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServer {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServer.class, args);
	}

}

