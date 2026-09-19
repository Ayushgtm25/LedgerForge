# SpendSmart API Gateway

A Spring Cloud Gateway implementation serving as the single entry point for the SpendSmart microservices-based personal finance application with React SPA frontend.

## Overview

This API Gateway is a reactive Spring Cloud Gateway that routes requests from the React frontend to multiple backend microservices via Eureka Service Registry. It provides centralized routing, CORS handling, and service discovery.

## Architecture

```
React SPA (localhost:3000/5173)
           ↓
    API Gateway (port 8080)
           ↓
    ┌──────┴──────┬──────────┬──────────┬──────────┐
    ↓             ↓          ↓          ↓          ↓
AUTH-SERVICE  EXPENSE-   INCOME-    CATEGORY-  BUDGET-
              SERVICE    SERVICE    SERVICE    SERVICE
                                      ↓          ↓
                                  ANALYTICS- RECURRING-
                                  SERVICE    SERVICE
                                      ↓
                                NOTIFICATION-
                                SERVICE
```

## Technology Stack

- **Java:** 17
- **Framework:** Spring Boot 4.0.5
- **Spring Cloud:** 2024.0.0
- **Core Component:** Spring Cloud Gateway (Reactive WebFlux)
- **Service Discovery:** Eureka Client
- **Build Tool:** Maven
- **Container:** Docker (optional)

## Project Structure

```
api-gateway/
├── pom.xml                                          # Maven configuration
├── Dockerfile                                        # Container image definition
├── docker-compose.yml                               # Local development stack
├── README.md                                         # This file
├── src/
│   ├── main/
│   │   ├── java/com/spendsmart/web/
│   │   │   ├── WebServiceApplication.java          # Main entry point with @EnableDiscoveryClient
│   │   │   └── config/
│   │   │       └── CorsGlobalConfiguration.java    # Global CORS configuration
│   │   └── resources/
│   │       ├── application.yml                      # Gateway routing & service configuration
│   │       └── logback-spring.xml                   # Logging configuration
│   └── test/
│       └── java/com/spendsmart/web/
│           └── WebServiceApplicationTests.java     # Basic test class
```

## Configuration

### Gateway Routes

The gateway routes requests to 8 microservices:

| Path | Service | Load Balancer URL |
|------|---------|------------------|
| `/auth/**` | AUTH-SERVICE | `lb://AUTH-SERVICE` |
| `/expenses/**` | EXPENSE-SERVICE | `lb://EXPENSE-SERVICE` |
| `/incomes/**` | INCOME-SERVICE | `lb://INCOME-SERVICE` |
| `/categories/**` | CATEGORY-SERVICE | `lb://CATEGORY-SERVICE` |
| `/budgets/**` | BUDGET-SERVICE | `lb://BUDGET-SERVICE` |
| `/analytics/**` | ANALYTICS-SERVICE | `lb://ANALYTICS-SERVICE` |
| `/recurring/**` | RECURRING-SERVICE | `lb://RECURRING-SERVICE` |
| `/notifications/**` | NOTIFICATION-SERVICE | `lb://NOTIFICATION-SERVICE` |

### CORS Configuration

Global CORS is enabled for React SPA development:
- **Allowed Origins:** 
  - `http://localhost:5173` (Vite default)
  - `http://localhost:3000` (Create React App default)
  - `http://localhost:80` (Production)
  - `http://localhost`
- **Allowed Methods:** GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Allowed Headers:** All (`*`)
- **Allow Credentials:** `true`
- **Max Age:** 3600 seconds

### Eureka Discovery

The gateway registers with Eureka and fetches the service registry:
```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## Running the Application

### Prerequisites

- Java 17+
- Maven 3.9+
- Eureka Server running on `http://localhost:8761`

### Local Development

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the gateway:**
   - Gateway: `http://localhost:8080`
   - Actuator Health: `http://localhost:8080/actuator/health`
   - Eureka Discovery: `http://localhost:8080/actuator/env` (for debugging)

### Docker Deployment

1. **Build the Docker image:**
   ```bash
   docker build -t spendsmart/api-gateway:latest .
   ```

