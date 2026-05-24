# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Participation manager for Kammerchor Wernigerode e.V. — Spring Boot 4 + Apache Wicket 10 web app backed by PostgreSQL or MariaDB, with Keycloak for authentication.

## Build & Run

```bash
mvn clean package        # Full build (SCSS, WebJars, Checkstyle, compile, test, jar)
mvn spring-boot:run      # Run locally (Docker Compose must be up first)
mvn checkstyle:check     # Run Checkstyle without building
```

Checkstyle executes automatically at the `validate` phase, so every `mvn` invocation (including `mvn test`) will fail on style violations.

SCSS is compiled automatically during `generate-resources` — no manual step needed.

Java 25 is required (see `pom.xml` → `<java.version>`).

## Local Development Services

Docker Compose provides Keycloak + database. Start before running the app:

```bash
docker compose --profile postgresql up -d   # PostgreSQL 17 + Keycloak
docker compose --profile mariadb up -d      # MariaDB 10.11 + Keycloak
docker compose down
```

Spring Boot runs Liquibase migrations on startup. In development, Liquibase can be disabled temporarily (`spring.liquibase.enabled=false` in local config) to skip migration checks when no migrations have changed.

`hibernate.ddl-auto` is set to `validate` — Hibernate does not create or modify the schema; Liquibase owns all migrations.

## Testing

```bash
mvn test
```

Integration tests use TestContainers (real PostgreSQL or MariaDB). Test class names must end with `Tests` (not `Test`) — enforced by Checkstyle.

## Architecture

- Web layer: **Apache Wicket 10** (component-based Java framework, not Spring MVC)
  - `ManagementWicketApplication` is the single instantiated Wicket app; `WicketApplication` is its base class
- Authentication: Keycloak 26 via Spring Security OAuth2 client
- Database migration: Liquibase (changelog at `src/main/resources/db/changelog/`)

## Code Style

- Line length: 120 chars (Java), 80 chars (Markdown)
- Indent: 4 spaces; LF line endings; final newlines required
- No star imports; static imports only from: JUnit Jupiter, AssertJ, Mockito, MockitoArgumentMatchers
- Lombok `@NonNull` throws `IllegalArgumentException` (configured in `lombok.config`)
