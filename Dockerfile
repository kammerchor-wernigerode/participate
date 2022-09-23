FROM maven:3-jdk-11 as maven
RUN mkdir -p  /usr/src/app
WORKDIR       /usr/src/app

COPY pom.xml /usr/src/app
RUN mvn -B -q dependency:go-offline

COPY src     /usr/src/app/src
# Due to missing depencencies, mvn does not run with -o (offline)
RUN mvn -q package

FROM openjdk:11-jdk-slim as healthcheck-builder
RUN mkdir -p                  /usr/src/healthcheck
WORKDIR                       /usr/src/healthcheck

COPY scripts/Healthcheck.java /usr/src/healthcheck/
RUN javac -d . Healthcheck.java

FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    netcat \
 && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app
WORKDIR      /app

ARG JAR_FILE=participate-2.17.1.jar
COPY --from=maven /usr/src/app/target/$JAR_FILE /app/application.jar
COPY --from=healthcheck-builder /usr/src/healthcheck/Healthcheck.class /usr/local/bin/
COPY scripts/docker-entrypoint.sh /usr/local/bin/

ENV SPRING_PROFILES_ACTIVE=smtp_auth,smtp_tls

HEALTHCHECK --interval=20s --timeout=5s --retries=5 --start-period=30s \
    CMD ["java", "-cp", "/usr/local/bin", "Healthcheck"]

EXPOSE 8080
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/usr/local/openjdk-11/bin/java", "-Djava.security.egd=file:/dev/./urandom", "-jar" ,"/app/application.jar"]
