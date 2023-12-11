FROM gradle:jdk19 AS build

COPY src/main ./src/main

COPY build.gradle.kts settings.gradle.kts  ./

COPY checkstyle ./checkstyle

RUN gradle clean build

FROM openjdk:19-jdk-oracle AS run

COPY --from=build /home/gradle/build/libs/mancala-game-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
