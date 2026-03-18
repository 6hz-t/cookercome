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
  userId: string
  realName: string
  gender: number
  chefLevel: number
  experienceYears: number
  completedOrders: number
  minPrice: number
  introduction: string
  phone: string
  avatarUrl: string
}

/**
 * 地址信息接口
 */
export interface UserAddress {
  id: number
  userId: number
  receiver: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  latitude?: number
  longitude?: number
  isDefault: number
}

/**
 * 订单创建请求参数
 */
export interface OrderCreateParams {
  chefId: number
  addressId: number
  reserveDate: string
  reserveTime: string
  dishRequirements?: string
  totalAmount: number
  remark?: string
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

/**
 * 订单信息接口
 */
export interface OrderInfo {
  id: number
  orderNo: string
  chefId: number
  chefName?: string
  chefAvatar?: string
  chefLevel?: number
  addressId: number
  reserveDate: string
  reserveTime: string
  dishRequirements?: string
  totalAmount: number
  status: number
  remark?: string
  createTime: string
}

/**
 * 订单分类类型
 */
export type OrderCategory = 'all' | 'pending' | 'payment' | 'fulfillment' | 'refunding' | 'history'

/**
 * 订单操作类型
 */
export type OrderActionType = 'cancel' | 'pay' | 'refund'

/**
 * 订单操作请求参数
 */
export interface OrderActionParams {
  orderId: number
  actionType: OrderActionType
  reason?: string
}

/**
 * 获取用户地址列表
 */
export function getUserAddresses() {
  return request({
    url: '/customer/addresses',
    method: 'get'
  })
}

/**
 * 创建订单
 */
export function createOrder(params: OrderCreateParams) {
  return request({
    url: '/customer/order/create',
    method: 'post',
    data: params
  })
}

/**
 * 获取用户订单列表
 */
export function getUserOrders(category: OrderCategory = 'all') {
  return request({
    url: '/customer/orders',
    method: 'get',
    params: { category }
  })
}

/**
 * 操作订单（取消、支付、退款）
 */
export function actionOrder(params: OrderActionParams) {
  return request({
    url: '/customer/order/action',
    method: 'post',
    data: params
  })
}
