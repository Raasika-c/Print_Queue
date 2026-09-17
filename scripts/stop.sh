#!/usr/bin/env bash
# ==============================================================================
# stop.sh — Safe Multi-Container Graceful Shutdown
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} STOPPING SERVICES (PERSISTING VOLUMES)               ${NC}"
echo -e "${BLUE}======================================================${NC}"

if command -v docker-compose >/dev/null 2>&1; then
    docker-compose down
    echo -e "${GREEN}[PASS] Containers stopped gracefully. Volume 'printqueue_uploads' preserved.${NC}"
elif command -v docker >/dev/null 2>&1; then
    docker compose down
    echo -e "${GREEN}[PASS] Containers stopped gracefully. Volume 'printqueue_uploads' preserved.${NC}"
else
    echo -e "${YELLOW}[INFO] Docker not active. Stopping any local Java background processes...${NC}"
fi

echo -e "${BLUE}======================================================${NC}"
