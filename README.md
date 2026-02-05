# Simple Admin

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.12-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MyBatis--Flex-1.11.6-blue" alt="MyBatis-Flex">
  <img src="https://img.shields.io/badge/Vue.js-3.5-blue" alt="Vue.js">
  <img src="https://img.shields.io/badge/Java-1.8-orange" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

## 📖 项目简介

**Simple Admin** 是一个基于 Spring Boot 2.7 和 Vue 3 的轻量级通用后台管理系统。项目旨在提供一个高效、简洁、易于扩展的开发基座。

### 核心特性

- 🔐 **权限安全**: 基于 Spring Security + JWT 的身份认证与无状态会话。
- ⚡ **卓越性能**: 后端采用 **MyBatis-Flex** 框架，具备极致的查询性能与流式 API 体验。
- 🎨 **极简开发**: 前端基于 **Cool-CRUD** 生态，支持配置化快速生成管理界面。
- 组织架构: 完善的部门、角色、用户管理体系，支持灵活的权限分配。

## 🏗️ 技术架构

### 后端技术栈 (admin-backend)

| 技术              | 版本   | 说明                               |
| :---------------- | :----- | :--------------------------------- |
| Spring Boot       | 2.7.12 | 核心基础框架                       |
| MyBatis-Flex      | 1.11.6 | 新一代 Java ORM 框架，支持流式查询 |
| Spring Security   | -      | 安全拦截与授权                     |
| JWT (jjwt)        | 0.9.1  | 用户令牌签发与校验                 |
| Redis             | -      | 权限缓存与会话控制                 |
| Hutool            | 5.8.26 | 强大的 Java 工具类库               |
| SpringDoc OpenAPI | 1.8.0  | API 在线文档                       |

### 前端技术栈 (admin-fe)

| 技术         | 版本   | 说明                   |
| :----------- | :----- | :--------------------- |
| Vue.js       | 3.5.x  | 渐进式 JavaScript 框架 |
| Vite         | 5.4.x  | 高性能前端构建工具     |
| Element Plus | 2.10.2 | 桌面端 UI 组件库       |
| Cool-CRUD    | 8.x    | 强大的 CRUD 开发插件   |
| TypeScript   | 5.5.x  | 静态类型支持           |
| TailwindCSS  | 3.4.x  | 原子化 CSS 框架        |

## 📁 项目结构

```text
Simple-Admin/
├── admin-backend/              # 后端服务 (Spring Boot)
│   ├── src/main/java/com/simple/
│   │   ├── core/               # 核心底层
│   │   │   ├── security/       # JWT 与安全配置
│   │   │   └── util/           # 封装工具类
│   │   └── modules/base/       # 基础业务模块
│   │       ├── controller/     # 接口层
│   │       ├── entity/         # MyBatis-Flex 实体
│   │       └── service/        # 业务逻辑
│   └── pom.xml                 # Maven 配置
│
└── admin-fe/                   # 前端服务 (Vue 3)
    ├── src/
    │   ├── modules/base/       # 基础管理页面
    │   │   ├── views/          # 用户/角色/菜单等视图
    │   └── cool/               # Cool-CRUD 核心配置
    ├── tailwind.config.js      # Tailwind 配置
    └── vite.config.ts          # Vite 配置文件
```

## 🚀 快速开始

### 运行环境

- **JDK**: 1.8+
- **Node.js**: 18.x+ (建议使用 pnpm)
- **数据库**: PostgreSQL (推荐) 或 MySQL 5.7+
- **Redis**: 6.0+

### 1. 数据库准备

1. 创建数据库 `simple_admin`。
2. 执行项目根目录下的 `db.sql` 导入表结构及基础数据。

### 2. 后端启动

```bash
cd admin-backend
# 修改 application-local.yml 中的数据库及 Redis 连接信息
mvn clean spring-boot:run
```

### 3. 前端启动

```bash
cd admin-fe
pnpm install
pnpm dev
```

## 🔗 访问路径

- **管理后台**: `http://localhost:9000` (默认账号: admin / 123456)
- **API 文档**: `http://localhost:8001/swagger-ui/index.html`

## 📊 数据库设计

系统核心表均以 `base_sys_` 为前缀，包含：

- `base_sys_user`: 用户信息与状态
- `base_sys_role`: 角色定义与权限关联
- `base_sys_menu`: 菜单树与权限标识
- `base_sys_department`: 组织架构管理
- `base_sys_log`: 系统操作审计日志

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可协议。
