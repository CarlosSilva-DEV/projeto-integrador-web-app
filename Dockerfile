FROM maven:3.9.12-eclipse-temurin-17-alpine AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.18_8-jre-alpine

# Adicionando usuário não root para executar o container, boa prática de segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/projeto-integrador-web-app-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]