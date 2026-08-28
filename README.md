# Kikis_Delivery
Baking better

Provides end points for store owners to keep track of baked goods

Currently using postman to access all post requests that follow JSON formatting

The api uses JWT validation, so only one endpoint is public for anyone to use in order to generate a token.
All requests made after must include the AUthorization token in the header of their requests

## Running with Docker

The image is built from source in a multi-stage build, so no local JDK or Gradle is needed:

```bash
docker build -t danielliu30/kikis-delivery:latest .
docker run --rm -p 8080:8080 --env-file .env \
  -e AWS_REGION -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY \
  danielliu30/kikis-delivery:latest
```

`.env` supplies `JWT_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD` and `CORS_ALLOWED_ORIGINS`
(see `.env.example`). The app listens on 8080 and runs as the unprivileged `bakery` user.
Heap sizing can be tuned with `JAVA_OPTS`.
