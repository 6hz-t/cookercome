/**
 * Token 管理工具类
 */

const TOKEN_KEY = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_INFO: 'user_info',
  LOGIN_TIMESTAMP: 'login_timestamp'
} as const

/**
 * 保存 Token 到本地存储
 */
export function saveToken(accessToken: string, refreshToken: string): void {
  localStorage.setItem(TOKEN_KEY.ACCESS_TOKEN, accessToken)
  localStorage.setItem(TOKEN_KEY.REFRESH_TOKEN, refreshToken)
  localStorage.setItem(TOKEN_KEY.LOGIN_TIMESTAMP, Date.now().toString())
}

/**
 * 获取 Access Token
 */
export function getAccessToken(): string | null {
  return localStorage.getItem(TOKEN_KEY.ACCESS_TOKEN)
}

/**
 * 获取 Refresh Token
 */
export function getRefreshToken(): string | null {
  return localStorage.getItem(TOKEN_KEY.REFRESH_TOKEN)
}

/**
 * 移除 Token
 */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY.ACCESS_TOKEN)
  localStorage.removeItem(TOKEN_KEY.REFRESH_TOKEN)
  localStorage.removeItem(TOKEN_KEY.USER_INFO)
  localStorage.removeItem(TOKEN_KEY.LOGIN_TIMESTAMP)
}

/**
 * 检查 Token 是否存在
 */
export function hasToken(): boolean {
  return !!getAccessToken()
}

/**
 * 保存用户信息
 */
export function saveUserInfo(userInfo: any): void {
  localStorage.setItem(TOKEN_KEY.USER_INFO, JSON.stringify(userInfo))
}

/**
 * 获取用户信息
 */
export function getUserInfo(): any {
  const userInfo = localStorage.getItem(TOKEN_KEY.USER_INFO)
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 获取登录时间戳
 */
export function getLoginTimestamp(): number | null {
  const timestamp = localStorage.getItem(TOKEN_KEY.LOGIN_TIMESTAMP)
  return timestamp ? parseInt(timestamp, 10) : null
}

/**
 * 检查 Token 是否过期（预留）
 */
export function isTokenExpired(): boolean {
  // 实际项目中可以在这里添加更复杂的过期逻辑
  return false
}
