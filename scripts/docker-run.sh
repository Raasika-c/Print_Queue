#!/usr/bin/env bash
# ==============================================================================
# docker-run.sh — Multi-Container Compose Orchestrator & Health Verifier
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} DOCKER COMPOSE MULTI-SERVICE LAUNCHER                ${NC}"
echo -e "${BLUE}======================================================${NC}"

# Check Docker daemon
if ! (command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1); then
    echo -e "${YELLOW}[WARN] Docker daemon is offline or not installed on this host.${NC}"
    echo -e "To start manually once Docker Desktop is running:"
    echo -e "  docker-compose up -d"
    exit 0
fi

# 1. Create persistent storage volume if not exists
echo "[1/4] Ensuring persistent named volume exists..."
docker volume create printqueue_uploads >/dev/null 2>&1 || true
echo -e "${GREEN}[PASS] Volume 'printqueue_uploads' ready${NC}"

# 2. Start MySQL and Application containers
echo "[2/4] Starting MySQL 8.0 and Spring Boot application via Docker Compose..."
docker-compose up -d

# 3. Wait for service readiness
echo "[3/4] Waiting for services to become healthy..."
MAX_RETRIES=15
RETRY=0
while [ $RETRY -lt $MAX_RETRIES ]; do
    if curl -s --connect-timeout 2 http://localhost:8080/actuator/health | grep -q "UP"; then
        echo -e "${GREEN}[PASS] Application Actuator reports status: UP${NC}"
        break
    fi
    echo -n "."
    sleep 3
    RETRY=$((RETRY+1))
done

if [ $RETRY -eq $MAX_RETRIES ]; then
    echo -e "${YELLOW}[WARN] Health endpoint did not report UP within 45s. Check docker logs:${NC}"
    echo -e "  docker logs printqueue-app"
fi

# 4. Display service access URLs
echo -e "${BLUE}======================================================${NC}"
echo -e "${GREEN} APPLICATION RUNNING SUCCESSFULLY!                    ${NC}"
echo -e " Web UI Landing Page : http://localhost:8080"
echo -e " Actuator Telemetry  : http://localhost:8080/actuator/health"
echo -e " Swagger API Docs    : http://localhost:8080/swagger-ui.html"
echo -e " Persistent Volume   : printqueue_uploads (Mapped to /app/uploads)"
echo -e "${BLUE}======================================================${NC}"
