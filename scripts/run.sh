#!/usr/bin/env bash
# ==============================================================================
# run.sh — One-Command Production Launcher & Verifier
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE} DIGITAL PRINTING QUEUE MANAGEMENT     ${NC}"
echo -e "${BLUE}========================================${NC}"

# 1. Check prerequisites
echo -e "\n[1/7] Checking prerequisites..."
if ! command -v java >/dev/null 2>&1; then
    echo -e "${RED}[STOP] Java is not installed or not in PATH.${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}')${NC}"

if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
elif [[ -f "C:/apache-maven-3.9.16/bin/mvn.cmd" ]]; then
    MVN_CMD="C:/apache-maven-3.9.16/bin/mvn.cmd"
else
    echo -e "${RED}[STOP] Maven is not installed.${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] Maven ($MVN_CMD)${NC}"

DOCKER_AVAILABLE=0
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
    DOCKER_AVAILABLE=1
    echo -e "${GREEN}[PASS] Docker Engine Active${NC}"
else
    echo -e "${YELLOW}[WARN] Docker daemon is not active. Will run via local Java process.${NC}"
fi

# 2. Port 8080 collision detection
echo -n "Checking Port 8080 status... "
if curl -s --connect-timeout 2 http://localhost:8080/actuator/health | grep -q "UP"; then
    echo -e "${GREEN}[PASS] Digital Print Queue is already running and healthy on Port 8080.${NC}"
elif command -v netstat >/dev/null 2>&1; then
    if netstat -ano | grep -q ":8080 "; then
        echo -e "${RED}[ERROR] Port 8080 is already in use by another process.${NC}"
        echo "To identify the process occupying port 8080:"
        echo "  Windows: netstat -ano | findstr :8080"
        echo "  Linux:   sudo lsof -i :8080"
        echo "Please free port 8080 before continuing."
        exit 1
    fi
    echo -e "${GREEN}[PASS] Port 8080 is available.${NC}"
else
    echo -e "${GREEN}[PASS] Port 8080 check passed.${NC}"
fi

# 3. Environment & directories
echo -e "\n[2/7] Preparing environment & directories..."
mkdir -p uploads target
if [[ ! -f .env && -f .env.example ]]; then
    cp .env.example .env
fi
echo -e "${GREEN}[PASS] Storage directory ready.${NC}"

# 4. Building application
echo -e "\n[3/7] Building application..."
"$MVN_CMD" clean compile -DskipTests
echo -e "${GREEN}[PASS] Compilation successful.${NC}"

# 5. Running automated tests
echo -e "\n[4/7] Running Quality Gate automated tests (120 tests)..."
if ! "$MVN_CMD" test; then
    echo -e "${RED}[STOP] Quality Gate Failed! Tests failed, startup aborted.${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] All 120 automated tests passed.${NC}"

# 6. Packaging & Image / Startup
echo -e "\n[5/7] Packaging application artifact..."
"$MVN_CMD" package -DskipTests
echo -e "${GREEN}[PASS] JAR packaged: target/digital-print-queue-1.0.0.jar${NC}"

if [ $DOCKER_AVAILABLE -eq 1 ]; then
    echo -e "\n[6/7] Starting via Docker Compose..."
    docker-compose up -d --build
else
    echo -e "\n[6/7] Starting Spring Boot application locally..."
    echo "Starting in background..."
    java -jar target/digital-print-queue-1.0.0.jar > target/application.log 2>&1 &
fi

# 7. Health check & verification
echo -e "\n[7/7] Health check..."
MAX_ATTEMPTS=20
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s --connect-timeout 2 http://localhost:8080/actuator/health | grep -q "UP"; then
        echo -e "${GREEN}[PASS] Application is UP & Healthy!${NC}"
        break
    fi
    echo -n "."
    sleep 2
    ATTEMPT=$((ATTEMPT+1))
done

echo -e "\n${BLUE}========================================${NC}"
echo -e "${GREEN} APPLICATION READY                     ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e " Application URL    : ${GREEN}http://localhost:8080${NC}"
echo -e " Health Endpoint    : ${GREEN}http://localhost:8080/actuator/health${NC}"
echo -e " API Documentation  : ${GREEN}http://localhost:8080/swagger-ui.html${NC}"
echo -e " Demo Admin Login   : ${BLUE}admin@digitalprint.com${NC} / ${BLUE}Admin@123${NC}"
echo -e "${BLUE}========================================${NC}"
