**System Role:** You are an expert Java Spring Boot developer and Cloud Architect. Your task is to generate a fully functional API Gateway for a microservices-based personal finance application named "SpendSmart".

**Project Context & Architecture:**
The application has shifted from a monolithic Thymeleaf MVC frontend to a modern React Single Page Application (SPA). Therefore, the `com.spendsmart.web` module must NOT be a Spring MVC application. Instead, it must be a pure, reactive **Spring Cloud Gateway**.

This gateway will act as the single entry point for the React frontend, routing requests to the following backend microservices via the Eureka Service Registry:
* `AUTH-SERVICE` (handles `/auth/**`)
* `EXPENSE-SERVICE` (handles `/expenses/**`)
* `INCOME-SERVICE` (handles `/incomes/**`)
* `CATEGORY-SERVICE` (handles `/categories/**`)
* `BUDGET-SERVICE` (handles `/budgets/**`)
* `ANALYTICS-SERVICE` (handles `/analytics/**`)
* `RECURRING-SERVICE` (handles `/recurring/**`)
* `NOTIFICATION-SERVICE` (handles `/notifications/**`)

**Technical Stack Requirements:**
* **Language:** Java 17 (or newer)
* **Framework:** Spring Boot 3.x
* **Cloud Dependency:** Spring Cloud (Latest stable release train, e.g., `2023.0.x`)
* **Core Module:** Spring Cloud Gateway (Reactive, strictly *no* `spring-boot-starter-web`)
* **Build Tool:** Maven (`pom.xml`)

**Execution Tasks & Deliverables:**

Please generate the complete source code, configuration files, and project structure for the `api-gateway` (package `com.spendsmart.web`) by strictly following these specifications:

### 1. Project Structure
Create the following directory structure:
```text
api-gateway/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/spendsmart/web/
│   │   │       ├── ApiGatewayApplication.java
│   │   │       └── config/
│   │   │           └── CorsGlobalConfiguration.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml (optional)
```

### 2. Maven Configuration (`pom.xml`)
Generate the `pom.xml` with the following requirements:
* Set the `groupId` to `com.spendsmart` and `artifactId` to `api-gateway`.
* Include the Spring Boot Starter Parent (version 3.2.x or latest stable).
* Include the Spring Cloud dependency management section.
* **CRITICAL:** Add `spring-cloud-starter-gateway`. Do NOT include `spring-boot-starter-web` as the gateway requires Spring WebFlux.
* Add `spring-cloud-starter-netflix-eureka-client` to discover downstream services.
* Add `spring-boot-starter-actuator` for health checks.

### 3. Application Properties (`application.yml`)
Generate the configuration file. It must define the dynamic routing using `lb://` (LoadBalancer) protocol to interact with Eureka.
Include the following configurations:
* **Server Port:** `8080` (Primary entry point for the React frontend).
* **Application Name:** `api-gateway`
* **Eureka Client Configuration:**
    * `register-with-eureka: true`
    * `fetch-registry: true`
    * Default zone URL pointing to `http://localhost:8761/eureka/`
* **Spring Cloud Gateway Routes:** Define routes for EVERY microservice listed in the project context.
    * *Example:* ```yaml
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/auth/**
      ```
    * Ensure you map all 8 core domains (`/auth/**`, `/expenses/**`, `/incomes/**`, `/categories/**`, `/budgets/**`, `/analytics/**`, `/recurring/**`, `/notifications/**`).

### 4. Global CORS Configuration (`CorsGlobalConfiguration.java`)
Since the React SPA will run on a different port (e.g., `localhost:5173` or `localhost:3000`), the gateway MUST handle CORS globally.
* Package: `com.spendsmart.web.config`
* Create a Spring `@Configuration` class.
* Define a `CorsWebFilter` bean that allows:
    * Allowed Origins: `http://localhost:5173`, `http://localhost:3000`, `http://localhost:80` (for prod).
    * Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`.
    * Allowed Headers: `*` (especially `Authorization`, `Content-Type`).
    * Allow Credentials: `true`.

### 5. Main Application Class (`ApiGatewayApplication.java`)
Generate the main entry point class.
* Package: `com.spendsmart.web`
* Add `@SpringBootApplication`.
* Add `@EnableDiscoveryClient` so it registers with Eureka.
* Include the standard `main` method.

### 6. Dockerization (Bonus/Optional)
Provide a standard `Dockerfile` in the root directory to containerize this Gateway. Use an eclipse-temurin or amazoncorretto JDK 17 alpine base image. Ensure it exposes port 8080.

**Final Developer Instruction:** Output the contents of these files clearly, using Markdown code blocks with their respective file names. Ensure the syntax is completely valid, specifically adhering to Spring WebFlux requirements for the Gateway and CORS filter.