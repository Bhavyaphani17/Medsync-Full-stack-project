FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY notification-service/notification-service/pom.xml notification-service/notification-service/

RUN mvn -B -f notification-service/notification-service/pom.xml dependency:go-offline

COPY notification-service/notification-service/src notification-service/notification-service/src

RUN mvn -B -f notification-service/notification-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/notification-service/notification-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
