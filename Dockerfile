FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]