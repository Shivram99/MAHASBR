# ============================================================
# Stage 1: Build Stage
# ============================================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code and build WAR
COPY src ./src
RUN mvn clean package -DskipTests


# ============================================================
# Stage 2: Runtime Stage
# ============================================================
FROM eclipse-temurin:17-jdk-jammy

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV TZ=Asia/Kolkata
ENV APP_HOME=/opt/app
ENV FILE_UPLOAD_DIR=/opt/app/uploads
ENV LOG_DIR=/opt/app/logs

WORKDIR $APP_HOME

# Create required directories and grant full access
RUN mkdir -p $FILE_UPLOAD_DIR $LOG_DIR && \
    chmod -R 777 $FILE_UPLOAD_DIR $LOG_DIR && \
    chmod -R 777 $APP_HOME

# Copy WAR from builder stage
COPY --from=builder /app/target/*.war app.war

# Expose application port
EXPOSE 8085

# Create non-root user and grant permissions
RUN useradd -ms /bin/bash springuser && \
    chown -R springuser:springuser $APP_HOME
USER springuser

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.war"]
