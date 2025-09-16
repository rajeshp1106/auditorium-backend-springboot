# Use Java 21 from Eclipse Temurin team
FROM maven:3.9.6-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-slim
COPY --from=build /target/project-0.0.1-SNAPSHOT.jar project.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","project.jar"]
