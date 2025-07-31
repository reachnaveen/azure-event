FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

WORKDIR /app

COPY target/servicebus-app-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
