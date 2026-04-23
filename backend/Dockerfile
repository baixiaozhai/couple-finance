# Render/Fly.io 部署用 Dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段（精简镜像）
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

# 优化JVM参数：限制内存 + 快速随机数
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
