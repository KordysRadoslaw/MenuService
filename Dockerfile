FROM openjdk:17-jdk-alpine3.13
WORKDIR /app
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]