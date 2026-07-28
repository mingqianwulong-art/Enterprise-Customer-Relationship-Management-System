# 企业客户关系管理系统（Enterprise CRM）

面向温州本地企业的一体化 CRM 系统，覆盖**市场获客 → 客户管理 → 商机跟进 → 售后服务 → 数据决策**全链路。

## 技术栈

- **后端**：Spring Boot 3 + MyBatis-Plus + MySQL 8 + Redis + JWT
- **前端**：Vue 3 + Vite + Element Plus + Pinia + ECharts
- **构建**：Maven 多模块（后端）+ pnpm（前端）

## 目录结构

```
企业客户关系管理系统/
├── backend/        # 后端 Spring Boot 多模块项目
├── frontend/       # 前端 Vue 3 项目
├── docs/           # 项目文档
│   ├── 需求文档.md
│   ├── 数据库设计.md
│   └── 技术方案.md
└── README.md
```

## 核心功能模块

1. 市场获客（多渠道线索归集 / 渠道分析 / 智能分配 / 知识库）
2. 客户全生命周期管理（基础信息 / 标签 / 跟进留痕 / 公海池）
3. 商机与销售流程（阶段管控 / 漏斗 / 合同回款 / 移动外勤）
4. 售后与服务（智能工单 / 全记录关联 / 问题溯源）
5. 数据分析与决策（自定义报表 / 看板 / 预测分析）
6. 系统基础管理（RBAC 权限 / 操作日志 / 第三方集成）

详细需求见 [docs/需求文档.md](docs/需求文档.md)。

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis 7.x

### 后端启动
```bash
cd backend
mvn clean install -DskipTests
cd crm-admin && mvn spring-boot:run
```
访问接口文档：http://localhost:8080/doc.html

### 前端启动
```bash
cd frontend
pnpm install
pnpm dev
```
访问：http://localhost:5173

### 数据库初始化
1. 创建数据库 `crm_db`
2. 执行 `docs/sql/init.sql`（待生成）

## 开发约定

- 提交前执行 `mvn -B verify` 与 `pnpm lint`
- 提交信息格式：`feat: 新增客户查重` / `fix: 修复公海回收逻辑` / `docs: 补充需求文档`
- 分支策略：`main`（生产）/ `dev`（开发）/ `feat/*`（功能）

## 交付物

- [x] 需求文档
- [x] 数据库设计
- [x] 技术方案
- [ ] UI 效果图
- [ ] 后端源码
- [ ] 前端源码
- [ ] 操作手册
