FROM maven:3.8.3-openjdk-17

RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/worldproxy-project.jar"]