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
ENV JAVA_OPTS="-Xms1024m -Xmx2048m"
ENV TZ=Asia/Kolkata
ENV APP_HOME=/opt/app
ENV FILE_UPLOAD_DIR=/data/upload
ENV LOG_DIR=/tmp/MAHASBR/logs
ENV VIRUS_SCAN_REQUIRED=true

WORKDIR $APP_HOME

# Install antivirus engine and create application user/directories before switching users
RUN useradd -ms /bin/bash springuser && \
    apt-get update && \
    apt-get install -y --no-install-recommends clamav clamav-freshclam && \
    rm -rf /var/lib/apt/lists/* && \
    (freshclam || true) && \
    mkdir -p "$APP_HOME" "$FILE_UPLOAD_DIR" "$LOG_DIR" && \
    chown -R springuser:springuser "$APP_HOME" /data /tmp/MAHASBR && \
    chmod -R 775 "$FILE_UPLOAD_DIR" "$LOG_DIR"

# Copy WAR from builder stage
COPY --from=builder /app/target/*.war app.war

# Expose application port
EXPOSE 8085

USER springuser

VOLUME ["/data/upload"]

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.war"]
