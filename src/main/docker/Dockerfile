FROM java:8

LABEL maintainer='vincent.nadoll@gmail.com'

VOLUME /tmp
ADD particpate.jar particpate.jar
RUN bash -c 'touch /particpate.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/particpate.jar"]
