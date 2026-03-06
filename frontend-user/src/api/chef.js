import request from '@/utils/request'

/**
 * 获取厨师列表
 */
export function getChefList(page = 1, size = 10, specialty, level) {
  return request({
    url: '/chef/list',
    method: 'get',
    params: {
      page,
      size,
      specialty,
      level
    }
  })
}

/**
 * 获取厨师详情
 */
export function getChefDetail(id) {
  return request({
    url: `/chef/detail/${id}`,
    method: 'get'
  })
}

/**
 * 搜索附近厨师
 */
export function getNearbyChefs(longitude, latitude, radius = 10) {
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
