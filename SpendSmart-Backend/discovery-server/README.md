# SpendSmart Discovery Server - Service Discovery

A fully functional Spring Cloud Netflix Eureka Server for the SpendSmart microservices ecosystem. This service acts as the central service registry for all microservices in the SpendSmart platform.

## Overview

The Discovery Server is a critical component of the SpendSmart microservices architecture. It provides:

- **Service Discovery**: Clients register their locations dynamically
- **Health Monitoring**: Real-time health status of registered instances
- **Load Balancing**: Integration with Spring Cloud Load Balancer
- **High Availability**: Support for multiple Eureka server instances

## Prerequisites

- **Java 17+** installed and configured
- **Maven 3.6+** (or use the included Maven wrapper)
- **Docker** (optional, for containerization)

## Quick Start

### 1. Build the Application

Using Maven Wrapper (Windows):
```bash
mvnw.cmd clean install
```

Using Maven Wrapper (Linux/Mac):
```bash
./mvnw clean install
```

Using system Maven:
```bash
mvn clean install
```

### 2. Run the Application

**From IDE**:
- Open `DiscoveryServer.java` in your IDE
- Run as Spring Boot Application

**From Terminal**:
```bash
mvnw.cmd spring-boot:run
```

**From JAR**:
```bash
java -jar target/discovery-*.jar
```

### 3. Access the Eureka Dashboard

Open your browser and navigate to:
```
http://localhost:8761/
```

You should see the Eureka Dashboard with no instances registered initially.

## Configuration

The server is configured via `src/main/resources/application.yml`:

```yaml
server:
  port: 8761                    # Eureka server port

eureka:
  client:
    register-with-eureka: false # Don't register itself as a client
    fetch-registry: false        # Don't fetch registry
  server:
    enable-self-preservation: false  # Disable for faster feedback in dev
```

### Key Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8761 | Server port |
| `eureka.client.register-with-eureka` | false | Server doesn't register as client |
| `eureka.client.fetch-registry` | false | Server doesn't fetch registry |
| `eureka.server.enable-self-preservation` | false | Faster eviction in dev mode |
| `eureka.server.eviction-interval-timer-in-ms` | 3000 | Eviction check interval |

## Registering Services

To register a microservice with this Eureka server:

### 1. Add Dependency (in your service's pom.xml)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 2. Add Annotation

```java
@SpringBootApplication
@EnableDiscoveryClient
public class YourServiceApplication {
    // ...
}
```

### 3. Configure Service

In your service's `application.yml`:

```yaml
spring:
  application:
    name: your-service-name

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 4. Verify Registration

Navigate to `http://localhost:8761/` and you should see your service listed under "Instances currently registered with Eureka".

## API Endpoints

### Management/Monitoring

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/health` | GET | Health status |
| `/actuator/info` | GET | Application info |
| `/actuator/metrics` | GET | Application metrics |

### Eureka Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/eureka/apps` | GET | Get all applications |
| `/eureka/apps/{appName}` | GET | Get app instances |
| `/eureka/apps/{appName}/{instanceId}` | GET | Get specific instance |

## Docker Deployment

### Build Docker Image

```bash
docker build -t spendsmart/discovery-server:latest .
```

### Run Docker Container

```bash
docker run -d \
  -p 8761:8761 \
  --name discovery-server \
  spendsmart/discovery-server:latest
```

### Docker Compose

Add to your `docker-compose.yml`:

```yaml
discovery-server:
  image: spendsmart/discovery-server:latest
  container_name: discovery-server
  ports:
    - "8761:8761"
  environment:
    - JAVA_OPTS=-Xmx512m -Xms256m
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 40s
```

## Project Structure

```
discovery-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/spendsmart/discovery/
│   │   │       └── DiscoveryServer.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/
│           └── com/spendsmart/discovery/
│               └── DiscoveryServerTests.java
├── pom.xml
├── Dockerfile
├── .dockerignore
├── mvnw
├── mvnw.cmd
└── README.md
```

## Dependencies

### Core Dependencies
- `spring-cloud-starter-netflix-eureka-server` - Eureka server
- `spring-boot-starter-web` - Web support
- `spring-boot-starter-actuator` - Monitoring

### Testing
- `spring-boot-starter-test` - Spring Boot tests

See `pom.xml` for complete dependency list.

## Development

### Enable Debug Logging

In `application.yml`:

```yaml
logging:
  level:
    com.netflix.eureka: DEBUG
    com.netflix.discovery: DEBUG
```

### Production Configuration

For production, update these settings in `application.yml`:

```yaml
eureka:
  server:
    enable-self-preservation: true  # Prevent sudden service loss
    eviction-interval-timer-in-ms: 60000  # Check every 60 seconds

logging:
  level:
    root: WARN
    com.netflix.eureka: INFO
```

## Monitoring and Health

The application exposes health endpoints:

```bash
# Health check
curl http://localhost:8761/actuator/health

# Metrics
curl http://localhost:8761/actuator/metrics

# Info
curl http://localhost:8761/actuator/info
```

## Troubleshooting

### Port Already in Use

If port 8761 is already in use:

```yaml
server:
  port: 8762  # Use a different port
```

### Services Not Registering

1. Verify services have correct `defaultZone` URL
2. Check service has `@EnableDiscoveryClient` annotation
3. Ensure `spring.application.name` is set in service config
4. Check logs for connection errors

### Connection Refused

If you get connection refused errors:

1. Ensure Eureka server is running
2. Check firewall settings
3. Verify correct hostname/IP and port
4. Check network connectivity

## Logging

Logs are configured in `logback-spring.xml`:

- **Console**: INFO level
- **File**: `logs/discovery-server.log`
- **Rotation**: Daily or 10MB (whichever comes first)

## License

This project is part of the SpendSmart application ecosystem.

## Support

For issues or questions, please refer to the SpendSmart documentation or create an issue in the project repository.

---

**Ready to use!** The discovery server is fully configured and can be deployed to your microservices infrastructure.

