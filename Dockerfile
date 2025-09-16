# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom and download dependencies first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy full project and build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk AS run
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/project-0.0.1-SNAPSHOT.jar project.jar

# Expose port
EXPOSE 8080

# Secrets will come from Render environment variables
ENV SECRET_KEY=${SECRET_KEY}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV MAIL_USER=${MAIL_USER}
ENV MAIL_PASSWORD=${MAIL_PASSWORD}

# Run the app
ENTRYPOINT ["java","-jar","project.jar"]
