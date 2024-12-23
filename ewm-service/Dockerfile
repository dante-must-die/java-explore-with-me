FROM openjdk:21-jdk-slim

WORKDIR /app

ARG JAR_FILE=ewm-service/target/ewm-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
