FROM openjdk:11-jdk-buster

WORKDIR /workspace/app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-jar","./app.jar"]

EXPOSE 8082
