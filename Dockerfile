# Use Java 21 from Eclipse Temurin team
FROM eclipse-temurin:21-jdk

# Set working directory in container
WORKDIR /app

# Copy your Spring Boot jar into the container
COPY target/project-0.0.1-SNAPSHOT.jar app.jar

# Expose the port used by Spring Boot
EXPOSE 8081

# Start the service
ENTRYPOINT ["java", "-jar", "app.jar"]
