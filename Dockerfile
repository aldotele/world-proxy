FROM openjdk:17-alpine
ENV APP_HOME=/app/

WORKDIR $APP_HOME
COPY ./target/worldproxy-project.jar $APP_HOME

EXPOSE 8080

CMD ["java", "-jar", "worldproxy-project.jar"]