2. **Run with Docker:**
   ```bash
   docker run -p 8080:8080 \
     -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ \
     spendsmart/api-gateway:latest
   ```

3. **Using Docker Compose (with Eureka server):**
   ```bash
   docker-compose up -d
   ```

## Actuator Endpoints

The following actuator endpoints are exposed for monitoring:

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus metrics (if enabled) |

## Security Headers

The gateway automatically adds these security headers to all responses:
```
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
```

## Request/Response Flow

1. React frontend sends request to `http://localhost:8080/api/path`
2. Gateway receives the request and checks predicate matchers
3. Request is routed to the appropriate microservice via load balancer
4. Microservice processes and returns response
5. Gateway forwards response back to frontend
6. CORS headers are automatically added if needed

## Logging Configuration

Logging is configured via `logback-spring.xml` with:
- **Console Output:** For development
- **File Output:** Rolling files with daily rotation and 30-day retention
- **Debug Level:** `org.springframework.cloud.gateway`
- **Info Level:** Application and Spring default loggers

Profiles:
- **dev:** DEBUG level logging to console and file
- **prod:** WARN level logging to file only

## Performance & Reliability

- **Circuit Breaker:** Ready for Spring Cloud Circuit Breaker integration
- **Health Checks:** Eureka instance health checks every 30 seconds
- **Request Timeout:** Configurable in route filters
- **Load Balancing:** Automatic round-robin via Spring Cloud LoadBalancer
- **Service Discovery:** Automatic service instance discovery

## Development Tips

1. **Debugging Routes:**
   - Enable DEBUG logging for `org.springframework.cloud.gateway`
   - Check Eureka dashboard at `http://localhost:8761`

2. **Testing CORS:**
   ```bash
   curl -H "Origin: http://localhost:3000" \
        -H "Access-Control-Request-Method: POST" \
        -H "Access-Control-Request-Headers: Content-Type" \
        -X OPTIONS http://localhost:8080/auth/login -v
   ```

3. **Service Registry Debugging:**
   - Check registered instances: `http://localhost:8761`
   - View gateway instance: Filter by "api-gateway"

## Troubleshooting

### Services Not Discovered
- Ensure Eureka Server is running on `http://localhost:8761`
- Check microservice names match exactly in `application.yml`
- Verify microservices have `@EnableDiscoveryClient`

### CORS Errors
- Verify React frontend origin is in `CorsGlobalConfiguration`
- Check browser console for specific CORS error messages
- Ensure preflight requests (OPTIONS) are handled

### Gateway Connection Refused
- Check port 8080 is not in use
- Verify Spring Cloud Gateway dependency is included
- Check for missing Spring WebFlux configuration

## Environment Variables

```bash
# Eureka Server Configuration
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# JVM Settings
JAVA_OPTS=-Xmx512m -Xms512m

# Logging Level
LOGGING_LEVEL_ROOT=INFO
```

## Dependencies

Key dependencies managed by Spring Cloud and Spring Boot:
- `spring-cloud-starter-gateway` - API Gateway
- `spring-cloud-starter-netflix-eureka-client` - Service Discovery
- `spring-boot-starter-actuator` - Health & Monitoring
- `spring-boot-starter-test` - Testing Framework

## Building from Source

```bash
# Clean build
mvn clean build

# Build without running tests
mvn clean install -DskipTests

# Package as JAR
mvn clean package

# View dependency tree
mvn dependency:tree
```

## Contributing

When adding new microservices:

1. Add route configuration in `application.yml`:
   ```yaml
   - id: new-service
     uri: lb://NEW-SERVICE
     predicates:
       - Path=/newservice/**
     filters:
       - RewritePath=/newservice/(?<segment>.*), /$\{segment}
   ```

2. Ensure microservice registers with Eureka using `@EnableDiscoveryClient`

3. Test CORS with the React frontend

## License

Internal - SpendSmart Project

## Support

For issues or questions regarding the API Gateway:
- Check Eureka dashboard: `http://localhost:8761`
- Review logs: Check console output and log files
- Verify gateway health: `http://localhost:8080/actuator/health`

