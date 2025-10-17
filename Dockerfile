# ==============================
# Build stage
# ==============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy pom.xml and resolve dependencies
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

# Copy source code and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ==============================
# Runtime stage
# ==============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from builder
COPY --from=builder /app/target/mahasbr-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
