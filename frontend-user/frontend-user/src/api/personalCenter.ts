import request from '@/utils/request'

/**
 * 个人中心统计信息响应类型
 */
export interface PersonalCenterStats {
  totalOrders: number      // 总订单数
  ongoingOrders: number    // 进行中订单数
  completedOrders: number  // 已完成订单数
  favoritesCount: number   // 收藏数量
  couponsCount: number     // 优惠券数量
  averageRating: number    // 平均评分
}

/**
 * 获取个人中心统计信息
 */
export function getPersonalCenterStats(): Promise<PersonalCenterStats> {
  return request({
    url: '/customer/stats',
    method: 'get'
  })
}

/**
 * 获取客户个人信息
 */
export function getCustomerProfile(): Promise<any> {
  return request({
    url: '/customer/profile',
    method: 'get'
  })
}
