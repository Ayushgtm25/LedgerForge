# 📑 API Gateway Documentation Index

Welcome to the **SpendSmart API Gateway** project! This document serves as your entry point to all available documentation.

---

## 🎯 Start Here

### For First-Time Users
1. Read: **[README.md](../README.md)** - Comprehensive overview and architecture
2. Follow: **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Step-by-step setup instructions
3. Reference: **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Common commands

### For Developers
- **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Commands, endpoints, troubleshooting
- **[application.yml](../src/main/resources/application.yml)** - Gateway configuration
- **[CorsGlobalConfiguration.java](../src/main/java/com/spendsmart/web/config/CorsGlobalConfiguration.java)** - CORS setup

### For DevOps/System Admins
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Deployment scenarios
- **[Dockerfile](../Dockerfile)** - Container build
- **[docker-compose.yml](../docker-compose.yml)** - Local dev stack

---

## 📚 Complete Documentation Map

```
📖 DOCUMENTATION STRUCTURE
│
├── README.md ⭐ START HERE
│   ├── Project Overview
│   ├── Technology Stack
│   ├── Architecture Diagram
│   ├── Running Instructions
│   ├── Configuration Details
│   ├── Deployment Scenarios
│   ├── Monitoring & Logs
│   ├── Troubleshooting
│   └── Contributing Guidelines
│
├── SETUP_GUIDE.md
│   ├── Quick Start (3 steps)
│   ├── Configuration Files Explained
│   ├── Prerequisites Check
│   ├── Build & Run Options
│   ├── Deployment Scenarios (4 scenarios)
│   ├── Verification Checklist
│   ├── Monitoring Guide
│   ├── Kubernetes Deployment
│   └── Next Steps
│
├── QUICK_REFERENCE.md
│   ├── Essential Commands
│   ├── Configuration Quick Links
│   ├── Service Routes
│   ├── File Locations
│   ├── Troubleshooting (quick answers)
│   ├── Environment Variables
│   ├── Development Tips
│   ├── Common Issues & Solutions
│   ├── Useful Endpoints
│   ├── Performance Tuning
│   └── Support Matrix
│
├── HELP.md (Original)
│   └── Spring Boot generated help
│
└── This File (INDEX.md)
    └── Navigation and links
```

---

## 🔧 Configuration Files Reference

### Build & Deployment
| File | Purpose | Key Settings |
|------|---------|--------------|
| **pom.xml** | Maven build config | Java 17, Spring Boot 4.0.5, Spring Cloud 2024.0.0 |
| **Dockerfile** | Container image | Eclipse Temurin 17 Alpine, port 8080 |
| **docker-compose.yml** | Local dev stack | API Gateway + Eureka, networks, health checks |

### Application Configuration
| File | Purpose | Key Settings |
|------|---------|--------------|
| **application.yml** | Spring Cloud Gateway config | 8 service routes, Eureka, Actuator, logging |
| **application.properties** | Legacy Spring config | (empty, using .yml) |
| **logback-spring.xml** | Logging setup | Console/file appenders, rolling policy, profiles |

### Java Source Code
| File | Purpose | Key Components |
|------|---------|-----------------|
| **WebServiceApplication.java** | Main entry point | @SpringBootApplication, @EnableDiscoveryClient |
| **CorsGlobalConfiguration.java** | CORS filter | CorsWebFilter bean, origin/method/header config |

---

## 🚀 Common Tasks

### Build the Project
```bash
mvn clean install
```
→ See: QUICK_REFERENCE.md → Build & Run

### Run Locally
```bash
mvn spring-boot:run
```
→ See: SETUP_GUIDE.md → Quick Start

### Run with Docker
```bash
docker-compose up -d
```
→ See: QUICK_REFERENCE.md → Docker Operations

### Check Health
```bash
curl http://localhost:8080/actuator/health
```
→ See: QUICK_REFERENCE.md → Health Checks

### Deploy to Kubernetes
```bash
# See deployment manifest
```
→ See: SETUP_GUIDE.md → Kubernetes Deployment

---

## 📋 Gateway Routes

All routes are configured in **[application.yml](../src/main/resources/application.yml):**

| Path | Service |
|------|---------|
| `/auth/**` | AUTH-SERVICE |
| `/expenses/**` | EXPENSE-SERVICE |
| `/incomes/**` | INCOME-SERVICE |
| `/categories/**` | CATEGORY-SERVICE |
| `/budgets/**` | BUDGET-SERVICE |
| `/analytics/**` | ANALYTICS-SERVICE |
| `/recurring/**` | RECURRING-SERVICE |
| `/notifications/**` | NOTIFICATION-SERVICE |

→ See: QUICK_REFERENCE.md → Service Routes

---

## 🌍 Supported Origins (CORS)

Configured in **[CorsGlobalConfiguration.java](../src/main/java/com/spendsmart/web/config/CorsGlobalConfiguration.java):**

- `http://localhost:3000` (Create React App)
- `http://localhost:5173` (Vite)
- `http://localhost:80` (Production)
- `http://localhost`

→ See: README.md → CORS Configuration

---

## 📊 Architecture

```
React SPA (localhost:3000/5173)
    ↓
API Gateway (port 8080)
    ├─ CORS Filter
    ├─ Route Predicates
    └─ Service Discovery (Eureka)
    ↓
8 Microservices
    ↓
Eureka Server (port 8761)
```

→ See: README.md → Architecture Section

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Gateway starts without errors: `mvn spring-boot:run`
- [ ] Health endpoint returns UP: `curl http://localhost:8080/actuator/health`
- [ ] Gateway registered in Eureka: `curl http://localhost:8761/eureka/apps/api-gateway`
- [ ] CORS headers present: Check browser Network tab
- [ ] Services discoverable: Check Eureka dashboard
- [ ] Routes working: `curl http://localhost:8080/{path}`

