FROM openjdk:21-slim
WORKDIR /app
COPY target/service-0.0.1-SNAPSHOT.jar service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "service.jar"]
