# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN sh gradlew --no-daemon --version

COPY src src
RUN sh gradlew --no-daemon --stacktrace bootJar \
    && cp build/libs/*.jar /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system --gid 1001 bakery \
    && useradd --system --uid 1001 --gid bakery --no-create-home bakery

COPY --from=build --chown=bakery:bakery /workspace/app.jar /app/app.jar

USER bakery
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
