# ---- Build stage: compile and package the executable JAR ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only the POM first so dependency downloads are cached as their own layer
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Runtime stage: slim JRE with only the JAR ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as an unprivileged user instead of root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
