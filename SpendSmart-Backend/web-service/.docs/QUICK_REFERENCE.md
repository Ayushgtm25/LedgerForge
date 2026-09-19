# Quick Reference Guide - SpendSmart API Gateway

## Essential Commands

### Build & Run
```bash
# Full build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run locally
mvn spring-boot:run

# Build JAR only
mvn clean package
```

### Docker Operations
```bash
# Build image
docker build -t spendsmart/api-gateway:latest .

# Run container
docker run -p 8080:8080 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ spendsmart/api-gateway:latest

# Docker Compose (includes Eureka)
docker-compose up -d
docker-compose logs -f
docker-compose down
```

### Health Checks
```bash
# Gateway health
curl http://localhost:8080/actuator/health

# Eureka registration
curl http://localhost:8761/eureka/apps/api-gateway

# Gateway info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## Configuration Quick Links

| Item | Location | Default |
|------|----------|---------|
| Gateway Port | `application.yml` | `8080` |
| Eureka URL | `application.yml` | `http://localhost:8761/eureka/` |
| CORS Origins | `CorsGlobalConfiguration.java` | localhost:3000, 5173, 80 |
| Logging Level | `application.yml` | INFO |
| Log File Path | `logback-spring.xml` | `./logs/spring.log` |

## Service Routes

```
/auth/**          → AUTH-SERVICE
/expenses/**      → EXPENSE-SERVICE
/incomes/**       → INCOME-SERVICE
/categories/**    → CATEGORY-SERVICE
/budgets/**       → BUDGET-SERVICE
/analytics/**     → ANALYTICS-SERVICE
/recurring/**     → RECURRING-SERVICE
/notifications/** → NOTIFICATION-SERVICE
```

## File Locations

| File | Path |
|------|------|
| Build Config | `pom.xml` |
| Application Config | `src/main/resources/application.yml` |
| CORS Config | `src/main/java/com/spendsmart/web/config/CorsGlobalConfiguration.java` |
| Main Class | `src/main/java/com/spendsmart/web/WebServiceApplication.java` |
| Logging Config | `src/main/resources/logback-spring.xml` |
| Docker Build | `Dockerfile` |
| Docker Compose | `docker-compose.yml` |

## Troubleshooting

### Gateway Won't Start
```bash
# Check port conflict
lsof -i :8080

# Check logs
tail -f logs/spring.log | grep ERROR

# Verify Java version
java -version  # Should be 17+
```

### Services Not Discovered
```bash
# Check Eureka
curl http://localhost:8761/eureka/apps

# Verify gateway registration
curl http://localhost:8761/eureka/apps/api-gateway

# Check application logs
tail -f logs/spring.log | grep "Registering"
```

### CORS Errors
```bash
# Test CORS
curl -i -X OPTIONS \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  http://localhost:8080/auth/health

# Check CorsGlobalConfiguration.java for allowed origins
```

## Environment Variables

```bash
# Eureka Server Location
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

# JVM Heap Size
JAVA_OPTS=-Xmx512m -Xms512m

# Logging Level
LOGGING_LEVEL_ROOT=INFO

# Logging Level for Gateway
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CLOUD_GATEWAY=DEBUG
```

## Development Tips

1. **Enable Debug Logging:**
   - Modify `application.yml`: `logging.level.org.springframework.cloud.gateway: DEBUG`

2. **Test Route Directly:**
   - `curl -v http://localhost:8080/auth/login -H "Content-Type: application/json"`

3. **Monitor Service Discovery:**
   - Visit `http://localhost:8761` → Dashboard → Instances section

4. **Check Request Headers:**
   - Add logging filter in routes config for debugging

5. **View All Metrics:**
   - `curl http://localhost:8080/actuator/metrics | jq`

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| `Connection refused` on port 8080 | Gateway not running, check `mvn spring-boot:run` |
| `Unable to find service` in logs | Microservice not registered with Eureka |
| CORS errors in browser | Update CORS origins in `CorsGlobalConfiguration.java` |
| `Failed to get ServiceInstance` | Eureka server not running on port 8761 |
| High memory usage | Reduce JAVA_OPTS Xmx value |
| Slow response times | Check network latency to Eureka server |

## Useful Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Service health status |
| `/actuator/health/readiness` | Readiness probe |
| `/actuator/health/liveness` | Liveness probe |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Available metrics |
| `/actuator/prometheus` | Prometheus format metrics |
| `/actuator/env` | Environment properties |

## Performance Tuning

```bash
# Optimize for high throughput
JAVA_OPTS=-Xmx1g -Xms1g -XX:+UseG1GC -XX:+ParallelRefProcEnabled

# Optimize for low latency
JAVA_OPTS=-Xmx512m -Xms512m -XX:+UseG1GC

# Enable remote debugging
JAVA_DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

## Support Matrix

| Component | Version | Status |
|-----------|---------|--------|
| Java | 17+ | ✅ Required |
| Spring Boot | 4.0.5 | ✅ Latest |
| Spring Cloud | 2024.0.0 | ✅ Latest |
| Maven | 3.9+ | ✅ Required |
| Docker | 20.10+ | ✅ Optional |

---

**For detailed documentation, see:**
- `README.md` - Full project documentation
- `SETUP_GUIDE.md` - Step-by-step setup instructions
- `application.yml` - Complete configuration reference

