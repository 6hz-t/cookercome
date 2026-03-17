import request from '@/utils/request'

/**
 * 获取订单列表（分页）
 * @param {Object} query 查询参数 { page, size, keyword, status, startDate, endDate }
 * @returns {Promise}
 */
export function getOrderList(query) {
  return request({
    url: '/api/order/list',
    method: 'get',
    params: query
  })
}

/**
 * 强制取消订单
 * @param {Number} id 订单 ID
 * @param {String} reason 取消原因
 * @returns {Promise}
 */
export function forceCancelOrder(id, reason) {
  return request({
    url: `/api/order/force-cancel/${id}`,
    method: 'post',
    params: { reason }
  })
}
