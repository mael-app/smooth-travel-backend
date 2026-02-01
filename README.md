# SmoothTravel Backend

Open-source multimodal travel planning API built with Quarkus and Java 21. Combines SNCF and urban transport networks to compute optimized itineraries.

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- Docker (optional)

## Quick start

```bash
./mvnw quarkus:dev
```

The API is available at `http://localhost:8080`. A health endpoint is exposed at `GET /health`.

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
