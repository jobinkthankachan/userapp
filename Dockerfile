#Build Stage
FROM maven:3.8.4-openjdk-11-slim AS build
RUN mkdir /userapp-project
COPY pom.xml /userapp-project
WORKDIR /userapp-project
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn package


#Runtime Stage
FROM openjdk:11-jre-slim
COPY --from=build /userapp-project/target/user-api.jar user-api.jar
CMD "java" "-jar" "user-api.jar"