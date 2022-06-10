FROM openjdk:17-alpine
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY ./target/worldproxy-0.0.1-SNAPSHOT.jar /usr/app

EXPOSE 8000

CMD ["java", "-jar", "worldproxy-0.0.1-SNAPSHOT.jar"]