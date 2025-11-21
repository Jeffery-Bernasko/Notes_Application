# Stage 1: Build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# Use an official JDK image as base
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file into container
#COPY target/*.jar app.jar
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]