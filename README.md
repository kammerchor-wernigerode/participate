# Wicket Participate

At the end of 2016 I have heard of the Apache Wicket Framework and started to write a closed-source (now open-source)
web application for one of my project choirs Kammerchor Wernigerode. We had trouble to plan our weekends because some
singers did not answer to invitations of the board members.

## Setup

You need JDK 17, Maven, and Docker for the local database and mail server. Configuration lives in
`src/main/resources/application*.yml`; there are no sample files to copy. Override individual properties with
environment variables or command-line arguments instead of editing those files.

Start a database and [FakeSMTP](https://github.com/Nilhcem/FakeSMTP). Both databases sit behind compose profiles, so
pick one; FakeSMTP always starts. Received emails are stored in `$HOME/received-emails`.

```bash
docker compose --profile mariadb up -d     # or: --profile postgres
```

Start the application with the matching Spring profile. The default profile has no datasource.

```bash
SPRING_PROFILES_ACTIVE=mariadb mvn spring-boot:run     # or: postgres
```

The application listens on http://localhost:8080 (actuator on port 8081). Liquibase creates the schema on first start.
Sign in with the password configured in `app.participate-password`.

### Profiles

| Profile               | Purpose                                                                       |
|-----------------------|-------------------------------------------------------------------------------|
| `mariadb`, `postgres` | Datasource for the respective database                                        |
| `keycloak`            | OAuth2 client registration for the local Keycloak realm                       |
| `oauth2`, `oidc`      | Sign in through an OAuth2/OIDC provider (`oidc` implies `oauth2`)             |
| `metrics`             | Exposes the Prometheus endpoint on the actuator port                          |

To try single sign-on locally, start Keycloak on port 8180 and add the profiles:

```bash
docker compose -f compose.keycloak.yaml up -d
SPRING_PROFILES_ACTIVE=mariadb,keycloak,oidc mvn spring-boot:run
```

### Docker

The image activates the `mariadb` profile by default. Configure it with Spring Boot's
[environment variable binding](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables):

```bash
docker network create participate

docker run \
 --env SPRING_DATASOURCE_URL='jdbc:mariadb://participate-db:3306/participate' \
 --env SPRING_DATASOURCE_USERNAME='participate' \
 --env SPRING_DATASOURCE_PASSWORD='participate' \
 --env SPRING_MAIL_HOST='mail.domain.tld' \
 --env SPRING_MAIL_PORT=587 \
 --env SPRING_MAIL_USERNAME='mail.user@domain.tld' \
 --env SPRING_MAIL_PASSWORD='mail_password' \
 --env SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true \
 --env SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true \
 --env APP_NOTIFICATION_EMAIL_SENDER_FROM='mail.user@domain.tld' \
 --env APP_NOTIFICATION_EMAIL_SENDER_REPLYTO='no-reply@domain.tld' \
 --env APP_BASEURL='http://localhost:8080' \
 --env APP_CUSTOMER='Application Customer' \
 --env APP_PARTICIPATEPASSWORD='application_password' \
 --env APP_CRYPTO_SESSIONSECRET='a-long-random-secret' \
 --env APP_CRYPTO_PBESALT='8charsxx' \
 -p 8080:8080 \
 --network participate \
 --name participate \
 kchwr/participate
```

The database container must run on the same network as `participate-db`. For PostgreSQL, set
`SPRING_PROFILES_ACTIVE=postgres` and a `jdbc:postgresql://` URL.

- `APP_CRYPTO_PBESALT` must be exactly 8 characters. If you leave both crypto values empty, the application generates
  random ones on each start, which invalidates "remember me" logins after every restart.
- Credentials can also be read from files (Docker secrets) by passing a path in one of `SPRING_DATASOURCE_USERNAME_FILE`,
  `SPRING_DATASOURCE_PASSWORD_FILE`, `SPRING_MAIL_USERNAME_FILE`, `SPRING_MAIL_PASSWORD_FILE`,
  `APP_CRYPTO_SESSION_SECRET_FILE`, `APP_CRYPTO_PBE_SALT_FILE` or
  `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET_FILE`. Note that these names keep the underscores.
- The optional features below are configured under `app.features` (for example
  `APP_FEATURES_REMINDOVERDUE_ENABLED=true`).

## Deployment

```bash
mvn clean package
```
creates an executable jar file. Start the application with

```bash
java -jar target/participate-5.4.1.jar
```

on your machine with `SPRING_PROFILES_ACTIVE` and the environment variables from the [Docker instructions](#docker)
set to fit your environment, or deploy the application with Docker.

## Features

Lately, two features have been added, which can be turned on and off by means of *enabled*-flag in the `application.yml`
file.

### Remind overdue singers

If the feature is active, all participants who have not yet registered for the next event will receive another
invitation by email. By default, all events are considered that are up to 14 days in the future.

The cron expression and the offset are configurable. However, the email address of the manager must be configured.

### Score's manager notification

Before the start of the next event, the score's manager will receive an email with a list of attending members. By default,
all events are considered that are up to 7 days in the future.

The cron expression and the offset are configurable.


## Keycloak

Keycloak is an authorization provider that implements the OAuth2 and OpenID Connect protocols. It manages software
clients, users, their roles and claims for the project.

Authorization via OpenID Connect is deactivated by default. Add `keycloak` to the list of active Spring profiles to
enable this feature.

### Users

Keycloak is preconfigured with a variety of users that are more or less useful. The username-password-combination
`admin:secret` might be the only one you ever need for development and manual testing.

| Username             | Password              | Description                         | Realm  | URL                                         |
|----------------------|-----------------------|-------------------------------------|--------|---------------------------------------------|
| <mark>`admin`</mark> | <mark>`secret`</mark> | Realm and application administrator | local  | http://localhost:8180/admin/local/console/  |
| `vnl`                | `secret`              | Management staff                    | local  | http://localhost:8180/admin/local/console/  |
| `superadmin`[^1]     | `secret`[^1]          | Keycloak administrator              | master | http://localhost:8180/admin/master/console/ |

[^1]: Corresponds to the values of `KEYCLOAK_ADMIN` and `KEYCLOAK_ADMIN_PASSWORD`, set for Composes' _keycloak_.

### Configuration Export

This section explains how to export updated configurations so that they can be managed by Git. The development
configuration for Keycloak is part of this project to distribute changes through Git.

First, make sure your development stack is up and running. Perform your necessary changes in the Keycloak web UI. Next,
perform the following command. This will start a new Keycloak instance inside the running container.

```shell
docker compose -f compose.yaml -f compose.keycloak.yaml exec keycloak \
  /opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/import --realm local --users realm_file
```


## Licence
Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
