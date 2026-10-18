# ── Etapa 1: Build ──────────────────────────────────────
FROM maven:3.9.16-eclipse-temurin-25 AS build
WORKDIR /app
LABEL authors="Franklin Cappa"

# Copia pom primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente
COPY src ./src

# Compila y empaqueta sin tests
RUN mvn clean package -DskipTests -B

# ── Etapa 2: Runtime (imagen liviana) ───────────────────
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S ventastech && adduser -S catalogo -G ventastech
COPY --from=build --chown=catalogo:ventastech /app/target/*.jar app.jar

USER catalogo

EXPOSE 8081

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
