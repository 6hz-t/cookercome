import request from '@/utils/request'

/**
 * 获取菜品列表（分页）
 * @param {Object} query 查询参数 { page, size, keyword, cuisineId }
 * @returns {Promise}
 */
export function getDishList(query) {
  return request({
    url: '/api/chef-dish/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取菜品详情
 * @param {Number} id 菜品 ID
 * @returns {Promise}
 */
export function getDishDetail(id) {
  return request({
    url: `/api/chef-dish/detail/${id}`,
    method: 'get'
  })
}

/**
 * 新增菜品
 * @param {Object} data 菜品数据
 * @returns {Promise}
 */
export function addDish(data) {
  return request({
    url: '/api/chef-dish',
    method: 'post',
    data: data
  })
}

/**
 * 更新菜品
 * @param {Object} data 菜品数据
 * @returns {Promise}
 */
export function updateDish(data) {
  return request({
    url: '/api/chef-dish',
    method: 'put',
    data: data
  })
}

/**
 * 删除菜品
 * @param {Number} id 菜品 ID
 * @returns {Promise}
 */
export function deleteDish(id) {
  return request({
    url: `/api/chef-dish/${id}`,
    method: 'delete'
  })
}
