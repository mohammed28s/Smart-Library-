# Smart Library System

A full-stack Smart Library platform for browsing, renting, and purchasing books, with role-based operations for library workers.

## Table of Contents

- [Overview](#overview)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Docker Workflows](#docker-workflows)
- [API and Access Control](#api-and-access-control)
- [AWS Deployment](#aws-deployment)
- [SRS Document](#srs-document)

## Overview

Smart Library provides:

- Customer and guest access to browse catalog
- User authentication with JWT
- Purchase and rental order flows
- Barcode generation and barcode validation for workers
- Refund request and approval workflow
- Worker analytics (inventory and revenue)
- AI assistant chat + feedback collection

## Core Features

### User-facing

- Register, login, forgot/reset password (email and SMS reset)
- Browse and search books
- Create and manage orders
- Request refund for paid orders
- Continue as guest (read-only catalog)

### Worker-facing

- Add, edit, delete books
- Validate order by barcode scan
- Approve refund requests
- View inventory and revenue analytics
- Manage users

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Security, JPA
- Frontend: Angular (standalone components)
- Database: SQLite
- Barcode: ZXing (CODE-128)
- Containers: Docker, Docker Compose
- Cloud templates: AWS ECS / EC2 / Elastic Beanstalk

## Architecture

`Angular Frontend` -> `Spring Boot REST API` -> `SQLite`

Production deployment is containerized and supports AWS targets using templates in `deploy/aws`.

## Project Structure

```text
SmartLibrary/
├─ backend/
├─ frontend/
├─ docs/
│  └─ SRS.md
├─ deploy/
│  └─ aws/
├─ docker-compose.yml
├─ docker-compose.dev.yml
├─ docker-compose.prod.yml
└─ README.md
```

## Getting Started

### Prerequisites

- Java 17+
- Node.js 20+
- Maven 3.9+
- Docker + Docker Compose (optional but recommended)

### Run Backend (local)

```bash
cd backend
mvn spring-boot:run
```

Backend health check:

```bash
curl http://localhost:8080/api/health
```

### Run Frontend (local)

```bash
cd frontend
npm install
npm start
```

Frontend URL: `http://localhost:4200`

## Environment Variables

Define values in `.env` (see `.env.example`):

- `JWT_SECRET`
- `SMS_MODE`, `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER`
- `DEEPSEEK_BASE_URL`, `DEEPSEEK_API_KEY`, `DEEPSEEK_MODEL`
- `BACKEND_IMAGE`, `FRONTEND_IMAGE`, `DATA_ROOT` (production/deployment)

## Docker Workflows

### Standard

```bash
docker compose up --build
```

### Development profile

```bash
docker compose -f docker-compose.dev.yml up --build
```

### Production profile

```bash
docker compose -f docker-compose.prod.yml --env-file .env up --build -d
```

## API and Access Control

- Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`
- Roles:
  - `USER`
  - `WORKER`
  - `GUEST` (frontend session mode, catalog read-only)

Key protected capabilities:

- Worker-only analytics endpoints
- Worker-only refund approval and barcode scan validation
- Book management restricted for authenticated roles (guest is read-only)

## AWS Deployment

Templates and instructions are included in:

- `deploy/aws/ecs/task-definition.json`
- `deploy/aws/ec2/docker-compose.prod.yml`
- `deploy/aws/elastic-beanstalk/Dockerrun.aws.json`
- `deploy/aws/README.md`

## SRS Document

Official requirements are documented in:

- `docs/SRS.md`

