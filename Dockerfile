FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/ticketgo-back-end-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=real

ENTRYPOINT ["java", "-jar", "app.jar"]
