FROM maven:3.8.6-openjdk17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app COPY --from=build /app/target/Chat-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080 ENTRYPOINT ["java", "-jar", "app.jar"]