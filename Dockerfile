FROM eclipse-temurin:21-jre

WORKDIR /app
COPY bootstrap/build/libs/aster.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
