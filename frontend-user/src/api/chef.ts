import request from '@/utils/request'

/**
 * 厨师列表参数接口
 */
export interface ChefListParams {
  page?: number
  size?: number
  sortBy?: string
  name?: string
}

/**
 * 厨师信息接口
 */
export interface ChefInfo {
  id: number
  userId: number
  realName: string
  level: number
  serviceCount: number
  basePrice: number
  introduction: string
  gender: number
  phone: string
  avatarUrl: string
  workYears: number
}

/**
 * 获取厨师列表（支持排序和搜索）
 */
export function getChefList(params: ChefListParams) {
  return request({
    url: '/customer/booking/chef/list',
    method: 'get',
    params
  })
}

/**
 * 获取厨师详情
 */
export function getChefDetail(id: number) {
  return request({
    url: `/chef/detail/${id}`,
    method: 'get'
  })
}

/**
 * 搜索附近厨师
 */
export function getNearbyChefs(longitude: number, latitude: number, radius = 10) {
  return request({
    url: '/chef/nearby',
    method: 'get',
    params: {
      longitude,
      latitude,
      radius
    }
  })
}
