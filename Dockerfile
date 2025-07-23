FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

WORKDIR /app

COPY target/azure-eventhub-listener-1.0.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
