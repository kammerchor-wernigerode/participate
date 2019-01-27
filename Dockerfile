FROM maven:3-alpine as maven

LABEL maintainer="Vincent Nadoll <vincent.nadoll@gmail.com>"

WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn -q org.apache.maven.plugins:maven-dependency-plugin:go-offline --fail-never

COPY . .
COPY src/main/resources/application-docker.yml src/main/resources/application.yml
RUN ( \
    echo 'changeLogFile=de/vinado/wicket/participate/db/liquibase/changelog.xml'; \
    ) > src/main/resources/liquibase.properties
# Due to missing depencencies, mvn does not run with -o (offline)
RUN mvn -B -e -q verify


FROM openjdk:8-jdk-alpine

ARG JAR_FILE=participate.jar
COPY --from=maven /usr/src/app/target/$JAR_FILE ./app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
