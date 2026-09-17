#!/usr/bin/env bash
# ==============================================================================
# run.sh — Root Entrypoint Delegating to scripts/run.sh
# Digital Printing Queue Management System (23IT723 DevOps Laboratory)
# ==============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec bash "$SCRIPT_DIR/scripts/run.sh" "$@"
