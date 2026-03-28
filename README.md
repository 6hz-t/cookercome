# CookerCome - 厨师上门服务平台

一个连接客户与厨师的 O2O 服务平台，提供上门烹饪服务预约、订单管理、评价系统等完整功能。

## 📋 项目结构

```
cookercome/
├── Backend/           # 后端服务（Spring Boot + MongoDB）
├── front-cooker/      # 前端项目（Vue 3 + Vite）
├── front-user/        # 前端项目（Vue 3 + Vite）
├── front-admin/       # 后台管理项目（Vue 3 + Vite）
├── README.md          # 项目说明
├── sql/               # 数据库 SQL 脚本
└── doc/               # 项目文档
```

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 4.0.3
- **数据库**: MySQL
- **安全**: Spring Security 
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
- **Node.js**: >= 20.19.0 或 >= 22.12.0
- **JDK**: 17
- **数据库**: MySQL 8.0+ (后端使用)
- **MongoDB**: 4.4+ (后端使用)
- **Maven**: 3.8+
- **npm**: 8.19.0+

### 后端依赖
- Spring Boot 4.0.3
- Spring Security (BCrypt 密码加密)
- Lombok
- MySQL Connector
- Spring Data MongoDB
- JWT (JSON Web Token)

### 前端依赖
- Vue 3
- Vite 7.3.1
- Element Plus
- Axios
- Vue Router 4
- Sass

### 后端启动

```bash
cd Backend

# 1. 安装依赖
mvn clean install

# 2. 配置数据库
# 2.1 MySQL配置
# - 创建数据库: cookercome_db
# - 导入sql目录下的初始化脚本
# - 修改application.properties中的数据库连接信息

# 2.2 MongoDB配置
# - 确保MongoDB服务已启动
# - 修改application.properties中的MongoDB连接信息

# 3. 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动（Spring Boot 默认端口）

### 前端启动

```bash
cd front-cooker

# 1. 安装依赖
npm install

# 2. 配置API地址
# 修改src/api/config.js中的API_BASE_URL为后端服务地址

# 3. 启动开发服务器
npm run dev

# 4. 生产构建
npm run build
```

前端服务将在 `http://localhost:5173` 启动（Vite 默认端口）

## 📄 License

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！
