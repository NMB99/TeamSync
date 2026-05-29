FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/teamsync-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]