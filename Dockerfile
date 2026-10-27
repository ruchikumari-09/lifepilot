# =========================
# Build Stage
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY . .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests


# =========================
# Runtime Stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/target/*.jar app.jar

# Render uses PORT environment variable.
# 10000 is the default fallback.
EXPOSE 10000

# Start Spring Boot application
# Bind to 0.0.0.0 so Render can access it.
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000} --server.address=0.0.0.0"]
