# Deploying Sebna to sebna.et

Stack (all in Docker, defined in `deploy/docker-compose.yml` of the backend repo). Run every `docker compose` command from the `deploy/` folder.

| Service   | What it is                                   | Exposed            |
|-----------|----------------------------------------------|--------------------|
| `web`     | Caddy: serves the React app + automatic HTTPS + proxies `/api/*` | ports 80, 443 |
| `backend` | Spring Boot API (Java 17)                    | internal only      |
| `db`      | PostgreSQL 16 (data in `postgres_data` volume) | internal only    |

```
Browser ──https──► Caddy (web) ──/api/*──► backend:8993 ──► db:5432
                        └── everything else ──► React build (/srv)
```

The database and backend are **not** reachable from the internet; only Caddy is.

---

## 1. Point the domain to the server

At your `.et` registrar (Ethio Telecom / your reseller), create DNS records:

| Type | Name  | Value              |
|------|-------|--------------------|
| A    | `@`   | `YOUR_SERVER_IP`   |
| A    | `www` | `YOUR_SERVER_IP`   |

Check it before continuing (can take minutes to hours):

```bash
nslookup sebna.et
```

HTTPS certificates are issued automatically by Let's Encrypt on first start **only once DNS points to the server and ports 80/443 are open**.

## 2. Prepare the server (Ubuntu 22.04/24.04)

SSH in as root (or a sudo user):

```bash
apt update && apt upgrade -y
curl -fsSL https://get.docker.com | sh
ufw allow OpenSSH && ufw allow 80/tcp && ufw allow 443/tcp && ufw allow 443/udp && ufw --force enable
```

If the server has **less than 4 GB RAM**, add swap (the Maven build needs memory):

```bash
fallocate -l 4G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

## 3. Upload the project

Clone both repos side by side on the server (folder names matter):

```bash
mkdir -p /opt/sebna && cd /opt/sebna
git clone https://github.com/nahom4426/sebnaBackend.git
git clone https://github.com/nahom4426/sebna.git Sebna_FE
```

If the repos are private, GitHub will ask for a username and a
[personal access token](https://github.com/settings/tokens) (not your password).

Result:

```
/opt/sebna/
├── sebnaBackend/          backend repo
│   └── deploy/            ← you run everything from here
└── Sebna_FE/              frontend repo
```

Two files are **not in git** (they contain secrets) — upload them from your PC (Git Bash):

```bash
scp "/d/RD/sebna BE/deploy/.env" root@YOUR_SERVER_IP:/opt/sebna/sebnaBackend/deploy/.env
scp "/d/RD/sebna BE/src/main/resources/firebase-service-account.json" root@YOUR_SERVER_IP:/opt/sebna/sebnaBackend/src/main/resources/
```

On the server:

```bash
cd /opt/sebna/sebnaBackend/deploy && chmod +x scripts/*.sh && chmod 600 .env
```

## 4. Configure `.env`

`.env` was generated with a random database password and JWT secret, plus the API keys that were in `application.properties`. Review it:

```bash
nano /opt/sebna/sebnaBackend/deploy/.env
```

- Set `ACME_EMAIL` to an email you read (certificate expiry warnings).
- Replace the keys listed in **Security** below once you rotate them.

## 5. Start

```bash
cd /opt/sebna/sebnaBackend/deploy
./scripts/deploy.sh
```

First build takes several minutes. Watch it come up:

```bash
docker compose ps                 # all should be "running" / backend "healthy"
docker compose logs -f backend    # Spring Boot startup
docker compose logs -f web        # certificate issuance
```

Open https://sebna.et

## 6. Backups (do this!)

```bash
crontab -e
# add:
0 3 * * * /opt/sebna/sebnaBackend/deploy/scripts/backup-db.sh >> /var/log/sebna-backup.log 2>&1
```

Backups go to `/opt/sebna-backups` (14 days kept). Copy them off the server regularly.

Restore:

```bash
gunzip -c /opt/sebna-backups/sebna-XXXX.sql.gz | docker compose exec -T db psql -U sebna -d sebna
```

## Updating after code changes

Push your changes to GitHub, then on the server:

```bash
cd /opt/sebna/sebnaBackend/deploy && ./scripts/deploy.sh
```

`deploy.sh` pulls the latest `main` of both repos, rebuilds and restarts. Use `./scripts/deploy.sh --no-pull` to rebuild without pulling.

Data in Postgres, uploads and certificates are in Docker volumes and survive rebuilds.
**Never** run `docker compose down -v` — `-v` deletes the database.

## Useful commands

```bash
docker compose restart backend
docker compose logs --tail=200 backend
docker compose exec db psql -U sebna -d sebna
docker stats
```

---

## Security — action required

The old `application.properties` (committed to GitHub `nahom4426/sebnaBackend`) contained live credentials. They have been moved out of the code into `.env`, but **they are still in the git history and must be considered leaked**. Rotate each one and put the new values in `.env`:

- [ ] Gmail app password (`MAIL_PASSWORD`) — Google Account → Security → App passwords
- [ ] AWS access key (`AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`) — IAM → deactivate old key, create new
- [ ] Chapa live secret key (`PAYMENT_API_KEY`) — Chapa dashboard
- [ ] AfroMessage token (`AFROMESSAGE_TOKEN`)
- [ ] Firebase service account key — Firebase console → Service accounts → generate new key, delete old one

Other notes:

- `PAYMENT_CHAPA_REQUEST_URL` / `CHAPA_DIRECT_PAY_URL` point to private LAN addresses (`192.168.x.x`) and won't work from the server; update them if payments are used.
- `WebSecurityConfig` allows **unauthenticated** access to `/api/sebna/role/**` and `/api/sebna/privilege/**`. Unless that is intentional, anyone on the internet can call those endpoints — restrict them before go-live.
- Swagger UI is not exposed publicly (only `/api/*` is proxied).

## Local development

Unchanged: `npm run dev` in `Sebna_FE`, run the backend from the IDE. Backend dev secrets are read from `secrets.properties` in the backend repo root (gitignored).

`.env` and `firebase-service-account.json` are gitignored — never commit them. Keep a private backup of both.
