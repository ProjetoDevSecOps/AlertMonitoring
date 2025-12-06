FROM alpine:edge
RUN apk update && apk upgrade --no-cache
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

