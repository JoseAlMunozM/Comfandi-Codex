# Usa una imagen base con Java 21
FROM eclipse-temurin:21-jdk

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app_korlon

# Copia el archivo JAR de la aplicación al contenedor
COPY target/*.jar korlon-0.0.1-SNAPSHOT.jar

# Expone el puerto en el que corre la aplicación
EXPOSE 8081

# Ejecuta la aplicación cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "korlon-0.0.1-SNAPSHOT.jar"]