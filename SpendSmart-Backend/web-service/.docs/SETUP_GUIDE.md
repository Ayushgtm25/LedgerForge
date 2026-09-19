# API Gateway Setup & Deployment Guide

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- Git
- Docker (optional, for containerized deployment)

### 1. Clone & Build

```bash
# Navigate to workspace
cd D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\web-service

# Build the project
mvn clean install
```

### 2. Start Eureka Server

Before starting the gateway, ensure Eureka Server is running:

```bash
# In a separate terminal
# (Assumes Eureka server is available in another microservice)
cd ../eureka-server
mvn spring-boot:run
```

Server will be available at: `http://localhost:8761`

### 3. Start the API Gateway

```bash
# From the web-service directory
mvn spring-boot:run
```

Gateway will be available at: `http://localhost:8080`

### 4. Verify Health

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "discoveryClient": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

## Configuration Files Generated

### 1. **pom.xml** - Maven Build Configuration
- Spring Boot 4.0.5 parent
- Spring Cloud 2024.0.0 dependency management
- Spring Cloud Gateway starter
- Netflix Eureka Client for service discovery
- Spring Boot Actuator for monitoring

Key Features:
- Java 17 compilation
- No spring-boot-starter-web (uses WebFlux instead)
- All required dependencies for reactive gateway

### 2. **application.yml** - Spring Configuration
Contains:
- Gateway routes for all 8 microservices
- Eureka client configuration
- Actuator endpoint exposure
- Security headers
- Logging configuration

Route Mapping:
- `/auth/**` → AUTH-SERVICE
- `/expenses/**` → EXPENSE-SERVICE
- `/incomes/**` → INCOME-SERVICE
- `/categories/**` → CATEGORY-SERVICE
- `/budgets/**` → BUDGET-SERVICE
- `/analytics/**` → ANALYTICS-SERVICE
- `/recurring/**` → RECURRING-SERVICE
- `/notifications/**` → NOTIFICATION-SERVICE

### 3. **WebServiceApplication.java** - Main Application Class
- `@SpringBootApplication` - Spring Boot entry point
- `@EnableDiscoveryClient` - Registers with Eureka
- Standard main() method

### 4. **CorsGlobalConfiguration.java** - CORS Filter
- Allows React SPA origins (localhost:3000, localhost:5173)
- Permits all HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
- Allows Authorization and Content-Type headers
- Credentials enabled for session management

### 5. **logback-spring.xml** - Logging Configuration
- Console appender for development
- Rolling file appender with daily rotation
- 30-day log retention
- Spring profiles for dev/prod logging levels
- Debug logging for Spring Cloud Gateway

### 6. **Dockerfile** - Container Image
- Multi-stage build for optimized image size
- Eclipse Temurin JRE 17 Alpine base
- Health checks configured
- Port 8080 exposed
- Curl included for health probes

### 7. **docker-compose.yml** - Local Development Stack
- API Gateway service
- Eureka Server service
- Network configuration
- Health checks
- Environment variables

## Deployment Scenarios

### Scenario 1: Local Development

```bash
# Terminal 1: Eureka Server
cd ../eureka-server
mvn spring-boot:run

# Terminal 2: API Gateway
mvn spring-boot:run

# Terminal 3: Test the gateway
curl http://localhost:8080/actuator/health
```

### Scenario 2: Docker Compose (Recommended for Testing)

```bash
# Start the stack
docker-compose up -d

# View logs
docker-compose logs -f api-gateway

# Stop the stack
docker-compose down
```

### Scenario 3: Kubernetes Deployment

1. Build and push image to registry:
```bash
docker build -t registry/spendsmart/api-gateway:v1.0.0 .
docker push registry/spendsmart/api-gateway:v1.0.0
```

2. Create deployment manifest:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 2
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: registry/spendsmart/api-gateway:v1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
          value: http://eureka-server:8761/eureka/
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 40
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

### Scenario 4: JAR Deployment

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/web-0.0.1-SNAPSHOT.jar \
  --eureka.client.serviceurl.defaultzone=http://eureka-server:8761/eureka/ \
  --server.port=8080
```

## Verifying the Setup

### 1. Check Gateway Health

```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. Check Eureka Registration

```bash
curl http://localhost:8761/eureka/apps/api-gateway
```

### 3. Test CORS from Browser Console

```javascript
// Run in browser console when on http://localhost:3000
fetch('http://localhost:8080/actuator/health', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  },
  credentials: 'include'
}).then(r => r.json()).then(console.log);
```

### 4. Test Route to Microservice (Once Available)

```bash
curl -X GET http://localhost:8080/auth/health
```

## Troubleshooting Checklist

- [ ] Eureka Server is running on port 8761
- [ ] API Gateway started without errors
- [ ] Gateway health endpoint returns UP
- [ ] Microservices are registered in Eureka
- [ ] React SPA origin is in CORS allowed list
- [ ] Firewall allows port 8080
- [ ] No port conflicts (lsof -i :8080)
- [ ] Logs show successful Eureka registration

## Monitoring & Logs

### View Gateway Logs

```bash
# Real-time logs during mvn spring-boot:run
# Or from JAR:
tail -f logs/spring.log

# Filter for gateway routes
tail -f logs/spring.log | grep "org.springframework.cloud.gateway"
```

### Key Log Patterns

- Successful startup: `Started WebServiceApplication`
- Eureka registration: `Registering application api-gateway with eureka`
- Route discovery: `No matching routes`

## Next Steps

1. **Deploy Microservices:** Ensure all 8 backend microservices are running
2. **Configure React Frontend:** Update API base URL to `http://localhost:8080`
3. **Load Testing:** Use tools like Apache JMeter or Locust
4. **Monitoring:** Set up Prometheus and Grafana
5. **API Documentation:** Generate OpenAPI specs from downstream services

## Additional Resources

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Spring Boot Actuator](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)

