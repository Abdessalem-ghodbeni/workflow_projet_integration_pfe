# FROM openjdk:21-jdk-slim
FROM openjdk:21-slim

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
