# crm-admin 启动模块

CRM 系统的启动入口与全局配置模块，聚合 crm-common、crm-system 等业务模块。

## 环境准备

1. **MySQL 8**：运行于 `localhost:3306`，账号 `root / 12345678`，数据库 `crm_db` 已建表（执行 `docs/sql/init.sql`）
2. **Redis 7.x**：运行于 `localhost:6379`，无密码
3. **JDK 17+**、**Maven 3.9+**

## 启动步骤

1. 在 `backend/` 目录执行构建：
   ```bash
   mvn clean install -DskipTests
   ```
2. 进入 `crm-admin` 目录启动：
   ```bash
   mvn spring-boot:run
   ```
3. 启动成功后访问接口文档：
   http://localhost:8080/swagger-ui.html

## 默认测试账号

- 用户名：`admin`
- 密码：`123456`

## 模块说明

- `com.crm.CrmApplication`：启动类，开启 MyBatis Mapper 扫描与 AOP 代理
- `com.crm.config`：全局配置（SpringDoc 接口文档、跨域 CORS）
- `application.yml`：主配置（端口、MyBatis-Plus、JWT、SpringDoc）
- `application-dev.yml`：开发环境数据源（MySQL + Redis + Druid）
- `logback-spring.xml`：日志配置（控制台 + 文件按天滚动，保留 30 天）

## 配置覆盖

生产环境可通过 `--spring.profiles.active=prod` 切换配置，并新增 `application-prod.yml`。
