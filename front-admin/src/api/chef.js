import request from '@/utils/request'

/**
 * 获取厨师审核列表（分页）
 * @param {Object} query 查询参数 { page, size, keyword, auditStatus }
 * @returns {Promise}
 */
export function getChefAuditList(query) {
  return request({
    url: '/api/chef/audit/list',
    method: 'get',
    params: query
  })
}

/**
 * 审核厨师
 * @param {Number} id 厨师 ID
 * @param {Number} status 审核状态：1-通过，2-拒绝
 * @param {String} reason 拒绝原因（status=2 时必填）
 * @returns {Promise}
 */
export function auditChef(id, status, reason) {
  return request({
    url: `/api/chef/audit/${id}`,
    method: 'post',
    params: { status, reason }
  })
}
