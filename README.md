# Wicket Participate

At the end of 2016 I have heard of the Apache Wicket Framework and started to write a closed-source (now open-source) web application for one of my project choirs Kammerchor Wernigerode. We had trouble to plan our weekends because some of the singers did not answer to invitations of the board members. 

This repository exists only for legacy support. If you are interestet in unsing this for your own club I would recommend you to checkout the [kammerchor-wernigerode/eva-api](https://github.com/kammerchor-wernigerode/eva-api) and [kammerchor-wernigerode/eva](https://github.com/kammerchor-wernigerode/eva) repositories.

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

You'll need a MySQL database.
- Windows: XAMPP
- Ubuntu: mysql-server

When you configured your database and your property files run Liquibase to create the database tables. You can either
run Liquibase manually with
```bash
mvn resources:resources liquibase:update
```
or let Spring Boot do the job for you. 

Start the application.
```bash
mvn spring-boot:run
```

## Deployment

```bash
mvn clean install
```
creates an war file. In order to run the application you'll need an Apache Tomcat server installed. Also don't forget to configure the `.properties` files to fit your environment.

## Licence
Apache License 2.0 - [Vinado](https://vinado.de) - Built with :heart: in Dresden
