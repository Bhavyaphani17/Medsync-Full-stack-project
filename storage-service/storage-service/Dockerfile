FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY common/pom.xml ./common/pom.xml
COPY auth-service/auth-service/pom.xml ./auth-service/auth-service/pom.xml
COPY patient-service/patient-service/pom.xml ./patient-service/patient-service/pom.xml
COPY doctor-service/doctor-service/pom.xml ./doctor-service/doctor-service/pom.xml
COPY appointment-service/appointment-service/pom.xml ./appointment-service/appointment-service/pom.xml
COPY notification-service/notification-service/pom.xml ./notification-service/notification-service/pom.xml
COPY storage-service/storage-service/pom.xml ./storage-service/storage-service/pom.xml
COPY hospital-management-service/hospital-management-service/pom.xml ./hospital-management-service/hospital-management-service/pom.xml
COPY api-gateway/api-gateway/pom.xml ./api-gateway/api-gateway/pom.xml
COPY discovery-server/discovery-server/pom.xml ./discovery-server/discovery-server/pom.xml

COPY common ./common
COPY storage-service/storage-service ./storage-service/storage-service

RUN mvn -pl storage-service/storage-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/storage-service/storage-service/target/*.jar app.jar

EXPOSE 10003

ENTRYPOINT ["java", "-jar", "app.jar"]
