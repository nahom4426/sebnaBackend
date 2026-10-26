#!/usr/bin/env bash
# Daily PostgreSQL backup. Keeps the last 14 days.
# Cron (as root):  0 3 * * * /opt/sebna/sebnaBackend/deploy/scripts/backup-db.sh >> /var/log/sebna-backup.log 2>&1
set -euo pipefail
cd "$(dirname "$0")/.."
set -a; . ./.env; set +a

BACKUP_DIR=/opt/sebna-backups
mkdir -p "$BACKUP_DIR"
FILE="$BACKUP_DIR/sebna-$(date +%F_%H%M).sql.gz"

docker compose exec -T db pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" | gzip > "$FILE"
find "$BACKUP_DIR" -name 'sebna-*.sql.gz' -mtime +14 -delete
echo "$(date '+%F %T') backup written: $FILE"
