FROM maven:3.8.4-openjdk-11 AS build
RUN mkdir /userapp-project
COPY . /userapp-project
WORKDIR /userapp-project
RUN mvn clean package

FROM openjdk:11
COPY --from=build /userapp-project/target/user-api.jar user-api.jar
CMD "java" "-jar" "user-api.jar"