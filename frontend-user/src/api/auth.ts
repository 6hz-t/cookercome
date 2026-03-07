import request from '@/utils/request'
import type { LoginParams, RegisterParams, AuthResponse } from '@/types/auth'

/**
 * 用户登录
 */
export function login(data: LoginParams): Promise<AuthResponse> {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export function register(data: RegisterParams): Promise<any> {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 刷新 Token
 */
export function refreshToken(data: { refreshToken: string }): Promise<AuthResponse> {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser(): Promise<AuthResponse['userInfo']> {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout(): Promise<void> {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
