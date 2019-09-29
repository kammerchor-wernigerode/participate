# Wicket Participate

At the end of 2016 I have heard of the Apache Wicket Framework and started to write a closed-source (now open-source)
web application for one of my project choirs Kammerchor Wernigerode. We had trouble to plan our weekends because some of
the singers did not answer to invitations of the board members. 

This repository exists only for legacy support. If you are interested in using this for your own club I would recommend
you to checkout the [kammerchor-wernigerode/kch-engine](https://github.com/kammerchor-wernigerode/kch-engine) and
[kammerchor-wernigerode/eva](https://github.com/kammerchor-wernigerode/eva) repositories.

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

---

You'll need a MySQL database or a running docker container with MySQL.  
After you configured your database and your property files run Liquibase to create the database tables. You can either
run Liquibase manually with
```bash
mvn resources:resources liquibase:update
```
or let Spring Boot do the job for you. 

Start the application.
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
 --env MAIL_FROM='no-reply@domain.tld' \
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

Navigate to `tools/` and run
```bash
docker-compose up -d
```
to start a development database as well as [FakeSMTP](https://github.com/Nilhcem/FakeSMTP).  
Received emails will be stored in `$HOME/received-emails`.

## Deployment

```bash
mvn clean package
```
creates an executable jar file. Start the application with
```bash
java -jar target/particpate.jar
```
on your machine. Don't forget to configure the properties files to fit your environment or follow the 
[Docker instructions](#docker) above to deploy the application with Docker.

## Tests / CI

- Liquibase integration tests
- Travis CI

## Licence
Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
