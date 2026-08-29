# Kikis_Delivery
Baking better

Provides end points for store owners to keep track of baked goods

Currently using postman to access all post requests that follow JSON formatting

The api uses JWT validation, so only one endpoint is public for anyone to use in order to generate a token.
All requests made after must include the AUthorization token in the header of their requests

## Data store

All data lives in PostgreSQL. The schema is created by the Flyway migration in
`src/main/resources/db/migration` on startup: `customers`, `baked_goods`,
`validation_tokens`, `customer_orders` and `store_front`. Connection settings come
from `DATABASE_URL`, `DATABASE_USER` and `DATABASE_PASSWORD`.

## Running with Docker

`docker-compose.yml` starts PostgreSQL and the api together; the image is built from
source in a multi-stage build, so no local JDK or Gradle is needed:

```bash
cp .env.example .env   # fill in JWT_SECRET, MAIL_USERNAME, MAIL_PASSWORD
docker compose up --build
```

To run the api alone against an existing database:

```bash
docker build -t danielliu30/kikis-delivery:latest .
docker run --rm -p 8080:8080 --env-file .env \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/bakery \
  danielliu30/kikis-delivery:latest
```

The app listens on 8080 and runs as the unprivileged `bakery` user. Heap sizing can be
tuned with `JAVA_OPTS`.

## Tests

```bash
sh gradlew test
```

Tests run against an in-memory H2 database (`src/test/resources/application.properties`),
so no PostgreSQL instance is needed.
