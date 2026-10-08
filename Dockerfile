# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy the project sources (see .dockerignore to keep the context small).
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY server ./server

# The generated daemon JVM criteria pins vendor=AZUL; drop it so the image's
# Java 21 (Temurin) satisfies the build without provisioning another JDK.
RUN rm -f gradle/gradle-daemon-jvm.properties \
    && chmod +x gradlew \
    && ./gradlew :server:buildFatJar --no-daemon --console=plain

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/server/build/libs/server-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
