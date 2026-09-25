# ====== Stage 1: Build ======
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

ARG MAVEN_CLI_OPTS="-B -q -s .mvn/settings.xml"

RUN chmod +x ./mvnw

RUN ./mvnw $MAVEN_CLI_OPTS clean package -DskipTests=true

# ====== Stage 2: Runtime ======
FROM eclipse-temurin:21-jre

# Install curl, needed by the docker compose healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/ishtech-springboot-oms-*.jar ishtech-springboot-oms.jar

# For building image with custom ports and properties
ARG TZ=Europe/Helsinki
ENV TZ=${TZ}

ARG SERVER_PORT=8080
ENV SERVER_PORT=${SERVER_PORT}

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "ishtech-springboot-oms.jar"]
