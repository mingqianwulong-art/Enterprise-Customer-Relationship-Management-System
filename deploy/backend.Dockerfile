# 后端多阶段构建：Maven 构建 + JRE 运行
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
# 先复制 pom 加速依赖缓存
COPY backend/pom.xml ./
COPY backend/crm-common/pom.xml crm-common/
COPY backend/crm-system/pom.xml crm-system/
COPY backend/crm-customer/pom.xml crm-customer/
COPY backend/crm-market/pom.xml crm-market/
COPY backend/crm-business/pom.xml crm-business/
COPY backend/crm-service/pom.xml crm-service/
COPY backend/crm-report/pom.xml crm-report/
COPY backend/crm-admin/pom.xml crm-admin/
RUN mvn -B -q -pl crm-common,crm-system,crm-customer,crm-market,crm-business,crm-service,crm-report,crm-admin -am dependency:go-offline
# 复制源码并构建
COPY backend/ ./
RUN mvn -B -q -pl crm-admin -am clean package -DskipTests

# 运行阶段：精简 JRE 镜像
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/crm-admin/target/crm-admin.jar app.jar
EXPOSE 8080
# 健康检查：每30秒检测后端是否响应（swagger-ui 为免认证路径）
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/swagger-ui.html || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
