FROM gradle:8.6-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle :telegram-bot:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/telegram-bot/build/install/telegram-bot/ /app/

ENTRYPOINT ["/app/bin/telegram-bot"]
