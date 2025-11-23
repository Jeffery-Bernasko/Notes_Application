# ====================================================================
# Stage 1: Build the application (The "builder" stage)
# Use Maven image with OpenJDK 21 for building, as your pom.xml specifies Java 21.
# Using 'maven:3.9-eclipse-temurin-21' is more modern and reliable than 3.8.5-openjdk-17.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first to download dependencies and utilize Docker cache layers
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy the rest of the source code and package the application
COPY src ./src
RUN mvn clean package -DskipTests

# ====================================================================
# Stage 2: Create the final, lightweight runtime image
# Use a Java Runtime Environment (JRE) image for the final production image.
# 'eclipse-temurin:21-jre-jammy' is much smaller than the JDK image.
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the JAR file from the 'build' stage
# The file name is typically notes_application-0.0.1-SNAPSHOT.jar based on your pom.xml
COPY --from=build /app/target/*.jar app.jar

# Expose the application port.
# Your application.yaml sets 'server.port: 8001', so we expose 8001.
EXPOSE 8001

# Command to run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]