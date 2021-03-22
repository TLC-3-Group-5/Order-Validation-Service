FROM openjdk:11-jdk-buster

WORKDIR /workspace/app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","./app.jar"]