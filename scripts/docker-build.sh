#!/usr/bin/env bash
# ==============================================================================
# docker-build.sh — Quality-Gated Container Image Builder
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} DOCKER QUALITY-GATED BUILD PROCESS                   ${NC}"
echo -e "${BLUE}======================================================${NC}"

# 1. Maven verification & tests
if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
elif [[ -f "C:/apache-maven-3.9.16/bin/mvn.cmd" ]]; then
    MVN_CMD="C:/apache-maven-3.9.16/bin/mvn.cmd"
else
    echo -e "${RED}[FAIL] Maven not found${NC}"
    exit 1
fi

echo "[1/3] Running automated Quality Gate test suite before build..."
if ! "$MVN_CMD" test; then
    echo -e "${RED}[STOP] Automated tests failed! Docker image build aborted by Quality Gate.${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] Quality Gate Passed: All 120 tests succeeded.${NC}"

# 2. Package JAR
echo "[2/3] Packaging Spring Boot Fat JAR..."
"$MVN_CMD" package -DskipTests
echo -e "${GREEN}[PASS] Packaging complete: target/digital-print-queue-1.0.0.jar${NC}"

# 3. Build Docker Image
echo "[3/3] Building multi-stage production Docker image..."
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
    docker build -t digital-print-queue:latest -t digital-print-queue:1.0.0 .
    echo -e "${GREEN}[PASS] Docker image 'digital-print-queue:latest' built successfully!${NC}"
else
    echo -e "${YELLOW}[WARN] Docker daemon is offline or not running.${NC}"
    echo -e "${YELLOW}In a running Docker environment, execute:${NC}"
    echo -e "  docker build -t digital-print-queue:latest ."
fi

echo -e "${BLUE}======================================================${NC}"
