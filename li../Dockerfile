FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY lifepilot/.mvn/ .mvn/
COPY lifepilot/mvnw .
COPY lifepilot/pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY lifepilot/src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
