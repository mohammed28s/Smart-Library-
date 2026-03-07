# SmartLibrary

Project structure:

```
SmartLibrary/
├─ backend/    # Spring Boot + SQLite
├─ frontend/   # Angular
├─ docker-compose.yml
└─ README.md
```

## Backend (Spring Boot + SQLite)

- Java: 17
- Port: `8080`
- Health endpoint: `GET /api/health`
- SQLite file path: `backend/data/smartlibrary.db` (inside container: `/app/data/smartlibrary.db`)

Run locally (without Docker):

```bash
cd backend
mvn spring-boot:run
```

## Frontend (Angular)

- Angular + Nginx (in Docker)
- Port: `4200`

Run locally (without Docker):

```bash
cd frontend
npm install
npm start
```

Local frontend uses proxy (`/api -> http://localhost:8080`) via `proxy.conf.json`.

## Run with Docker Compose

```bash
cd SmartLibrary
docker compose up --build
```

Then open:

- Frontend: http://localhost:4200
- Backend health: http://localhost:8080/api/health

### Optional SMS (Twilio) configuration for Docker

By default, SMS mode is `mock` (no real SMS sent). To enable real SMS:

```bash
export SMS_MODE=twilio
export TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
export TWILIO_AUTH_TOKEN=your_twilio_auth_token
export TWILIO_FROM_NUMBER=+1xxxxxxxxxx
docker compose up --build
```
