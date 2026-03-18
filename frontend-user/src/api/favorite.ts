/**
 * 收藏功能 API
 */

import request from '../utils/request'

/**
 * 收藏的厨师信息
 */
export interface FavoriteChef {
  favoriteId: number
  chefId: number
  realName: string
  gender: number
  chefLevel: number
  experienceYears: number
  completedOrders: number
  minPrice: number
  introduction: string
  avatarUrl: string
  phone: string
  favoriteTime: string
}

/**
 * 添加收藏
 * @param chefId 厨师 ID
 */
export function addFavorite(chefId: number) {
  return request({
    url: `/customer/favorite/add/${chefId}`,
    method: 'post'
  })
}

/**
 * 检查是否已收藏
 * @param chefId 厨师 ID
 */
export function checkFavorite(chefId: number) {
  return request({
    url: `/customer/favorite/check/${chefId}`,
    method: 'get'
  })
}

/**
 * 获取收藏列表
 */
export function getFavoriteList() {
  return request<FavoriteChef[]>({
    url: '/customer/favorite/list',
    method: 'get'
  })
}

/**
 * 取消收藏
 * @param chefId 厨师 ID
 */
export function removeFavorite(chefId: number) {
  return request({
    url: `/customer/favorite/remove/${chefId}`,
    method: 'post'
  })
}
