# SmoothTravel Backend

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mael-app_smooth-travel-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mael-app_smooth-travel-backend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mael-app_smooth-travel-backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mael-app_smooth-travel-backend)

Open-source multimodal travel planning API. Combines SNCF and urban transport networks to compute optimized itineraries. Built with **Quarkus** and **Java 21**.

---

## Tech stack

| Component     | Technology                                  |
|---------------|---------------------------------------------|
| Framework     | Quarkus 3.31.1                              |
| Language      | Java 21                                     |
| Database      | PostgreSQL 17                               |
| Cache         | Redis 7                                     |
| ORM           | Hibernate Panache + Flyway                  |
| API           | REST (JAX-RS) + GraphQL (SmallRye)          |
| Auth          | JWT (RS256) + WebAuthn/Passkeys + OTP email |
| Build         | Maven                                       |
| Containerization | Docker                                   |

---

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- Docker and Docker Compose

---

## Local setup

### 1. Environment variables

```bash
cp .env.example .env
```

The default values in `.env.example` work out of the box for local development. No changes are needed to get started.

### 2. Start dependencies (PostgreSQL + Redis)

```bash
docker compose -f docker-compose.dev.yml --env-file .env up -d
```

### 3. Run the API in dev mode

```bash
./mvnw quarkus:dev
```

The API is available at `http://localhost:8080`.

> In dev mode, Quarkus enables live reload and exposes the Swagger UI at `/docs`.

---

## Endpoints

### Base URL

```
http://localhost:8080/api/v1
```

### Email OTP authentication

| Method | Endpoint                | Auth   | Description                              |
|--------|-------------------------|--------|------------------------------------------|
| `POST` | `/auth/register`        | Public | Create an account and send an OTP code   |
| `POST` | `/auth/register/verify` | Public | Verify the code and return a JWT         |
| `POST` | `/auth/login`           | Public | Send an OTP code to an existing account  |
| `POST` | `/auth/login/verify`    | Public | Verify the code and return a JWT         |
| `POST` | `/auth/resend-code`     | Public | Resend the OTP code (30s cooldown)       |
| `POST` | `/auth/logout`          | JWT    | Revoke the current token                 |

### Passkeys (WebAuthn)

| Method | Endpoint                    | Auth   | Description                            |
|--------|-----------------------------|--------|----------------------------------------|
| `POST` | `/passkeys/register/start`  | JWT    | Start passkey registration             |
| `POST` | `/passkeys/register/finish` | JWT    | Complete passkey registration          |
| `POST` | `/passkeys/auth/start`      | Public | Start passkey authentication           |
| `POST` | `/passkeys/auth/finish`     | Public | Complete authentication, return a JWT  |

### Users

| Method   | Endpoint      | Auth | Description                              |
|----------|---------------|------|------------------------------------------|
| `GET`    | `/users/me`   | JWT  | Get the authenticated user's profile     |
| `DELETE` | `/users/me`   | JWT  | Delete the account and revoke the token  |
| `GET`    | `/users/{id}` | JWT  | Get a user by UUID                       |

### Health

| Method | Endpoint  | Auth   | Description                         |
|--------|-----------|--------|-------------------------------------|
| `GET`  | `/health` | Public | API health status (database, Redis) |

### Interactive documentation

- **Swagger UI**: `http://localhost:8080/docs`
- **GraphQL Playground**: `http://localhost:8080/graphql-ui`

---

## GraphQL API

Endpoint: `POST http://localhost:8080/graphql`

### Queries

```graphql
query {
  userById(id: "uuid") {
    id
    email
    verified
    createdAt
  }
}

query {
  userByEmail(email: "user@example.com") {
    id
    email
    verified
  }
}
```

### Mutations

```graphql
mutation {
  createUser(request: { email: "user@example.com" }) {
    id
    email
  }
}

mutation {
  verifyEmail(email: "user@example.com", code: "ABC123") {
    id
    verified
  }
}

mutation {
  resendVerificationCode(email: "user@example.com")
}
```

---

## Architecture

```
src/main/java/com/smoothtravel/
├── auth/               # JWT generation, Redis blacklist, authentication filter
├── passkey/            # WebAuthn: entity, service, resource, Yubico adapter
├── user/               # User entity, service, repository, REST + GraphQL endpoints
├── health/             # PostgreSQL and Redis health checks
└── infrastructure/
    ├── mail/           # EmailService (Quarkus Mailer)
    └── redis/          # RedisService (Redis DataSource wrapper)
```

### OTP authentication flow

```
Client → POST /auth/register { email }
       ← 201 { id, email, verified: false }

Client → POST /auth/register/verify { email, code }
       ← 200 { user, token: "JWT..." }

Client → POST /auth/login { email }
       ← 200

Client → POST /auth/login/verify { email, code }
       ← 200 { user, token: "JWT..." }
```

### Passkey authentication flow

```
Client → POST /passkeys/register/start               (JWT required)
       ← 200 { creationOptions: "..." }

Client → POST /passkeys/register/finish { response } (JWT required)
       ← 204

Client → POST /passkeys/auth/start { email? }
       ← 200 { requestId, assertionOptions: "..." }

Client → POST /passkeys/auth/finish { requestId, response }
       ← 200 { user, token: "JWT..." }
```

---

## Tests

```bash
./mvnw test
```

With JaCoCo coverage report:

```bash
./mvnw verify -Pcoverage
```

The HTML report is generated at `target/site/jacoco/index.html`.

---

## Build & Docker

### Build JAR

```bash
./mvnw package
```

### Docker image

```bash
docker build -t smooth-travel-backend .
docker run -p 8080:8080 \
  -e POSTGRES_HOST=... \
  -e POSTGRES_USER=... \
  -e POSTGRES_PASSWORD=... \
  -e REDIS_HOST=... \
  smooth-travel-backend
```

---

## Environment variables

See `.env.example` for the full documented list.

| Variable                   | Dev (default)              | Prod     |
|----------------------------|----------------------------|----------|
| `POSTGRES_HOST`            | `localhost`                | Required |
| `POSTGRES_PORT`            | `5432`                     | Optional |
| `POSTGRES_DB`              | `smoothtravel`             | Required |
| `POSTGRES_USER`            | `smoothtravel`             | Required |
| `POSTGRES_PASSWORD`        | `smoothtravel`             | Required |
| `REDIS_HOST`               | `localhost`                | Required |
| `REDIS_PORT`               | `6379`                     | Optional |
| `SMTP_HOST`                | `localhost`                | Required |
| `SMTP_PORT`                | `1025`                     | Optional |
| `SMTP_USERNAME`            | _(empty)_                  | Required |
| `SMTP_PASSWORD`            | _(empty)_                  | Required |
| `SMTP_FROM`                | `noreply@smoothtravel.com` | Required |
| `JWT_PUBLIC_KEY_LOCATION`  | _(embedded key)_           | Required |
| `JWT_PRIVATE_KEY_LOCATION` | _(embedded key)_           | Required |
| `WEBAUTHN_RP_ID`           | `localhost`                | Required |
| `WEBAUTHN_RP_NAME`         | `SmoothTravel`             | Optional |
| `WEBAUTHN_ORIGIN`          | `http://localhost:3000`    | Required |
| `CORS_ORIGINS`             | _(dev: localhost:3000)_    | Required |

---

## License

MIT
