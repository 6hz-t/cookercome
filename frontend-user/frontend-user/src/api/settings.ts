import request from '@/utils/request'

/**
 * 获取当前客户个人信息
 */
export function getProfile() {
  return request({
    url: '/customer/settings/profile',
    method: 'get'
  })
}

/**
 * 更新客户个人信息
 */
export function updateProfile(data: any) {
  return request({
    url: '/customer/settings/profile',
    method: 'put',
    data
  })
}

/**
 * 绑定新手机号
 */
export function bindPhone(data: {
  newPhone: string
  currentPassword: string
}) {
  return request({
    url: '/customer/settings/phone/bind',
    method: 'post',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(data: {
  oldPassword: string
  newPassword: string
}) {
  return request({
    url: '/customer/settings/password/change',
    method: 'post',
    data
  })
}
