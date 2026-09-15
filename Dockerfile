# ====== Stage 1: Build ======
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

ARG MAVEN_CLI_OPTS="-B -q -s .mvn/settings.xml"

RUN chmod +x ./mvnw

RUN ./mvnw $MAVEN_CLI_OPTS clean package -DskipTests=true

# ====== Stage 2: Runtime ======
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/ishtech-springboot-oms-*.jar ishtech-springboot-oms.jar

ARG TZ=Europe/Helsinki
ENV TZ=$TZ

ARG SERVER_PORT=8080
ENV SERVER_PORT=${SERVER_PORT}

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-jar", "ishtech-springboot-oms.jar"]
