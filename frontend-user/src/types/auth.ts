/**
 * 用户信息接口（仅包含 t_user 表字段）
 */
export interface UserInfo {
  id: number
  phone: string
  role: number  // 角色：0-客户，1-厨师，2-管理员
}

/**
 * 认证响应接口
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
