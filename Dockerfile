# FROM openjdk:21-jdk-slim
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
