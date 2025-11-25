# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Skip tests during build to speed it up (run them in CI/CD pipeline instead)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables (Can be overridden at runtime)
ENV DB_USER=auth_user
ENV DB_PASSWORD=auth_pass
ENV AUTH_JWT_SECRET=change_me_in_production

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]