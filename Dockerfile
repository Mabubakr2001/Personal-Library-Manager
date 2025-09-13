FROM openjdk:26-jdk-slim
WORKDIR /app
COPY target/library-manager-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080