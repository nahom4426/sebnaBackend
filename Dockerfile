# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline || true

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system sebna && useradd --system --gid sebna --home /app sebna \
    && mkdir -p /app/uploads && chown -R sebna:sebna /app

COPY --from=build --chown=sebna:sebna /app/target/sebna.jar /app/sebna.jar

USER sebna

ENV BE_PORT=8993 \
    TZ=Africa/Addis_Ababa \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8993

HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=5 \
    CMD curl -fs http://localhost:${BE_PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/sebna.jar"]
