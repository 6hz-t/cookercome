/**
 * Token 管理工具类
 */

import { encryptPassword, decryptPassword } from './encryption'

const TOKEN_KEY = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_INFO: 'user_info',
  LOGIN_TIMESTAMP: 'login_timestamp',
  REMEMBER_ME: 'remember_me',
  ACCOUNT_INFO: 'account_info',  // 存储手机号和密码
  AVATAR_CACHE: 'avatar_cache'   // 头像缓存（包含 URL 和时间戳）
} as const

/**
 * 保存 Token到本地存储
 */
export function saveToken(accessToken: string, refreshToken: string, rememberMe?: boolean): void {
  // 如果没有明确指定 rememberMe，根据当前存储位置判断
  if (rememberMe === undefined) {
    // 检查是否已经存在 localStorage 中的 token
    const existingLocalToken = localStorage.getItem(TOKEN_KEY.ACCESS_TOKEN)
    rememberMe = !!existingLocalToken
  }
  
  // 根据是否记住我选择存储方式
  const storage = rememberMe ? localStorage : sessionStorage
  
  storage.setItem(TOKEN_KEY.ACCESS_TOKEN, accessToken)
  storage.setItem(TOKEN_KEY.REFRESH_TOKEN, refreshToken)
  storage.setItem(TOKEN_KEY.LOGIN_TIMESTAMP, Date.now().toString())
  
  // 如果不是记住我，清除 localStorage 中的旧数据（避免冲突）
  if (!rememberMe) {
    localStorage.removeItem(TOKEN_KEY.ACCESS_TOKEN)
    localStorage.removeItem(TOKEN_KEY.REFRESH_TOKEN)
  } else {
    // 如果是记住我，清除 sessionStorage 中的旧数据
    sessionStorage.removeItem(TOKEN_KEY.ACCESS_TOKEN)
    sessionStorage.removeItem(TOKEN_KEY.REFRESH_TOKEN)
  }
}

/**
 * 获取 Access Token（同时检查 localStorage 和 sessionStorage）
 */
export function getAccessToken(): string | null {
  // 先尝试 localStorage（记住我的情况）
  const localToken = localStorage.getItem(TOKEN_KEY.ACCESS_TOKEN)
  if (localToken) return localToken
  
  // 再尝试 sessionStorage（未记住我的情况）
  const sessionToken = sessionStorage.getItem(TOKEN_KEY.ACCESS_TOKEN)
  return sessionToken
}

/**
 * 获取 Refresh Token（同时检查 localStorage 和 sessionStorage）
 */
export function getRefreshToken(): string | null {
  // 先尝试 localStorage（记住我的情况）
  const localToken = localStorage.getItem(TOKEN_KEY.REFRESH_TOKEN)
  if (localToken) return localToken
  
  // 再尝试 sessionStorage（未记住我的情况）
  const sessionToken = sessionStorage.getItem(TOKEN_KEY.REFRESH_TOKEN)
  return sessionToken
}

/**
 * 移除 Token（同时清除 localStorage 和 sessionStorage）
 */
export function removeToken(): void {
  // 清除 localStorage
  localStorage.removeItem(TOKEN_KEY.ACCESS_TOKEN)
  localStorage.removeItem(TOKEN_KEY.REFRESH_TOKEN)
  localStorage.removeItem(TOKEN_KEY.USER_INFO)
  localStorage.removeItem(TOKEN_KEY.LOGIN_TIMESTAMP)
  
  // 清除 sessionStorage
  sessionStorage.removeItem(TOKEN_KEY.ACCESS_TOKEN)
  sessionStorage.removeItem(TOKEN_KEY.REFRESH_TOKEN)
  sessionStorage.removeItem(TOKEN_KEY.USER_INFO)
}

/**
 * 检查 Token 是否存在（同时检查 localStorage 和 sessionStorage）
 */
export function hasToken(): boolean {
  return !!getAccessToken()
}

/**
 * 保存用户信息（根据是否记住我选择存储位置）
 */
