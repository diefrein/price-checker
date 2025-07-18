FROM gradle:8.6-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle :checker-service:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/checker-service/build/install/checker-service/ /app/

ENTRYPOINT ["/app/bin/checker-service"]
