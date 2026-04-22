FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
COPY pom.xml .
COPY src ./src
ENTRYPOINT ["mvn", "compile", "quarkus:dev", "-Dquarkus.http.host=0.0.0.0"]
