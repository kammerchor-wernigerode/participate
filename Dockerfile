# Stage 0: Download dependencies and package project as .jar file
FROM maven:3.5.4-alpine as maven
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -B -e -C -q org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline

COPY . .
COPY src/main/resources/application-docker.yml src/main/resources/application.yml
RUN ( \
    echo 'changeLogFile=de/vinado/wicket/participate/db/liquibase/changelog.xml'; \
    ) > src/main/resources/liquibase.properties
# Due to missing depencencies, mvn does not run with -o (offline)
RUN mvn -B -e -q -DskipTests=true verify

# Stage 1: Copy JAR file to java
FROM openjdk:8-jdk-alpine

ARG JAR_FILE=particpate.jar
COPY --from=maven /usr/src/app/target/$JAR_FILE ./app.jar

ARG APPLICATION_NAME='Participate'
ENV APPLICATION_NAME=$APPLICATION_NAME
ARG DATABASE_HOST='localhost'
ENV DATABASE_HOST=$DATABASE_HOST
ARG DATABASE_PORT='3306'
ENV DATABASE_PORT=$DATABASE_PORT
ARG DATABASE_NAME='participate'
ENV DATABASE_NAME=$DATABASE_NAME
ARG MAIL_PORT='587'
ENV MAIL_PORT=$MAIL_PORT
EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
