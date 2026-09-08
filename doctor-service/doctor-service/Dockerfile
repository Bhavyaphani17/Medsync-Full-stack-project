FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY doctor-service/pom.xml doctor-service/pom.xml

RUN mvn dependency:go-offline -pl doctor-service -am -DskipTests

COPY . .

RUN mvn clean package -pl doctor-service -am -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/doctor-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
