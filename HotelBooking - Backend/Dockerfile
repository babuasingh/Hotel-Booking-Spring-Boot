# this is a multi stage docker file
#Stage 1 : Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline


COPY src src
RUN mvn clean package -DskipTests


#Stage 2 : Runtime Stage distroless java runtime
FROM gcr.io/distroless/java21-debian12
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/HotelBooking.jar app.jar
EXPOSE 8080

CMD ["app.jar"]