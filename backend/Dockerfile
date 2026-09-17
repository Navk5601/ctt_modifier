FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY backend/pom.xml ./backend/pom.xml
COPY backend/src ./backend/src

WORKDIR /build/backend

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy Spring Boot backend JAR
COPY --from=build /build/backend/target/*.jar backend.jar

# Copy the JAR that your application executes
COPY /jar/CTT-ZipModifier-1.0.18-SNAPSHOT.jar /app/jar/CTT-ZipModifier-1.0.18-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "backend.jar"]