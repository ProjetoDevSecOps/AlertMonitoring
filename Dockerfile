FROM FROM alpine:latest
# Garante a correção do libpng
RUN apk update && apk upgrade --no-cache && apk add --no-cache libpng 
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
