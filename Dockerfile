FROM maven:3.9.12-eclipse-temurin-17-alpine AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
ARG JWTSECRETKEY
ARG JWTEXPIRATIONMS
RUN mvn clean package -DskipTests \
    -DJWTSECRETKEY=${JWTSECRETKEY} \
    -DJWTEXPIRATIONMS=${JWTEXPIRATIONMS}

FROM eclipse-temurin:17.0.18_8-jre-alpine

COPY --from=build /app/target/projeto-integrador-web-app-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]