FROM frolvlad/alpine-oraclejdk8
VOLUME /tmp
ADD  build/libs/servivcebus-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]