# Smart Library System

> A modern, Figma-inspired experience for browsing books, booking reading rooms, and reaching the team when feedback is needed.

## Features
- **Book catalog** seeded with real titles like _Clean Code_, _The Pragmatic Programmer_, _Atomic Habits_, _1984_, _To Kill a Mockingbird_, _The Alchemist_, _Sapiens_, and others so visitors see a lively inventory immediately.
- **Booking & appointments** lets visitors reserve reading rooms (Aurora Loft, Harbor Suite, Skyline Terrace) with date/time validation and live upcoming visit list on the same page.
- **Contact/email workflow** captures name, email, subject, and message to route feedback or questions through `/api/contact`.
- **Polished frontend** using Bootstrap, gradients, curated cover photography, and responsive layouts inspired by the Figma direction discussed in this project.

## Tech stack
| Layer     | Stack                                         |
|-----------|-----------------------------------------------|
| Frontend  | Angular 18 + standalone components, Bootstrap |
| Backend   | Spring Boot (Java 17), Spring Security, JPA   |
| Database  | SQLite (auto-migrated with book and room data)|

## Getting started
### Backend
```bash
cd backend
# configure your .env / jwt/sms/deepseek settings per .env.example
mvn clean verify
mvn spring-boot:run
```
Key endpoints:
- `GET /api/books`, `POST /api/books` (authenticated for managers)
- `GET /api/booking/rooms`, `GET /api/booking/appointments`, `POST /api/booking/appointments`
- `POST /api/contact`
- Security allows health, auth, booking, contact routes without authentication for UX flows.

### Frontend
```bash
cd frontend
npm install
npm test            # runs Karma + FirefoxHeadless
npm run build       # production bundle in dist/smartlibrary-frontend
npm start           # for local dev with live reload
```
The Books page now hosts the catalog, booking form, appointment list, and contact card with rich gradient styling.

## Tests & validation
| Command         | Purpose                         |
|-----------------|---------------------------------|
| `mvn verify`    | Backend unit/integration smoke checks |
| `npm test`      | Angular unit tests (Karma + FirefoxHeadless) |
| `npm run build` | Frontend production bundle verification |

## Environment variables
See `.env.example`. Common keys are `JWT_SECRET`, `TWILIO_*`, `DEEPSEEK_*`, `BACKEND_IMAGE`, `FRONTEND_IMAGE`, `DATA_ROOT`.

## Security note
`npm audit` currently reports 42 vulnerabilities (Angular 18 + tooling) that require a breaking upgrade to Angular 21+/latest CLI to resolve. The recommended path is to migrate to the latest Angular major release before running `npm audit fix --force`.

## Additional Resources
- Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`
- Frontend live demo: `http://localhost:4200`
