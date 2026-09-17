#!/usr/bin/env bash
# ==============================================================================
# setup.sh — Automated Environment Initialization & Directory Provisioning
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} PROJECT INITIALIZATION & ENVIRONMENT SETUP           ${NC}"
echo -e "${BLUE}======================================================${NC}"

# 1. Create uploads and storage directories
echo "[1/4] Provisioning storage directories..."
mkdir -p uploads
mkdir -p target
echo -e "${GREEN}[PASS] Directory 'uploads' created/verified${NC}"

# 2. Setup environment file (.env)
echo "[2/4] Initializing environment configuration..."
if [[ ! -f .env ]]; then
    if [[ -f .env.example ]]; then
        cp .env.example .env
        echo -e "${GREEN}[PASS] Created .env from .env.example template${NC}"
    else
        echo -e "${BLUE}[INFO] .env.example template not found, skipping${NC}"
    fi
else
    echo -e "${GREEN}[PASS] Existing .env file detected${NC}"
fi

# 3. Verify Maven wrapper or local Maven installation
echo "[3/4] Checking Maven build tool..."
if command -v mvn >/dev/null 2>&1; then
    MVN_CMD="mvn"
elif [[ -f "C:/apache-maven-3.9.16/bin/mvn.cmd" ]]; then
    MVN_CMD="C:/apache-maven-3.9.16/bin/mvn.cmd"
else
    echo "Maven not found in PATH or standard path"
    exit 1
fi
echo -e "${GREEN}[PASS] Maven verified: $MVN_CMD${NC}"

# 4. Resolve dependencies and compile classes
echo "[4/4] Validating dependencies and compiling source code..."
"$MVN_CMD" clean compile -DskipTests
echo -e "${GREEN}[PASS] Project setup and compilation successfully completed!${NC}"
echo -e "${BLUE}======================================================${NC}"
