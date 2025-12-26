FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY product-service/pom.xml product-service/pom.xml
RUN mvn -f product-service/pom.xml -B -DskipTests dependency:go-offline

COPY product-service product-service
RUN mvn -f product-service/pom.xml -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/product-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
