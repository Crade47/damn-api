FROM openjdk:17-jdk-slim
WORKDIR /app
#COPY damn-data-manager/target/*.jar app.jar
COPY target/*.jar app.jar
EXPOSE 8084
CMD ["java", "-jar", "app.jar"]