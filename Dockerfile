# Étape 1 : Build avec Maven
FROM maven:3.8.5-openjdk-17 AS build
COPY src .
RUN mvn clean package -DskipTests

# Étape 2 : Exécution
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]