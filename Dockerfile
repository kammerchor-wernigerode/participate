FROM maven:3.9.8-eclipse-temurin-17 AS base

FROM base AS dependencies
WORKDIR /usr/src/anwendung

COPY pom.xml                                                     .
RUN mkdir -p                                                     ./src/main/resources/scss
COPY src/main/java/de/vinado/wicket/participate/Participate.java ./src/main/java/de/vinado/wicket/participate/Participate.java

RUN mvn -C -q package


FROM base AS builder
WORKDIR /usr/src/anwendung

COPY --from=dependencies /root/.m2/repository /root/.m2/repository
COPY pom.xml .
COPY src/    ./src/

RUN mvn package


FROM base AS healthcheck-builder
WORKDIR /usr/src/healthcheck

COPY scripts/Healthcheck.java ./Healthcheck.java
RUN javac -d . Healthcheck.java


FROM eclipse-temurin:17.0.12_7-jre
WORKDIR /opt/anwendung

ARG JAR_FILE=participate-*.jar
COPY --from=builder             /usr/src/anwendung/target/${JAR_FILE} ./server.jar
COPY --from=healthcheck-builder /usr/src/healthcheck/Healthcheck.class /usr/local/bin/Healthcheck.class

ENV SPRING_PROFILES_ACTIVE=smtp_auth,smtp_tls

HEALTHCHECK --interval=20s --timeout=5s --retries=5 --start-period=30s \
    CMD ["/opt/java/openjdk/bin/java", "-cp", "/usr/local/bin", "Healthcheck"]

EXPOSE 8080
CMD ["/opt/java/openjdk/bin/java", "-Djava.security.egd=file:/dev/urandom", "-jar" ,"/opt/anwendung/server.jar"]
