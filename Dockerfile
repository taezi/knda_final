# Build the React frontend.
FROM node:22-alpine AS frontend-build
WORKDIR /workspace/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# Package the frontend assets into the Spring Boot application.
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /workspace/backend

COPY backend/ ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
COPY --from=frontend-build /workspace/frontend/dist/ ./src/main/resources/static/

RUN ./gradlew bootJar --no-daemon \
    && mkdir -p /workspace/output \
    && find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -exec cp {} /workspace/output/app.jar \; \
    && test -f /workspace/output/app.jar

# Run the combined application. Render supplies PORT at runtime; local Docker uses 8080.
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=backend-build /workspace/output/app.jar ./app.jar
ENV SERVER_PORT=8080
EXPOSE 10000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
