# AWS Deployment Guide

This folder contains deployment templates for SmartLibrary.

## 1) ECS (Fargate)

Files:
- `ecs/task-definition.json`

Steps:
1. Create ECR repos:
   - `smartlibrary-backend`
   - `smartlibrary-frontend`
2. Build and push images.
3. Replace placeholders in `task-definition.json`:
   - `<ACCOUNT_ID>`, `<REGION>`, `<EFS_FILE_SYSTEM_ID>`
4. Create CloudWatch Logs group: `/ecs/smartlibrary`.
5. Register task definition and deploy ECS service behind an ALB.
6. Add Secrets Manager/SSM parameters for JWT/Twilio/DeepSeek values.

## 2) EC2

Files:
- `ec2/docker-compose.prod.yml`

Steps:
1. Install Docker + Docker Compose on EC2.
2. Copy compose file and create `.env`:
   - `BACKEND_IMAGE`, `FRONTEND_IMAGE`, `JWT_SECRET`
3. Start services:
   - `docker compose -f docker-compose.prod.yml --env-file .env up -d`
4. Persist SQLite at `${DATA_ROOT}` (default `/opt/smartlibrary/data`).

## 3) Elastic Beanstalk

Files:
- `elastic-beanstalk/Dockerrun.aws.json`

Steps:
1. Replace ECR placeholders.
2. Zip `Dockerrun.aws.json` and deploy to a multi-container EB environment.
3. Configure environment variables in EB console.
