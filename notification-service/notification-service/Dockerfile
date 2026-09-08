FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the complete Maven project
COPY . .

# Build notification-service along with its required modules
RUN mvn -B -pl notification-service/notification-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/notification-service/notification-service/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
