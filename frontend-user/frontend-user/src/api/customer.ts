import request from '@/utils/request'

/**
 * 获取当前客户个人信息
 */
export function getProfile() {
  return request({
    url: '/customer/profile',
    method: 'get'
  })
}

/**
 * 更新客户个人信息
 */
export function updateProfile(data: any) {
  return request({
    url: '/customer/profile',
    method: 'put',
    data
  })
}

/**
 * 获取客户的订单列表
 */
export function getOrders() {
  return request({
    url: '/customer/orders',
    method: 'get'
  })
}

/**
 * 获取客户的收藏列表
 */
export function getFavorites() {
  return request({
    url: '/customer/favorites',
    method: 'get'
  })
}

/**
 * 获取客户的收货地址列表
 */
export function getAddresses() {
  return request({
    url: '/customer/addresses',
    method: 'get'
  })
}

/**
 * 添加收货地址
 */
export function addAddress(data: any) {
  return request({
    url: '/customer/addresses',
    method: 'post',
    data
  })
}

/**
 * 删除收货地址
 */
export function deleteAddress(id: number) {
  return request({
    url: `/customer/addresses/${id}`,
    method: 'delete'
  })
}

/**
 * 更新收货地址
 */
export function updateAddress(id: number, data: any) {
  return request({
    url: `/customer/addresses/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取用户时间段占用情况
 */
export const getUserSchedule = () => {
  return request({
    url: '/customer/schedule',
    method: 'get'
  })
}
