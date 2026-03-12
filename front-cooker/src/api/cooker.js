import request from './request'

// 厨师登录
export const login = (data) => request.post('/cooker/login', data)

// 获取厨师信息
export const getChefInfo = () => request.get('/cooker/info')

// 更新厨师信息
export const updateChefInfo = (data) => request.put('/cooker/info', data)

// 提交资质审核
export const submitAudit = (data) => request.post('/cooker/audit', data)

// 获取审核状态
export const getAuditStatus = () => request.get('/cooker/audit/status')

// 切换服务状态
export const toggleServiceStatus = (status) => request.put('/cooker/service-status', { status })

// 获取菜系列表
export const getCuisineList = () => request.get('/cuisine/list')

// 获取厨师菜系
export const getChefCuisines = () => request.get('/cooker/cuisines')

// 添加厨师菜系
export const addChefCuisine = (cuisineId) => request.post('/cooker/cuisines', { cuisineId })

// 删除厨师菜系
export const removeChefCuisine = (cuisineId) => request.delete(`/cooker/cuisines/${cuisineId}`)

// 获取菜品列表
export const getDishList = () => request.get('/cooker/dishes')

// 添加菜品
export const addDish = (data) => request.post('/cooker/dishes', data)

// 更新菜品
export const updateDish = (id, data) => request.put(`/cooker/dishes/${id}`, data)

// 删除菜品
export const deleteDish = (id) => request.delete(`/cooker/dishes/${id}`)

// 获取待接单列表
export const getPendingOrders = () => request.get('/cooker/orders/pending')

// 接单
export const acceptOrder = (orderId) => request.post(`/cooker/orders/${orderId}/accept`)

// 拒单
export const rejectOrder = (orderId, reason) => request.post(`/cooker/orders/${orderId}/reject`, { reason })

// 确认服务开始
export const startService = (orderId) => request.post(`/cooker/orders/${orderId}/start`)

// 确认服务结束
export const endService = (orderId) => request.post(`/cooker/orders/${orderId}/end`)

// 获取收入统计
export const getIncomeStats = () => request.get('/cooker/income/stats')

// 获取评价列表
export const getEvaluations = () => request.get('/cooker/evaluations')

// 回复评价
export const replyEvaluation = (evaluationId, content) => request.post(`/cooker/evaluations/${evaluationId}/reply`, { content })
