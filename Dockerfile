FROM gradle:jdk19-alpine AS builder

WORKDIR /build

RUN adduser -S myuser

COPY src/main /build/src/main
COPY build.gradle.kts settings.gradle.kts  /build
COPY checkstyle /build/checkstyle

RUN chown -R myuser /build
USER myuser

RUN gradle clean build --no-daemon && rm -rf .gradle

#

FROM openjdk:19-jdk-alpine

WORKDIR /app

EXPOSE 8080

ENV DB_HOST=mancala-postgres
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

RUN adduser -S myuser

COPY --from=builder /build/build/libs/mancala-game-1.0-SNAPSHOT.jar /app/app.jar

RUN chown -R myuser /app
USER myuser

CMD ["java", "-jar", "app.jar"]
