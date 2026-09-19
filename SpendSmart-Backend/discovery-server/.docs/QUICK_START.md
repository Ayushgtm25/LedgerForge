# SpendSmart Eureka Server - Quick Reference

## 🚀 START HERE

### 1. Build
```bash
mvnw.cmd clean install  # Windows
./mvnw clean install    # Linux/Mac
```

### 2. Run
```bash
mvnw.cmd spring-boot:run  # Windows
./mvnw spring-boot:run    # Linux/Mac
```

### 3. Access
```
http://localhost:8761
```

---

## 📋 Essential Configuration

**Port**: 8761  
**App Name**: discovery-server  
**Config File**: `src/main/resources/application.yaml`

---

## 🔗 Key Endpoints

| Endpoint | Purpose |
|----------|---------|
| `http://localhost:8761/` | Eureka Dashboard |
| `http://localhost:8761/actuator/health` | Health Check |
| `http://localhost:8761/eureka/apps` | All Registered Apps |

---

## 📝 Register a Service

In your microservice's `application.yaml`:

```yaml
spring:
  application:
    name: my-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

And in your main class:

```java
@EnableDiscoveryClient
@SpringBootApplication
public class MyServiceApplication {
    // ...
}
```

---

## 🐳 Docker Commands

```bash
# Build
docker build -t spendsmart/eureka-server:latest .

# Run
docker run -p 8761:8761 spendsmart/eureka-server:latest

# Stop
docker stop eureka-server
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8761 in use | Change `server.port` in application.yaml |
| Service not registering | Verify `spring.application.name` and `eureka.client.service-url.defaultZone` |
| Can't access dashboard | Ensure server is running on port 8761 |
| Connection refused | Check firewall and network connectivity |

---

## 📂 Important Files

```
eureka-server/
├── pom.xml                         # Maven configuration
├── Dockerfile                      # Docker build
├── src/main/java/com/spendsmart/eureka/
│   └── EurekaServerApplication.java  # Main application
└── src/main/resources/
    ├── application.yaml            # Server configuration
    └── logback-spring.xml          # Logging configuration
```

---

## 📖 Full Documentation

- **README.md** - Complete usage guide
- **EUREKA_SETUP.md** - Detailed setup and architecture
- **IMPLEMENTATION_SUMMARY.md** - What was implemented

---

## 🔍 Verify Installation

After running the server, check:

1. **Dashboard**: http://localhost:8761/
2. **Health**: http://localhost:8761/actuator/health
3. **Logs**: Check console output for "Started EurekaServerApplication"

---

## 💡 Pro Tips

1. **Development**: `enable-self-preservation: false` for faster feedback
2. **Production**: Set `enable-self-preservation: true` 
3. **Monitoring**: Use actuator endpoints for health checks
4. **Docker**: Use multi-stage build for smaller images
5. **Logging**: Check `logs/eureka-server.log` for issues

---

**Ready to go!** 🎉

