# CookerCome - 厨师上门服务平台

一个连接客户与厨师的 O2O 服务平台，提供上门烹饪服务预约、订单管理、评价系统等完整功能。

## 📋 项目结构

```
cookercome/
├── Backend/           # 后端服务（Spring Boot + MongoDB）
├── front-cooker/      # 前端项目（Vue 3 + Vite）
├── sql/              # 数据库 SQL 脚本
└── doc/              # 项目文档
```

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 4.0.3
- **数据库**: MySQL
- **安全**: Spring Security (BCrypt 密码加密)
- **其他**: Lombok, Java 17

### 前端
- **框架**: Vue 3
- **构建工具**: Vite 7.3.1
- **UI 组件**: Element Plus
- **HTTP 客户端**: Axios
- **路由**: Vue Router 4
- **样式**: Sass

## 🚀 快速开始

### 环境要求
- Node.js >= 20.19.0 或 >= 22.12.0
- JDK 17
- MongoDB
- Maven



### 前端启动

```bash
cd front-cooker

# 1. 安装依赖
npm install

# 2. 启动开发服务器
npm run dev

# 3. 生产构建
npm run build
```

前端服务将在 `http://localhost:5173` 启动（Vite 默认端口）

## 📄 License

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！
