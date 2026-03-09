import request from './request'

// 厨师登录
export const login = (data) => request.post('/chef/login', data)

// 获取厨师信息
export const getChefInfo = () => request.get('/chef/info')

// 更新厨师信息
export const updateChefInfo = (data) => request.put('/chef/info', data)

// 提交资质审核
export const submitAudit = (data) => request.post('/chef/audit', data)

// 获取审核状态
export const getAuditStatus = () => request.get('/chef/audit/status')

// 切换服务状态
export const toggleServiceStatus = (status) => request.put('/chef/service-status', { status })

// 获取菜系列表
export const getCuisineList = () => request.get('/cuisine/list')

// 获取厨师菜系
export const getChefCuisines = () => request.get('/chef/cuisines')

// 添加厨师菜系
export const addChefCuisine = (cuisineId) => request.post('/chef/cuisines', { cuisineId })

// 删除厨师菜系
export const removeChefCuisine = (cuisineId) => request.delete(`/chef/cuisines/${cuisineId}`)

// 获取菜品列表
export const getDishList = () => request.get('/chef/dishes')

// 添加菜品
export const addDish = (data) => request.post('/chef/dishes', data)

// 更新菜品
export const updateDish = (id, data) => request.put(`/chef/dishes/${id}`, data)

// 删除菜品
export const deleteDish = (id) => request.delete(`/chef/dishes/${id}`)

// 获取待接单列表
export const getPendingOrders = () => request.get('/chef/orders/pending')

// 接单
export const acceptOrder = (orderId) => request.post(`/chef/orders/${orderId}/accept`)

// 拒单
export const rejectOrder = (orderId, reason) => request.post(`/chef/orders/${orderId}/reject`, { reason })

// 确认服务开始
export const startService = (orderId) => request.post(`/chef/orders/${orderId}/start`)

// 确认服务结束
export const endService = (orderId) => request.post(`/chef/orders/${orderId}/end`)

// 获取收入统计
export const getIncomeStats = () => request.get('/chef/income/stats')

// 获取评价列表
export const getEvaluations = () => request.get('/chef/evaluations')

// 回复评价
export const replyEvaluation = (evaluationId, content) => request.post(`/chef/evaluations/${evaluationId}/reply`, { content })
