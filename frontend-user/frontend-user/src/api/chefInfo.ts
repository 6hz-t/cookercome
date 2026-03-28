import request from '@/utils/request'

/**
 * 获取厨师列表
 */
export function getChefList() {
  return request({
    url: '/api/chef/list',
    method: 'get'
  })
}

/**
 * 获取厨师详情
 */
export function getChefDetail(id: number) {
  return request({
    url: `/api/chef/${id}`,
    method: 'get'
  })
}
