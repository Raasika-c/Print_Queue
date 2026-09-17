#!/usr/bin/env bash
# ==============================================================================
# ansible-deploy.sh — Automated Configuration Management & Deployment
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE} ANSIBLE INFRASTRUCTURE DEPLOYMENT ORCHESTRATION      ${NC}"
echo -e "${BLUE}======================================================${NC}"

# 1. Validate Ansible tool availability
echo "[1/5] Checking Ansible installation..."
if ! command -v ansible-playbook >/dev/null 2>&1; then
    echo -e "${YELLOW}[WARN] 'ansible-playbook' command not found in current PATH.${NC}"
    echo -e "${YELLOW}In Windows 11, Ansible is typically executed via WSL2 or a Linux control node.${NC}"
    echo -e "To execute manually inside WSL:"
    echo -e "  ansible-playbook -i ansible/inventory.ini ansible/site.yml"
    exit 0
fi
echo -e "${GREEN}[PASS] Ansible playbook engine available${NC}"

# 2. Validate Inventory
echo "[2/5] Validating inventory inventory.ini..."
ansible-inventory -i ansible/inventory.ini --list >/dev/null
echo -e "${GREEN}[PASS] Inventory syntax and host groups validated${NC}"

# 3. Validate Playbook Syntax
echo "[3/5] Performing dry-run syntax check on site.yml..."
ansible-playbook -i ansible/inventory.ini ansible/site.yml --syntax-check
echo -e "${GREEN}[PASS] Playbook syntax check successful${NC}"

# 4. Execute Playbook
echo "[4/5] Executing infrastructure provisioning and deployment..."
ansible-playbook -i ansible/inventory.ini ansible/site.yml

# 5. Verify Health Endpoint
echo "[5/5] Verifying post-deployment health via Actuator..."
sleep 5
if command -v curl >/dev/null 2>&1; then
    if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
        echo -e "${GREEN}[PASS] Actuator health check confirmed: status UP${NC}"
    else
        echo -e "${RED}[FAIL] Health endpoint check failed!${NC}"
        exit 1
    fi
fi

echo -e "${BLUE}======================================================${NC}"
echo -e "${GREEN} ANSIBLE DEPLOYMENT COMPLETED & VERIFIED!             ${NC}"
echo -e "${BLUE}======================================================${NC}"
