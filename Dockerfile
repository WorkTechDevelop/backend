# Используем Amazon Corretto 21
FROM amazoncorretto:21

# Указываем рабочую директорию
WORKDIR /home/deployer/backend

# Копируем собранный JAR-файл (он будет переименован в app.jar перед сборкой образа)
COPY app.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]]