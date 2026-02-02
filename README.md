# SmoothTravel Backend

Open-source multimodal travel planning API built with Quarkus and Java 21. Combines SNCF and urban transport networks to compute optimized itineraries.

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- Docker (optional)

## Quick start

Copy the environment file and adjust values if needed:

```bash
cp .env.dev.example .env.dev
```

Start the dev dependencies (PostgreSQL + Redis):

```bash
docker compose -f docker-compose.dev.yml --env-file .env.dev up -d
```

Then run the API in dev mode:

```bash
./mvnw quarkus:dev
```

The API is available at `http://localhost:8080`. A health endpoint is exposed at `GET /api/v1/health` and the Swagger UI at `/docs`.

## Build

```bash
./mvnw package
```

## Docker

```bash
docker build -t smooth-travel-backend .
docker run -p 8080:8080 smooth-travel-backend
```

## Tests

```bash
./mvnw test
```

## License

MIT
