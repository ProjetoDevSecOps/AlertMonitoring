FROM eclipse-temurin:21-jdk-alpine
# Garante a correção do libpng
RUN apk update && apk upgrade --no-cache && apk add --no-cache libpng 
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
