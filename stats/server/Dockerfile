FROM openjdk:21-slim
WORKDIR /app
COPY target/server-0.0.1-SNAPSHOT.jar server.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "server.jar"]
