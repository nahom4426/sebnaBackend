#!/usr/bin/env bash
# Pull latest code for backend + frontend, rebuild and (re)start the stack.
# Run on the server:  /opt/sebna/sebnaBackend/deploy/scripts/deploy.sh
set -euo pipefail
cd "$(dirname "$0")/.."

[ -f .env ] || { echo ".env missing - copy .env.example to .env and fill it in"; exit 1; }
set -a; . ./.env; set +a
FRONTEND_DIR="${FRONTEND_DIR:-../../Sebna_FE}"

[ -f ../src/main/resources/firebase-service-account.json ] || {
  echo "Missing ../src/main/resources/firebase-service-account.json (not in git - upload it)"; exit 1; }

if [ "${1:-}" != "--no-pull" ]; then
  git -C .. pull --ff-only
  git -C "$FRONTEND_DIR" pull --ff-only
fi

docker compose build --pull
docker compose up -d --remove-orphans
docker image prune -f
docker compose ps
