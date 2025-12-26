FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY farm-market/pom.xml farm-market/pom.xml
RUN mvn -f farm-market/pom.xml -B -DskipTests clean package || true

COPY farm-market farm-market
RUN mvn -f farm-market/pom.xml -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/farm-market/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
