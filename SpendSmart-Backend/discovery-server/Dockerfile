# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Build the application
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

WORKDIR /app

# Copy the built JAR from the builder stage (artifactId: discovery)
COPY --from=builder /build/target/discovery-*.jar app.jar

# Create a non-root user for running the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Expose the Eureka server port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar \
  -Dspring.boot.loader.main-class=com.netflix.eureka.server.EurekaServerApplication \
  org.springframework.boot.loader.JarLauncher \
  --management.endpoints.web.exposure.include=health || exit 1

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Start the application
CMD ["java", "-jar", "app.jar"]

