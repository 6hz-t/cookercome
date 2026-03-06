# 后端项目结构说明

## 项目结构
```
Backend/
├── src/main/java/com/hs/backend/
│   ├── BackendApplication.java      # 启动类
│   ├── common/                       # 公共类
│   │   ├── Result.java              # 统一响应结果
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   ├── config/                       # 配置类
│   │   └── SecurityConfig.java      # 安全配置（密码加密）
│   ├── cooker/                       # 厨师模块
│   │   ├── controller/              # 控制器层
│   │   │   └── CookerController.java
│   │   ├── service/                 # 服务层
│   │   │   ├── CookerService.java  # 服务接口
│   │   │   └── impl/
│   │   │       └── CookerServiceImpl.java
│   │   ├── repository/              # 数据访问层
│   │   │   └── CookerRepository.java
│   │   └── entity/                  # 实体类
│   │       └── Cooker.java
│   └── user/                        # 用户模块（待创建）
│   └── order/                       # 订单模块（待创建）
├── src/main/resources/
│   ├── application.properties       # 配置文件
│   └── static/                      # 静态资源
├── docs/                            # 文档
│   └── 厨师模块 API.md
└── pom.xml                          # Maven 配置
```

## 技术栈
- **Spring Boot 4.0.3** - 核心框架
- **Spring Data MongoDB** - 数据库访问
- **Spring Security** - 密码加密（BCrypt）
- **Lombok** - 简化代码
- **Java 17** - JDK 版本

## 快速开始

### 1. 安装 MongoDB
```bash
# Windows 下载：https://www.mongodb.com/try/download/community
# 启动 MongoDB
mongod
```

### 2. 配置数据库
编辑 `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/cooker_platform
```

### 3. 运行项目
```bash
cd Backend
mvn spring-boot:run
```

### 4. 测试接口
使用 Postman 或前端调用测试：
```bash
# 登录测试
POST http://localhost:8080/api/cooker/login
{
  "username": "test",
  "password": "123456"
}
```

## 数据库集合

### cooker (厨师集合)
```javascript
{
  _id: ObjectId,
  username: String,        // 用户名
  password: String,        // 加密密码
  phone: String,           // 手机号
  idCard: String,          // 身份证号
  realName: String,        // 真实姓名
  avatar: String,          // 头像 URL
  introduction: String,    // 简介
  dishKinds: [String],     // 擅长菜系
  star: Double,            // 评分
  status: Integer,         // 状态：0-审核中 1-正常 2-禁用
  certificates: [String],  // 证书图片
  createTime: Date,        // 创建时间
  updateTime: Date         // 更新时间
}
```

## 扩展模块

### 添加用户模块
1. 创建 `user/entity/User.java`
2. 创建 `user/repository/UserRepository.java`
3. 创建 `user/service/UserService.java` 和实现
4. 创建 `user/controller/UserController.java`

### 添加订单模块
1. 创建 `order/entity/Order.java`
2. 创建 `order/repository/OrderRepository.java`
3. 创建 `order/service/OrderService.java` 和实现
4. 创建 `order/controller/OrderController.java`
