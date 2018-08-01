FROM java:8
VOLUME /tmp
ADD particpate.jar particpate.jar
RUN bash -c 'touch /particpate.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/particpate.jar"]