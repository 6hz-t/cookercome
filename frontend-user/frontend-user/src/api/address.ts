import request from '@/utils/request'

/**
 * 获取用户地址列表
 */
export function getAddressList() {
  return request({
    url: '/customer/settings/address/list',
    method: 'get'
  })
}

/**
 * 添加用户地址
 */
export function addAddress(data) {
  return request({
    url: '/customer/settings/address/add',
    method: 'post',
    data
  })
}

/**
 * 更新用户地址
 */
export function updateAddress(data) {
  return request({
    url: '/customer/settings/address/update',
    method: 'put',
    data
  })
}

/**
 * 删除用户地址
 */
export function deleteAddress(addressId) {
  return request({
    url: `/customer/settings/address/delete/${addressId}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export function setDefaultAddress(addressId) {
  return request({
    url: `/customer/settings/address/set-default/${addressId}`,
    method: 'put'
  })
}
