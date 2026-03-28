import request from './request'

const { chefrequest, authrequest } = request

// 厨师登录
export const login = (data) => authrequest.post('/login', data)

// 获取新订单 /api/chef/getNewOrders
export const getNewOrders = () => chefrequest.get('/getNewOrders')

// 接单 /api/chef/acceptOrder
export const acceptOrder = (data) => chefrequest.post('/acceptOrder', data)

// 获取厨师信息
export const getChefInfo = () => chefrequest.get('/cooker/info')

// 更新厨师信息
export const updateChefInfo = (data) => chefrequest.put('/cooker/info', data)

// 提交资质审核
export const submitAudit = (data) => chefrequest.post('/cooker/audit', data)

// 获取审核状态
export const getAuditStatus = () => chefrequest.get('/cooker/audit/status')

// 切换服务状态
export const toggleServiceStatus = (status) => chefrequest.put('/cooker/service-status', { status })

// 获取菜系列表
export const getCuisineList = () => chefrequest.get('/cuisine/list')

// 获取厨师菜系
export const getChefCuisines = () => chefrequest.get('/cooker/cuisines')

// 添加厨师菜系
export const addChefCuisine = (cuisineId) => chefrequest.post('/cooker/cuisines', { cuisineId })

// 删除厨师菜系
export const removeChefCuisine = (cuisineId) => chefrequest.delete(`/cooker/cuisines/${cuisineId}`)

// 获取菜品列表
export const getDishList = () => chefrequest.get('/cooker/dishes')

// 添加菜品
export const addDish = (data) => chefrequest.post('/cooker/dishes', data)

// 更新菜品
export const updateDish = (id, data) => chefrequest.put(`/cooker/dishes/${id}`, data)

// 删除菜品
export const deleteDish = (id) => chefrequest.delete(`/cooker/dishes/${id}`)

// 获取待接单列表
export const getPendingOrders = () => chefrequest.get('/cooker/orders/pending')

// 拒单
export const rejectOrder = (orderId, reason) => chefrequest.post(`/cooker/orders/${orderId}/reject`, { reason })

// 获取待服务订单列表（已接单但未服务）
export const getServingOrders = () => chefrequest.get('/cooker/orders/serving')

// 更新订单状态（与页面调用保持一致）
export const updateOrderStatus = (orderId, status) =>
  chefrequest.post('/updateOrderStatus', { orderId, status })

// 获取厨师订单（与页面调用保持一致）
export const getOrders = (chefId) => chefrequest.get('/getOrders', { params: { chefId } })

// 确认服务开始
export const startService = (orderId) => chefrequest.post(`/cooker/orders/${orderId}/start`)

// 确认服务结束
export const endService = (orderId) => chefrequest.post(`/cooker/orders/${orderId}/end`)

// 获取收入统计
export const getIncomeStats = () => chefrequest.get('/cooker/income/stats')

// 获取评价列表
export const getEvaluations = () => chefrequest.get('/cooker/evaluations')

// 回复评价
export const replyEvaluation = (evaluationId, content) =>
  chefrequest.post(`/cooker/evaluations/${evaluationId}/reply`, { content })

