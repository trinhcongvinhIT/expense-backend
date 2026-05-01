# Bước 1: Dùng hệ điều hành Linux có sẵn Maven và Java 17 để build code
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Nhặt cái file đã build ra và cho chạy bằng Java 17
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]