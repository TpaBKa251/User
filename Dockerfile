FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew assemble
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "/app/build/libs/User-0.0.1-SNAPSHOT.jar"]
