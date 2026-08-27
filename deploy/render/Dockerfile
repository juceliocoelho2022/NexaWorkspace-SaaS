FROM node:22-alpine AS frontend-build
WORKDIR /frontend
COPY frontend/package.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./pom.xml
RUN mvn -q -DskipTests dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
ENV PORT=10000
EXPOSE 10000
ENTRYPOINT ["sh","-c","if [ -n \"$DATABASE_URL\" ] && [ -z \"$DB_URL\" ]; then export DB_URL=\"jdbc:postgresql://${DATABASE_URL#*@}\"; fi; if [ -z \"$FRONTEND_URL\" ] && [ -n \"$RENDER_EXTERNAL_HOSTNAME\" ]; then export FRONTEND_URL=\"https://${RENDER_EXTERNAL_HOSTNAME}\"; fi; exec java -XX:MaxRAMPercentage=75 -jar app.jar"]
