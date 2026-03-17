import request from '@/utils/request'

/**
 * 获取菜系列表（分页）
 * @param {Object} query 查询参数 { page, size, keyword }
 * @returns {Promise}
 */
export function getCuisineList(query) {
  return request({
    url: '/api/cuisine/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取菜系详情
 * @param {Number} id 菜系 ID
 * @returns {Promise}
 */
export function getCuisineDetail(id) {
  return request({
    url: `/api/cuisine/detail/${id}`,
    method: 'get'
  })
}

/**
 * 新增菜系
 * @param {Object} data 菜系数据 { cuisineName, description, sort, status }
 * @returns {Promise}
 */
export function addCuisine(data) {
  return request({
    url: '/api/cuisine',
    method: 'post',
    data: data
  })
}

/**
 * 更新菜系
 * @param {Object} data 菜系数据 { id, cuisineName, description, sort, status }
 * @returns {Promise}
 */
export function updateCuisine(data) {
  return request({
    url: '/api/cuisine',
    method: 'put',
    data: data
  })
}

/**
 * 删除菜系
 * @param {Number} id 菜系 ID
 * @returns {Promise}
 */
export function deleteCuisine(id) {
  return request({
    url: `/api/cuisine/${id}`,
    method: 'delete'
  })
}
