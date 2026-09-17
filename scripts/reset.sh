#!/usr/bin/env bash
# ==============================================================================
# reset.sh — Guarded Clean Reset with Explicit Confirmation Prompt
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${RED}======================================================${NC}"
echo -e "${RED} CAUTION: ENVIRONMENT & DATA RESET INITIATED          ${NC}"
echo -e "${RED}======================================================${NC}"
echo -e "${YELLOW}WARNING:${NC}"
echo "This operation will stop running application containers and optionally"
echo "delete stored uploaded files and persistent database volumes."
echo ""
read -r -p "Are you sure you want to proceed with reset? [y/N]: " CONFIRMATION

if [[ ! "$CONFIRMATION" =~ ^[Yy]$ ]]; then
    echo -e "${BLUE}Reset operation cancelled by user. No data was modified.${NC}"
    exit 0
fi

read -r -p "Do you also wish to permanently DELETE persistent storage volumes? [y/N]: " VOL_CONFIRM

if command -v docker-compose >/dev/null 2>&1; then
    if [[ "$VOL_CONFIRM" =~ ^[Yy]$ ]]; then
        echo -e "${RED}Stopping containers and removing volumes...${NC}"
        docker-compose down -v
        echo -e "${GREEN}[PASS] Containers and persistent volumes removed.${NC}"
    else
        echo -e "${YELLOW}Stopping containers but PRESERVING volumes...${NC}"
        docker-compose down
        echo -e "${GREEN}[PASS] Containers stopped. Volumes kept safe.${NC}"
    fi
else
    echo -e "${YELLOW}Cleaning local Maven target directory...${NC}"
    rm -rf target/
    echo -e "${GREEN}[PASS] Local build target cleaned.${NC}"
fi

echo -e "${BLUE}======================================================${NC}"
