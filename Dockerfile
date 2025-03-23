# Imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Crear directorio /app/resources
RUN mkdir -p /app/resources

# Copiar application.properties en el contenedor
COPY src/main/resources/application.properties /app/resources/application.properties

# Establecer directorio de trabajo en /app
WORKDIR /app

# Copiar el JAR generado en target
COPY target/blackjack-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la app
EXPOSE 8081

# Ejecutar la aplicación al iniciar el contenedor
CMD ["java", "-jar", "app.jar"]