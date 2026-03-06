# 厨师模块 API 接口文档

## 基础信息
- **基础路径**: `/api/cooker`
- **数据格式**: JSON
- **字符编码**: UTF-8

---

## 接口列表

### 1. 厨师登录
**请求**
```http
POST /api/cooker/login
Content-Type: application/json

{
  "username": "厨师用户名",
  "password": "密码"
}
```

**响应**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "厨师 ID",
    "username": "用户名",
    "realName": "真实姓名",
    "phone": "手机号",
    "avatar": "头像 URL",
    "status": 1
  }
}
```

---

### 2. 厨师注册
**请求**
```http
POST /api/cooker/register
Content-Type: application/json

{
  "username": "厨师用户名",
  "password": "密码",
  "realName": "张三",
  "phone": "13800138000",
  "idCard": "身份证号"
}
```

**响应**
```json
{
  "code": 200,
  "message": "注册成功，等待审核",
  "data": {
    "id": "厨师 ID",
    "username": "用户名",
    "status": 0
  }
}
```

---

### 3. 获取厨师信息
**请求**
```http
GET /api/cooker/{id}
```

**响应**
```json
{
  "code": 200,
  "data": {
    "id": "厨师 ID",
    "username": "用户名",
    "realName": "真实姓名",
    "phone": "手机号",
    "avatar": "头像 URL",
    "introduction": "简介",
    "dishKinds": ["LuCai", "ChuanCai"],
    "star": 4.5,
    "status": 1
  }
}
```

---

### 4. 更新厨师信息
**请求**
```http
PUT /api/cooker/{id}
Content-Type: application/json

{
  "realName": "张三",
  "phone": "13800138000",
  "avatar": "头像 URL",
  "introduction": "简介",
  "dishKinds": ["LuCai", "ChuanCai"]
}
```

---

### 5. 获取所有厨师
**请求**
```http
GET /api/cooker/list
```

**响应**
```json
{
  "code": 200,
  "data": [
    {
      "id": "厨师 ID",
      "username": "用户名",
      "realName": "真实姓名",
      "avatar": "头像 URL",
      "star": 4.5,
      "status": 1
    }
  ]
}
```

---

### 6. 删除厨师
**请求**
```http
DELETE /api/cooker/{id}
```

**响应**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 500 | 失败 |

## 厨师状态说明

| 状态值 | 说明 |
|--------|------|
| 0 | 审核中 |
| 1 | 正常 |
| 2 | 禁用 |
