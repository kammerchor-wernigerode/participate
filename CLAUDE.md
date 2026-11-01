# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Project

Participation manager for Kammerchor Wernigerode e.V. It is a Spring Boot 3.5
and Apache Wicket 10 web app backed by PostgreSQL or MariaDB, with Keycloak for
authentication.

## Toolchain

Java 17 is required (see `pom.xml` → `<java.version>`). The flake provides it.
Run every Maven command through the dev shell, because a globally installed JDK
may be a different version:

```bash
nix develop -c mvn -q validate
```

The dev shell sets `JAVA_HOME` to JDK 17 and puts a matching Maven on `PATH`.

## Build & Run

```bash
nix develop -c mvn clean package       # SCSS, WebJars, compile, test, jar
nix develop -c mvn spring-boot:run     # Docker Compose must be up first
```

SCSS is compiled automatically during `generate-resources`, no manual step
needed. The Maven Enforcer checks dependency convergence at `validate`.

## Local Development Services

Docker Compose provides Keycloak and the database. Start before running the app:

```bash
docker compose --profile postgresql up -d
docker compose --profile mariadb up -d
docker compose down
```

Spring Boot runs Liquibase migrations on startup. `hibernate.ddl-auto` is
`validate`. Hibernate does not create or modify the schema, Liquibase owns all
migrations.

## Testing

```bash
nix develop -c mvn test
```

Integration tests use Testcontainers (real PostgreSQL or MariaDB) and need a
running Docker daemon.

## Nix Flake

`devShells.<system>.default` provides JDK 17 and Maven for Linux and macOS.

The Docker build in `Dockerfile` stays in use until the host runs NixOS.

## Architecture

- Web layer: **Apache Wicket 10** (component-based Java framework, not Spring
  MVC)
- Authentication: Keycloak via Spring Security OAuth2 client
- Database migration: Liquibase (changelog in
  `src/main/resources/de/vinado/wicket/participate/db/liquibase/`)

## Code Style

- Line length: 120 chars (Java), 80 chars (Markdown)
- Indent: 4 spaces, LF line endings, final newlines required
- Lombok `@NonNull` throws `IllegalArgumentException` (configured in
  `lombok.config`)
