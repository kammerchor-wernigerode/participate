FROM openjdk:8-jdk-alpine

LABEL maintainer='vincent.nadoll@gmail.com'

VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]