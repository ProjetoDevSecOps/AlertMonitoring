FROM eclipse-temurin:21-jdk-alpine

# -----------------------------------------------------------
# ADIÇÃO IMPORTANTE:
# Atualiza os pacotes do sistema (incluindo libpng) para a versão mais recente
# corrigindo as vulnerabilidades CRITICAL/HIGH do OS.
# -----------------------------------------------------------
RUN apk update && apk upgrade --no-cache libpng

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
