#!/usr/bin/env bash
# ==============================================================================
# health-check.sh — Automated Project System & Service Health Verification
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} AUTOMATED HEALTH CHECK AUDIT                         ${NC}"
echo -e "${BLUE} Digital Printing Queue Management System             ${NC}"
echo -e "${BLUE}======================================================${NC}"

FAILED=0

# 1. Check Java 21
echo -n "Checking Java 21... "
if command -v java >/dev/null 2>&1; then
    JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$JAVA_VER" =~ ^21 ]]; then
        echo -e "${GREEN}[PASS] (Java $JAVA_VER)${NC}"
    else
        echo -e "${YELLOW}[WARN] (Detected $JAVA_VER, Java 21 Recommended)${NC}"
    fi
else
    echo -e "${RED}[FAIL] Java is not installed or not in PATH${NC}"
    FAILED=1
fi

# 2. Check Apache Maven
echo -n "Checking Apache Maven... "
if command -v mvn >/dev/null 2>&1; then
    MVN_VER=$(mvn -v 2>&1 | awk 'NR==1 {print $3}')
    echo -e "${GREEN}[PASS] (Maven $MVN_VER)${NC}"
elif [[ -f "C:/apache-maven-3.9.16/bin/mvn.cmd" ]]; then
    echo -e "${GREEN}[PASS] (Found at C:/apache-maven-3.9.16/bin/mvn.cmd)${NC}"
else
    echo -e "${RED}[FAIL] Maven is not installed or not in PATH${NC}"
    FAILED=1
fi

# 3. Check Git
echo -n "Checking Git VCS... "
if command -v git >/dev/null 2>&1; then
    GIT_VER=$(git --version | awk '{print $3}')
    echo -e "${GREEN}[PASS] (Git $GIT_VER)${NC}"
else
    echo -e "${RED}[FAIL] Git is not installed${NC}"
    FAILED=1
fi

# 4. Check Docker CLI
echo -n "Checking Docker CLI... "
if command -v docker >/dev/null 2>&1; then
    DOCKER_VER=$(docker --version | awk '{print $3}' | tr -d ',')
    echo -e "${GREEN}[PASS] (Docker $DOCKER_VER)${NC}"
else
    echo -e "${YELLOW}[WARN] Docker CLI not in PATH${NC}"
fi

# 5. Check Docker Daemon
echo -n "Checking Docker Daemon... "
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
    echo -e "${GREEN}[PASS] Docker Daemon is ACTIVE${NC}"
else
    echo -e "${YELLOW}[WARN] Docker Daemon is OFFLINE or inaccessible on this host${NC}"
fi

# 6. Check Port 8080 Availability
echo -n "Checking Port 8080... "
if command -v netstat >/dev/null 2>&1; then
    if netstat -ano | grep -q ":8080 "; then
        echo -e "${YELLOW}[OCCUPIED] Port 8080 is currently in use${NC}"
    else
        echo -e "${GREEN}[AVAILABLE] Port 8080 is free${NC}"
    fi
else
    echo -e "${BLUE}[INFO] netstat not available for port check${NC}"
fi

# 7. Check Spring Boot Actuator Health (if app is running)
echo -n "Checking Application Endpoint (http://localhost:8080/actuator/health)... "
if command -v curl >/dev/null 2>&1; then
    HEALTH_RESP=$(curl -s --connect-timeout 2 http://localhost:8080/actuator/health || true)
    if [[ "$HEALTH_RESP" =~ "UP" ]]; then
        echo -e "${GREEN}[PASS] (Application is UP)${NC}"
    else
        echo -e "${YELLOW}[INACTIVE] Application not running or unreachable on port 8080${NC}"
    fi
else
    echo -e "${BLUE}[INFO] curl not available${NC}"
fi

echo -e "${BLUE}======================================================${NC}"
if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}Prerequisites Health Check: PASSED${NC}"
    exit 0
else
    echo -e "${RED}Prerequisites Health Check: FAILED (Critical tools missing)${NC}"
    exit 1
fi
