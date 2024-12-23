FROM openjdk:21-jdk-slim

WORKDIR /app

ARG JAR_FILE=stats/stats-server/target/stats-server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
