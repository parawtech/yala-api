FROM maven:3.8-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the POM file first to leverage Docker layer caching
COPY pom.xml .

# Download dependencies only (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Now copy the source code
COPY src/ ./src/

# Build the application
RUN mvn package -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/rket-0.1.0.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]