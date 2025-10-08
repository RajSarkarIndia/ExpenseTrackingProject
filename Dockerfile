FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ExpenseTracking.jar ExpenseTracking.jar
COPY src/main/resources/application.properties application.properties

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ExpenseTracking.jar"]

