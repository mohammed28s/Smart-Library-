# SmartLibrary

Project structure:

```
SmartLibrary/
├─ backend/    # Spring Boot + SQLite
├─ frontend/   # Angular
├─ docs/
├─ deploy/
├─ docker-compose.yml
├─ docker-compose.dev.yml
├─ docker-compose.prod.yml
└─ README.md
```

## Backend (Spring Boot + SQLite)

- Java: 17
- Port: `8080`
- Health endpoint: `GET /api/health`
- SQLite path in container: `/app/data/smartlibrary.db`

Run locally (without Docker):

```bash
cd backend
mvn spring-boot:run
```

## Frontend (Angular)

Run locally (without Docker):

```bash
cd frontend
npm install
npm start
```

Local frontend uses proxy (`/api -> http://localhost:8080`) via `proxy.conf.json`.

## Docker Profiles

### Quick start (default compose)

```bash
docker compose up --build
```

### Development profile (live Angular dev server)

```bash
docker compose -f docker-compose.dev.yml up --build
```

### Production profile

```bash
docker compose -f docker-compose.prod.yml --env-file .env up --build -d
```

Then open:

- Frontend: http://localhost (prod) or http://localhost:4200 (dev)
- Backend health: http://localhost:8080/api/health

## AI assistant (DeepSeek)

- If `DEEPSEEK_API_KEY` is set, backend calls DeepSeek Chat Completions API.
- If not set, backend uses local fallback assistant.

## SMS (Twilio)

By default, SMS mode is `mock`. To enable real SMS, set:

- `SMS_MODE=twilio`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`

## AWS Deployment

Deployment templates are in:

- `deploy/aws/ecs/task-definition.json`
- `deploy/aws/ec2/docker-compose.prod.yml`
- `deploy/aws/elastic-beanstalk/Dockerrun.aws.json`
- `deploy/aws/README.md`

These are production-ready templates with placeholders for account/region/image IDs.

## SRS

System requirements are documented in:

- `docs/SRS.md`
