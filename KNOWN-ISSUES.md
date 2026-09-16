#

Known issues that have been confirmed by testing but not yet fixed. Each entry has enough detail to reproduce and fix without re-investigating.

---

## 1. [Docker container always reports `unhealthy`](https://github.com/muneer2ishtech/springboot-books-app/blob/dev/KNOWN-ISSUES.md#1-docker-container-always-reports-unhealthy)

---

## 2. Flyway migrations don't run, so the app fails to start on a new database

**Status:** Open
**Impact:** High — the app doesn't start against an empty database (docker compose, or a newly set-up local database).
**Affects:** `pom.xml`

### Description

`pom.xml` declares `org.flywaydb:flyway-core` but not `spring-boot-starter-flyway`. In Spring Boot 4, Flyway runs automatically only when that starter is present. Without it Flyway never runs: the log has no Flyway lines, no tables are created, and Hibernate schema validation (`spring.jpa.hibernate.ddl-auto=validate`) stops the startup.

The JUnit test uses H2 and doesn't run Flyway, so `./mvnw clean install` still passes.

### Steps to reproduce

```sh
SERVER_PORT_LOCAL=8383 DB_PORT=33306 docker compose up --build
docker logs ishtech_springboot_oms_app
```

Expected: Flyway migrates `ishtech_oms_dev_db` and the app starts.
Actual: `Schema validation: missing table [t_customer_discount]`, and the app container exits.

### Likely cause

`spring-boot-starter-flyway` is missing. ishtech-springboot-jwtauth-web and springboot-books-app both declare it.

### Suggested fix

1. In `pom.xml`, replace `flyway-core` with `org.springframework.boot:spring-boot-starter-flyway`, and keep `flyway-mysql`.
2. Re-run test Level 3 and check that the log shows the Flyway migrations before `Started OmsApplication`.
