# ==============================================================================
# Multi-Stage Dockerfile — Digital Printing Queue Management System
# Base Image: Eclipse Temurin Java 21 (Ubuntu Jammy LTS)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

WORKDIR /build

# Cache dependencies by copying pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Production Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

LABEL maintainer="DevOps Team <devops@digitalprint.com>" \
      project="Digital Printing Queue Management System" \
      version="1.0.0"

# Install curl for health checking
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Security: Create non-root user and group
RUN groupadd -g 10001 appgroup && \
    useradd -u 10001 -g appgroup -m -s /bin/bash appuser

WORKDIR /app

# Prepare upload directory with proper permissions
RUN mkdir -p /app/uploads && \
    chown -R appuser:appgroup /app

# Declare persistent volume mount point for file uploads
VOLUME ["/app/uploads"]

# Copy compiled JAR from builder stage
COPY --from=builder --chown=appuser:appgroup /build/target/digital-print-queue-1.0.0.jar app.jar

# Drop privileges to non-root user
USER appuser:appgroup

# Expose Spring Boot HTTP port
EXPOSE 8080

# Configure container health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Configure container-aware JVM memory management
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE="docker"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
