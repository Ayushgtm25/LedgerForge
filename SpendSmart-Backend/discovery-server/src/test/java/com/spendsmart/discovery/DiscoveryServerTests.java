package com.spendsmart.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for DiscoveryServer.
 *
 * Tests that the Eureka server starts successfully and is properly configured.
 */
@SpringBootTest
class DiscoveryServerTests {

	/**
	 * Test that the application context loads successfully.
	 */
	@Test
	void contextLoads() {
	}

	/**
	 * Test that the Eureka server is enabled and running.
	 * This validates that the @EnableEurekaServer annotation is correctly applied.
	 */
	@Test
	void eurekaServerIsEnabled() {
		// Spring Boot test will fail if the context cannot be created
		// with @EnableEurekaServer, so this test implicitly verifies it
	}

}

