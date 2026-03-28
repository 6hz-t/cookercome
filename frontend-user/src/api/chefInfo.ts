import request from '@/utils/request'

/**
 * 厨师列表查询参数
 */
export interface ChefListParams {
  page?: number
  size?: number
  specialty?: string
  level?: number | null
}

/**
 * 分页响应数据结构
 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 获取厨师列表（分页）
 */
export function getChefList(params: ChefListParams) {
  return request<PageResult<any>>({
    url: '/api/chef/list',
    method: 'get',
    params
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
