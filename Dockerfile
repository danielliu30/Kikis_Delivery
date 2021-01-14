FROM openjdk:11
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 80:8080
ENTRYPOINT ["java","-jar","/app.jar"]