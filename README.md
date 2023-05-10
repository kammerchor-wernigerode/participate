# Wicket Participate

At the end of 2016 I have heard of the Apache Wicket Framework and started to write a closed-source (now open-source)
web application for one of my project choirs Kammerchor Wernigerode. We had trouble to plan our weekends because some
singers did not answer to invitations of the board members.

## Setup

Copy or move the sample files in the rest and configure your properties.

```bash
cp src/main/resources/application.sample.properties src/main/resources/application.properties

cp src/main/resources/liquibase.sample.properties src/main/resources/liquibase.properties
```

Install the maven dependencies.

```bash
mvn install
```

You'll need a MySQL database or a running Docker Container with MySQL. Start the application with

```bash
mvn spring-boot:run
```

### Docker

Start a container with
```bash
docker run \
 --env APPLICATION_NAME='Application Name' \
 --env APPLICATION_CUSTOMER='Application Customer' \
 --env APPLICATION_PASSWORD='application_password' \
 --env DATABASE_HOST='mysql' \
 --env DATABASE_PORT=3306 \
 --env DATABASE_NAME='participate' \
 --env DATABASE_USER='participate' \
 --env DATABASE_PASSWORD='participate' \
 --env SMTP_HOST='mail.domain.tld' \
 --env SMTP_PORT=587 \
 --env SMTP_USER='mail.user@domain.tld' \
 --env SMTP_PASSWORD='mail_password' \
 --env MAIL_FROM='mail.user@domain.tld' \
 --env MAIL_REPLY_TO='no-reply@domain.tld' \
 --env BASE_URL='http://localhost:8080' \
 --env LOG_PATH=/tmp \
 -p 8080:8080 \
 --name participate \
 --link participate-db:mysql \
 kchwr/participate
```
make sure you are running a database container too.

*The Docker example does not cover proprietary features. Check the `Feature` class for more information.*

---

Run the command below to start a development database as well as [FakeSMTP](https://github.com/Nilhcem/FakeSMTP).
Received emails will be stored in `$HOME/received-emails`.

```bash
docker compose up -d
```

## Deployment

```bash
mvn clean package
```
creates an executable jar file. Start the application with

```bash
java -jar target/participate-2.20.0.jar
```

on your machine. Don't forget to configure the properties files to fit your environment or follow the
[Docker instructions](#docker) above to deploy the application with Docker.

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

## Licence
Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