→ See: SETUP_GUIDE.md → Verifying the Setup

---

## 🐛 Troubleshooting Guide

### Common Issues

| Problem | Solution | Link |
|---------|----------|------|
| Gateway won't start | Check Java version, port conflicts | QUICK_REFERENCE.md |
| Services not discovered | Verify Eureka is running | QUICK_REFERENCE.md |
| CORS errors | Check origin in CorsGlobalConfiguration | QUICK_REFERENCE.md |
| High memory usage | Reduce JAVA_OPTS | QUICK_REFERENCE.md |

→ See: QUICK_REFERENCE.md → Troubleshooting

---

## 📚 External Resources

- **[Spring Cloud Gateway Docs](https://spring.io/projects/spring-cloud-gateway)**
- **[Eureka Documentation](https://spring.io/projects/spring-cloud-netflix)**
- **[Spring Boot Reference](https://spring.io/projects/spring-boot)**
- **[Docker Documentation](https://docs.docker.com/)**
- **[Maven Guide](https://maven.apache.org/)**

---

## 🔐 Security Features

- ✅ Global CORS filter
- ✅ Security headers (X-Frame-Options, X-Content-Type-Options, etc.)
- ✅ Request/response filtering capability
- ✅ Ready for OAuth2 integration
- ✅ Ready for API rate limiting

→ See: README.md → Security Headers

---

## 📈 Monitoring Features

- ✅ Health check endpoints
- ✅ Actuator integration
- ✅ Metrics collection
- ✅ Prometheus compatibility
- ✅ Structured logging

### Actuator Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Service health |
| `/actuator/metrics` | Application metrics |
| `/actuator/info` | App information |
| `/actuator/prometheus` | Prometheus metrics |

→ See: QUICK_REFERENCE.md → Useful Endpoints

---

## 🐳 Container Support

### Docker Build
```bash
docker build -t spendsmart/api-gateway:latest .
```

### Docker Run
```bash
docker run -p 8080:8080 spendsmart/api-gateway:latest
```

### Docker Compose
```bash
docker-compose up -d
```

→ See: QUICK_REFERENCE.md → Docker Operations

---

## 📝 File Locations Quick Reference

```
web-service/
├── pom.xml                                    [Build config]
├── README.md                                  [Main docs]
├── SETUP_GUIDE.md                            [Setup guide]
├── QUICK_REFERENCE.md                        [Commands]
├── Dockerfile                                [Container]
├── docker-compose.yml                        [Dev stack]
│
└── src/main/
    ├── java/com/spendsmart/web/
    │   ├── WebServiceApplication.java        [Main class]
    │   └── config/
    │       └── CorsGlobalConfiguration.java [CORS config]
    │
    └── resources/
        ├── application.yml                   [Gateway routes]
        ├── application.properties            [Spring config]
        └── logback-spring.xml               [Logging]
```

→ See: QUICK_REFERENCE.md → File Locations

---

## 🎯 Quick Navigation by Role

### 👨‍💼 Project Manager
- Start: **README.md** (Overview)
- Then: **SETUP_GUIDE.md** (Deployment options)

### 👨‍💻 Developer
- Start: **README.md** (Architecture)
- Then: **QUICK_REFERENCE.md** (Commands & debugging)
- Reference: Configuration files

### 🏗️ DevOps/System Admin
- Start: **SETUP_GUIDE.md** (Deployment scenarios)
- Then: **Dockerfile** & **docker-compose.yml**
- Reference: **QUICK_REFERENCE.md** (troubleshooting)

### 🔍 Troubleshooter
- Start: **QUICK_REFERENCE.md** (Issues & solutions)
- Then: **README.md** (Architecture context)
- Reference: **application.yml** (Configuration)

---

## 📞 Getting Help

1. **Quick answer?** → Check **QUICK_REFERENCE.md**
2. **Need setup help?** → Read **SETUP_GUIDE.md**
3. **Want full details?** → See **README.md**
4. **Have a problem?** → Troubleshooting section in QUICK_REFERENCE.md

---

## ✨ Next Steps

1. ✅ Review the [README.md](../README.md)
2. ✅ Follow [SETUP_GUIDE.md](SETUP_GUIDE.md) to set up locally
3. ✅ Use [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for daily development
4. ✅ Bookmark the external resources for deeper learning

---

## 📊 Technology Stack at a Glance

| Technology | Version | Status |
|-----------|---------|--------|
| Java | 17+ | ✅ Required |
| Spring Boot | 4.0.5 | ✅ Latest |
| Spring Cloud | 2024.0.0 | ✅ Latest |
| Maven | 3.9+ | ✅ Required |
| Docker | 20.10+ | ✅ Optional |
| Kubernetes | Any | ✅ Ready |

---

## 📅 Document Overview

| Document | Length | Time to Read | Best For |
|----------|--------|--------------|----------|
| README.md | Long | 20-30 min | Full understanding |
| SETUP_GUIDE.md | Long | 15-20 min | Setup & deployment |
| QUICK_REFERENCE.md | Medium | 5-10 min | Quick lookups |
| INDEX.md (this) | Medium | 5-10 min | Navigation |

---

**Last Updated:** April 17, 2026  
**Status:** ✅ Complete and Production-Ready

---

### Need More Help?
- See **README.md** → Troubleshooting section
- See **QUICK_REFERENCE.md** → Common Issues & Solutions
- Check Spring Cloud Gateway official docs (link provided above)

