/**
 * 头像管理工具
 * 提供头像缓存、更新策略等功能
 */

import { getAvatarCache, saveAvatarCache, isAvatarCacheExpired } from './token'

// 默认头像（本地静态资源）
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

/**
 * 获取用户头像（优先使用缓存）
 * @param userInfo - 用户信息对象（可选）
 * @returns 头像 URL
 */
export function getUserAvatar(userInfo?: any): string {
  // 1. 如果传入了 userInfo 且有 avatar，直接使用
  if (userInfo && userInfo.avatar) {
    return userInfo.avatar
  }
  
  // 2. 尝试从缓存获取
  const cache = getAvatarCache()
  if (cache && cache.url) {
    return cache.url
  }
  
  // 3. 返回默认头像
  return DEFAULT_AVATAR
}

/**
 * 更新头像缓存
 * @param avatarUrl - 新的头像 URL
 * @param rememberMe - 是否记住我
 */
export function updateUserAvatar(avatarUrl: string, rememberMe?: boolean): void {
  saveAvatarCache(avatarUrl, rememberMe)
}

/**
 * 检查是否需要更新头像（缓存过期则更新）
 * @param maxAge - 最大缓存时间（毫秒），默认 24 小时
 * @returns 是否需要更新
 */
export function shouldUpdateAvatar(maxAge: number = 24 * 60 * 60 * 1000): boolean {
  return isAvatarCacheExpired(maxAge)
}

/**
 * 清除头像缓存
 */
export function clearAvatarCache(): void {
  localStorage.removeItem('avatar_cache')
  sessionStorage.removeItem('avatar_cache')
}