export function saveUserInfo(userInfo: any, rememberMe?: boolean): void {
  // 如果没有明确指定 rememberMe，根据当前存储位置判断
  if (rememberMe === undefined) {
    const existingLocalUser = localStorage.getItem(TOKEN_KEY.USER_INFO)
    rememberMe = !!existingLocalUser
  }
  
  // 根据是否记住我选择存储方式
  const storage = rememberMe ? localStorage : sessionStorage
  storage.setItem(TOKEN_KEY.USER_INFO, JSON.stringify(userInfo))
  
  // 清除另一种存储中的旧数据，避免冲突
  if (!rememberMe) {
    localStorage.removeItem(TOKEN_KEY.USER_INFO)
  } else {
    sessionStorage.removeItem(TOKEN_KEY.USER_INFO)
  }
}

/**
 * 获取用户信息（同时检查 localStorage 和 sessionStorage）
 */
export function getUserInfo(): any {
  // 先尝试 localStorage（记住我的情况）
  const localUser = localStorage.getItem(TOKEN_KEY.USER_INFO)
  if (localUser) return JSON.parse(localUser)
  
  // 再尝试 sessionStorage（未记住我的情况）
  const sessionUser = sessionStorage.getItem(TOKEN_KEY.USER_INFO)
  return sessionUser ? JSON.parse(sessionUser) : null
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

/**
 * 保存账号信息（用于记住我功能，使用 AES 加密密码）
 */
export function saveAccountInfo(phone: string, password: string): void {
  localStorage.setItem(TOKEN_KEY.REMEMBER_ME, 'true')
  // 使用 AES 加密后存储
  const encryptedPassword = encryptPassword(password)
  localStorage.setItem(TOKEN_KEY.ACCOUNT_INFO, JSON.stringify({ phone, password: encryptedPassword }))
}

/**
 * 获取账号信息（自动解密密码）
 */
export function getAccountInfo(): { phone: string; password: string } | null {
  const accountInfo = localStorage.getItem(TOKEN_KEY.ACCOUNT_INFO)
  if (!accountInfo) return null
  
  const { phone, password: encryptedPassword } = JSON.parse(accountInfo)
  // 使用 AES 解密密码
  const password = decryptPassword(encryptedPassword)
  
  return { phone, password }
}

/**
 * 检查是否勾选了记住我
 */
export function hasRememberMe(): boolean {
  return localStorage.getItem(TOKEN_KEY.REMEMBER_ME) === 'true'
}

/**
 * 清除账号信息
 */
export function clearAccountInfo(): void {
  localStorage.removeItem(TOKEN_KEY.REMEMBER_ME)
  localStorage.removeItem(TOKEN_KEY.ACCOUNT_INFO)
}

/**
 * 保存头像缓存（包含 URL 和时间戳）
 */
export function saveAvatarCache(avatarUrl: string, rememberMe?: boolean): void {
  const cacheData = {
    url: avatarUrl,
    timestamp: Date.now()
  }
  
  // 根据是否记住我选择存储位置
  const storage = rememberMe ? localStorage : sessionStorage
  storage.setItem(TOKEN_KEY.AVATAR_CACHE, JSON.stringify(cacheData))
  
  // 清除另一种存储中的旧数据
  if (!rememberMe) {
    localStorage.removeItem(TOKEN_KEY.AVATAR_CACHE)
  } else {
    sessionStorage.removeItem(TOKEN_KEY.AVATAR_CACHE)
  }
}

/**
 * 获取头像缓存
 */
export function getAvatarCache(): { url: string; timestamp: number } | null {
  // 先尝试 localStorage（记住我的情况）
  const localCache = localStorage.getItem(TOKEN_KEY.AVATAR_CACHE)
  if (localCache) return JSON.parse(localCache)
  
  // 再尝试 sessionStorage（未记住我的情况）
  const sessionCache = sessionStorage.getItem(TOKEN_KEY.AVATAR_CACHE)
  return sessionCache ? JSON.parse(sessionCache) : null
}

/**
 * 检查头像缓存是否过期（默认 24 小时）
 */
export function isAvatarCacheExpired(maxAge: number = 24 * 60 * 60 * 1000): boolean {
  const cache = getAvatarCache()
  if (!cache) return true
  
  const now = Date.now()
  return (now - cache.timestamp) > maxAge
}
