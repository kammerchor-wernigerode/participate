FROM maven:3-jdk-8 as maven
RUN mkdir -p  /usr/src/app
WORKDIR       /usr/src/app

COPY pom.xml /usr/src/app
RUN mvn -B -q dependency:go-offline

COPY src     /usr/src/app/src
# Due to missing depencencies, mvn does not run with -o (offline)
RUN mvn -q package


FROM openjdk:8-jre-alpine
RUN mkdir -p /app
WORKDIR      /app

ARG JAR_FILE=participate-2.12.0-SNAPSHOT.jar
COPY --from=maven /usr/src/app/target/$JAR_FILE /app/application.jar
COPY scripts/docker-entrypoint.sh /usr/local/bin/

ENV SPRING_PROFILES_ACTIVE=smtp_auth,smtp_tls

EXPOSE 8080
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/usr/bin/java", "-Djava.security.egd=file:/dev/./urandom", "-jar" ,"/app/application.jar"]
