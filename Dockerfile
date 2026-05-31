
# Stage 1 - Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src
ARG CACHEBUST=1
RUN ./mvnw clean package -DskipTests --no-transfer-progress
RUN cat src/main/resources/application.properties

# Stage 2 - Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/teamsync-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]