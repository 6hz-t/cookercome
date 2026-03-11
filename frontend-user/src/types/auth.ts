/**
 * 用户基础信息接口（对应 t_user 表）
 */
export interface UserInfo {
  id: number
  phone: string
  role: number  // 角色：0-客户，1-厨师，2-管理员
}

/**
 * 客户详细信息接口（对应 t_customer_info 表）
 */
export interface CustomerInfo {
  id: number
  userId: number
  avatar: string
  realName: string
  gender: number  // 0-未知，1-男，2-女
  email: string
  birthday?: string  // 可选，格式：yyyy-MM-dd
}

/**
 * 后端统一响应包装接口
 */
export interface Result<T> {
  code: number
  message: string
  data: T
}

/**
 * 认证响应接口（对应后端 AuthResponse）
 */
export interface AuthResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  userInfo: UserInfo
}

/**
 * 登录请求参数
 */
export interface LoginParams {
  phone: string
  password: string
}

/**
 * 注册请求参数
 */
export interface RegisterParams {
  phone: string
  password: string
  role?: number  // 角色：0-客户，1-厨师，2-管理员
}

/**
 * 刷新 Token 请求参数
 */
export interface RefreshTokenParams {
  refreshToken: string
}
