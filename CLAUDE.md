# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Participate is a Spring Boot 3.5 + Apache Wicket 10 (wicket-bootstrap 7 / Bootstrap 5) web app for managing choir events: inviting singers, tracking their responses, and handling accommodation. It runs on Java 17 and builds with Maven.

## Commands

```bash
mvn -B compile test-compile           # what CI runs first
mvn -B test                           # all tests
mvn -Dtest=EventServiceTest test      # single test class
mvn -Dtest=EventServiceTest#method test   # single test method
mvn -B -DskipTests package            # executable jar in target/
```

- `validate` runs `maven-enforcer` `dependencyConvergence`. When a dependency upgrade breaks the build, pin the conflicting transitive version in `<dependencyManagement>` with a `convergence.*` property in `pom.xml`.
- The SCSS in `src/main/resources/scss` compiles during `generate-resources` (sass-cli-maven-plugin) into `target/generated-sources/.../resources/css`. Run a Maven build after changing styles; there is no separate frontend toolchain.
- Resource filtering uses `^...^` as the delimiter (for example `app.version: "^project.version^"`), not `${...}`.

### Running locally

The default profile has no datasource. Activate a database profile:

```bash
docker compose --profile mariadb up -d     # or --profile postgres; fakesmtp (no profile) always starts
SPRING_PROFILES_ACTIVE=mariadb mvn spring-boot:run
docker compose -f compose.keycloak.yaml up -d   # optional, for the keycloak/oauth2/oidc profiles
```

- Profiles: `mariadb` / `postgres` (datasource), `keycloak` (OAuth2 client registration), `oauth2` or `oidc` (the `oidc` group implies `oauth2`), and `metrics` (Prometheus).
- With neither `oauth2` nor `oidc` active, `PermitAllWebSecurityConfiguration` disables Spring Security, and Wicket's own sign-in page with the shared `app.participate-password` handles login.
- `app.wicket.runtime-configuration: DEVELOPMENT` switches to `TrivialCryptFactory`. `DEPLOYMENT` (the default) uses `SunJceCryptFactory`, which needs `app.crypto.*` secrets.
- Actuator runs on port 8081; the app runs on 8080.
- `DockerSecretProcessor` (registered in `META-INF/spring.factories`) resolves `*_FILE` environment variables into credential properties for Docker secrets.

The README's setup section is outdated: it mentions `application.sample.properties`, which no longer exists. Configuration lives in `application*.yml`.

## Tests

- Most tests run against H2 in PostgreSQL mode with `ddl-auto: create` and Liquibase disabled (`src/test/resources/application.yml`).
- `MariadbSmokeTests` and `PostgresSmokeTests` activate the `mariadb` / `postgres` test profiles. Those profiles use Testcontainers JDBC URLs (`jdbc:tc:...`), run the real Liquibase changelog, and then Hibernate `validate`. **They need a Docker daemon.** They are the only check that entity mappings match the migrations on both databases.
- Wicket component tests extend `de.vinado.wicket.test.SpringEnabledWicketTestCase`, which injects Spring beans into a minimal `BootstrapApplication`.

## Architecture

The code has two package roots, both component-scanned by `Participate.java`:

- `de.vinado.wicket.participate`: the original code, organized by layer. It contains `model` (JPA entities and repositories), `services` (`EventService`, `PersonService`, `UserService` + `*Impl`, built on `DataService`), `ui` (Wicket pages/panels by feature), `components`, `providers` (data providers), and `features` (the optional cron jobs).
- `de.vinado.app.participate`: newer code, organized by bounded context (`event`, `notification.email`, `management`, `common`, `wicket`). Each context has `model` / `app` / `infrastructure` / `presentation` / `support` subpackages. New features and refactors go here. Some older top-level packages (`person`, `singer`, `user`, `event`) under the legacy root already follow the same layering.

A few things only become clear after reading several files:

- **Wicket inside Spring.** `ManagementApplication` is a Spring `@Component` that extends `AuthenticatedBootstrapWebApplication`. Components get Spring beans through `SpringComponentInjector` (`@SpringBean`), not constructor injection. The session class is `ManagementSession`. Page URLs are mounted in one place: `ui/pages/ManagementPageRegistry`.
- **Markup location.** Wicket `.html` and `.properties` files live under `src/main/resources/` in the same package path as their Java class. A few sit next to the Java class in `src/main/java`, and the POM copies those as resources. i18n uses `*.utf8.properties` / `*_de.utf8.properties` for components and `messages*.properties` for Spring.
- **Email.** `notification.email` defines `EmailDispatcher` (implemented by `JavaMailDispatcher`, with bounded concurrency and recipient batching configured under `app.notification.email.dispatch`) and `TemplatedEmail`, rendered with FreeMarker templates in `src/main/resources/templates` (`*_de.ftl` for German).
- **Feature cron jobs.** `RemindOverdueCronjob` and `ScoresManagerNotificationCronjob` are gated by `@ConditionalOnExpression` on `app.features.*.enabled`.
- **Schema.** Liquibase is the source of truth, and Hibernate only runs `validate` against it. The master changelog is `src/main/resources/de/vinado/wicket/participate/db/liquibase/changelog.xml`. New migrations get a `changelog-<version>.xml` included at the end. The app supports **both MariaDB and PostgreSQL**. Changesets and SQL files use `dbms="mariadb"` / `dbms="postgresql"` or `*.mariadb.sql` / `*.postgresql.sql` variants where the dialects differ, especially for the `v_*` reporting views. Any schema change needs both variants and must pass both smoke tests. `migration.load` is a pgloader script for moving existing MariaDB data to PostgreSQL.
- Lombok is used throughout. `lombok.config` makes `@NonNull` throw `IllegalArgumentException`.

## Conventions

- Branching follows git-flow: `release/x.y.z` and `hotfix/x.y.z` branches merge into `main`. The version is set in `pom.xml` ("Bump version number to …" commits).
- Pushing a `v*` tag builds and pushes the multi-arch `kchwr/participate` Docker image. CI (`maven.yml`) runs on pushes and PRs to `main`.
- `.editorconfig`: 4-space indent, LF line endings.
