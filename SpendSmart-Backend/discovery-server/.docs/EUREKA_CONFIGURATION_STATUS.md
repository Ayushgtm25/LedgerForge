# Eureka Configuration Status Audit

Date: 2026-04-17

## Scope
Audited 3 projects:
1. `eureka-server`
2. `auth-service`
3. `expense-service`

Goal: verify whether `auth-service` and `expense-service` are correctly configured to register with the Eureka server.

## Evidence Reviewed
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\eureka-server\pom.xml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\eureka-server\src\main\resources\application.yml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\eureka-server\src\main\java\com\spendsmart\eureka\EurekaServer.java`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\auth-service\pom.xml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\auth-service\src\main\resources\application.yml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\auth-service\src\main\java\com\spendsmart\auth\AuthService.java`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\expense-service\pom.xml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\expense-service\src\main\resources\application.yml`
- `D:\Programs\SprintProjects\SpendSmart\SpendSmart-Backend\expense-service\src\main\java\com\spendsmart\expense\ExpenseServiceApplication.java`

## Executive Status
Overall status: **Configured correctly for local Eureka registration** (localhost-based setup).

- Eureka server configuration: **PASS**
- Auth service Eureka client configuration: **PASS**
- Expense service Eureka client configuration: **PASS**
- Cross-project alignment/operational cautions: **WARN** (see action items)

## Detailed Check Matrix

| Check | Eureka Server | Auth Service | Expense Service | Status |
|---|---|---|---|---|
| Correct discovery dependency in `pom.xml` | `spring-cloud-starter-netflix-eureka-server` present | `spring-cloud-starter-netflix-eureka-client` present | `spring-cloud-starter-netflix-eureka-client` present | PASS |
| Spring Cloud BOM configured | `spring-cloud-dependencies:2023.0.3` | `spring-cloud-dependencies:2023.0.3` | `spring-cloud-dependencies:2023.0.3` | PASS |
| Service app name set | `discovery-server` | `auth-service` | `expense-service` | PASS |
| Eureka URL configured | `http://localhost:8761/eureka/` | `${EUREKA_SERVER_URL:http://localhost:8761/eureka/}` | `${EUREKA_SERVER_URL:http://localhost:8761/eureka/}` | PASS |
| Server is server-only (not a client) | `register-with-eureka: false`, `fetch-registry: false` | N/A | N/A | PASS |
| Client registers and fetches registry | N/A | `register-with-eureka: true`, `fetch-registry: true` | `register-with-eureka: true`, `fetch-registry: true` | PASS |
| Bootstrapping annotations | `@EnableEurekaServer` present | `@EnableDiscoveryClient` present | `@EnableDiscoveryClient` present | PASS |

## Findings

### 1) Eureka server setup is correct
- `eureka-server` has the correct server starter dependency.
- `EurekaServer` class includes `@EnableEurekaServer`.
- `application.yml` correctly sets:
  - `server.port: 8761`
  - `eureka.client.register-with-eureka: false`
  - `eureka.client.fetch-registry: false`
  - `eureka.client.service-url.defaultZone: http://localhost:8761/eureka/`

### 2) Auth service is correctly configured as Eureka client
- `auth-service` includes `spring-cloud-starter-netflix-eureka-client`.
- `AuthService` includes `@EnableDiscoveryClient`.
- `application.yml` correctly sets:
  - `spring.application.name: auth-service`
  - `eureka.client.service-url.defaultZone` to env-driven value with localhost fallback
  - `register-with-eureka: true`
  - `fetch-registry: true`

### 3) Expense service is correctly configured as Eureka client
- `expense-service` includes `spring-cloud-starter-netflix-eureka-client`.
- `ExpenseServiceApplication` includes `@EnableDiscoveryClient`.
- `application.yml` correctly sets:
  - `spring.application.name: expense-service`
  - `eureka.client.service-url.defaultZone` to env-driven value with localhost fallback
  - `register-with-eureka: true`
  - `fetch-registry: true`

## Warnings / Gaps (Not blockers for local setup)

1. **Mixed Spring Boot versions across projects**
   - `eureka-server`: Spring Boot `3.2.5`
   - `auth-service` and `expense-service`: Spring Boot `3.3.5`
   - This can work, but version drift may cause operational inconsistency over time.

2. **`localhost` default Eureka URL is environment-sensitive**
   - Works when running all apps on same host.
   - In Docker/Kubernetes or distributed hosts, services must override `EUREKA_SERVER_URL`.

3. **Artifact naming consistency**
   - Server uses artifactId `eureka` while app name is `discovery-server`.
   - Not functionally broken, but naming mismatch can confuse release/deployment pipelines.

## What Needs To Be Done Further

### Priority 1 (Recommended)
1. **Standardize Spring Boot version across all three projects**
   - Choose one compatible version line and align server + services.

2. **Set environment-specific Eureka URL in deployment configs**
   - Ensure `EUREKA_SERVER_URL` is set per environment (dev/stage/prod/container).

### Priority 2 (Operational hygiene)
3. **Unify naming conventions**
   - Align artifactId/service naming (`eureka` vs `discovery-server`) for clarity.

4. **Run an integration startup check in each environment**
   - Start server, then services, and verify both services appear in Eureka dashboard.

## Final Conclusion
Both services (`auth-service`, `expense-service`) are **properly configured to register with the Eureka server** based on current code/configuration. No immediate configuration defects were found for local execution. Remaining items are consistency and environment-hardening improvements.

