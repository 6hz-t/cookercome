import request from '@/utils/request'

/**
 * 管理员登录
 * @param {Object} data 登录数据 { phone, password }
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: data
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新 Token
 * @param {String} refreshToken 刷新令牌
 * @returns {Promise}
 */
export function refreshToken(refreshToken) {
  return request({
    url: '/api/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}
