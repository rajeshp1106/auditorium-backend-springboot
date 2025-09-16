# Build stage: Use Eclipse Temurin JDK 21 and install Maven manually
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy code and build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk AS run
WORKDIR /app
COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar project.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","project.jar"]
