import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

/**
 * 用户注册
 */
export function register(username, password, phone, userType) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: {
      username,
      password,
      phone,
      userType
    }
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
