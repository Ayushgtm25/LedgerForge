**System Role:** You are an expert Java Spring Boot developer and Cloud Architect. Your task is to generate a fully functional Spring Cloud Netflix Eureka Server (Service Discovery) for a microservices-based personal finance application named "SpendSmart".

**Project Context & Architecture:**
SpendSmart is a microservices ecosystem modeled on a standard API Gateway and Service Registry pattern. The ecosystem consists of the following independent microservices that will register with this Eureka server:
* `auth-service`
* `expense-service`
* `income-service`
* `category-service`
* `budget-service`
* `analytics-service`
* `recurring-service`
* `notification-service`
* `spendsmart-web` (API Gateway / MVC layer)

**Technical Stack Requirements:**
* **Language:** Java 17 (or newer)
* **Framework:** Spring Boot 3.x
* **Cloud Dependency:** Spring Cloud (Latest stable release train, e.g., `2023.0.x`)
* **Build Tool:** Maven (`pom.xml`)

**Execution Tasks & Deliverables:**

Please generate the complete source code, configuration files, and project structure for the `discovery-server` application by strictly following these specifications:

### 1. Project Structure
Create the following directory structure:
```text
discovery-server/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/spendsmart/discovery/
│   │   │       └── DiscoveryServerApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml (optional, for basic logging)
```

### 2. Maven Configuration (`pom.xml`)
Generate the `pom.xml` with the following requirements:
* Set the `groupId` to `com.spendsmart` and `artifactId` to `discovery-server`.
* Include the Spring Boot Starter Parent (version 3.2.x or latest stable).
* Include the Spring Cloud dependency management section.
* Add the essential dependency: `spring-cloud-starter-netflix-eureka-server`.
* Add `spring-boot-starter-actuator` for health checks and monitoring.
* Include the `spring-boot-maven-plugin` in the build section.

### 3. Application Properties (`application.yml`)
Generate the configuration file. It must explicitly configure the application to act *only* as a server and not attempt to register itself as a client.
Include the following configurations:
* **Server Port:** `8761` (Standard Eureka port).
* **Application Name:** `discovery-server`
* **Eureka Client Configuration:**
    * `register-with-eureka: false`
    * `fetch-registry: false`
    * Set the default zone URL to `http://localhost:8761/eureka/`
* **Eureka Server Configuration:**
    * Set `wait-time-in-ms-when-sync-empty` to `0` for faster local startup.
    * (Optional but recommended) Disable self-preservation for development mode: `enable-self-preservation: false`.

### 4. Main Application Class (`DiscoveryServerApplication.java`)
Generate the main entry point class.
* Package: `com.spendsmart.discovery`
* Add the standard `@SpringBootApplication` annotation.
* Add the critical `@EnableEurekaServer` annotation to activate the service registry.
* Include the standard `public static void main(String[] args)` method with `SpringApplication.run`.

### 5. Dockerization (Bonus/Optional)
Provide a standard `Dockerfile` in the root directory to containerize this Eureka server. Use an eclipse-temurin or amazoncorretto JDK 17 alpine base image. Ensure it exposes port 8761 and runs the generated `.jar` file.

**Final Developer Instruction:** Output the contents of these files clearly, using Markdown code blocks with their respective file names. Ensure the syntax is completely valid and ready to be compiled using `mvn clean install` without any further modifications.