FROM openjdk:10-jre-slim
MAINTAINER Nicolas Barbé "https://github.com/nicolasbarbe"
EXPOSE 8080

ADD application.jar .

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/application.jar"]