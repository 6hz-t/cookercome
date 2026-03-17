import request from '@/utils/request'

/**
 * 获取统计数据
 * @param {Number} days 天数（7/30/90）
 * @returns {Promise}
 */
export function getStatistics(days) {
  return request({
    url: '/api/statistics/overview',
    method: 'get',
    params: { days }
  })
}
