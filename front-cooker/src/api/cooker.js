import { chefrequest, authrequest } from './request'

export const login = (data) => authrequest.post('/login', data)

export const registerChef = (data) => {
  const payload = {
    phone: data.phone,
    password: data.password,
    role: 1
  }
  return authrequest.post('/register', payload)
}

export const getNewOrders = (chefId) =>
  chefrequest.post('/getNewOrders', null, {
    params: { chefId }
  })

export const acceptOrder = (orderId, chefId) =>
  chefrequest.post('/acceptOrder', null, {
    params: { orderId, chefId }
  })

export const getTodayOrders = (chefId) =>
  chefrequest.post('/getTodayOrders', null, {
    params: { chefId }
  })

export const getOrders = (chefId) =>
  chefrequest.post('/getOrders', null, {
    params: { chefId }
  })

export const getHistoryOrders = (chefId) =>
  chefrequest.post('/getHistoryOrders', null, {
    params: { chefId }
  })

export const updateChefStatus = (chefId, status, reason = '') =>
  chefrequest.post('/updateChefStatus', null, {
    params: { chefId, status, reason }
  })

export const updateOrderStatus = (orderId, status) =>
  chefrequest.post('/updateOrderStatus', null, {
    params: { orderId, status }
  })

export const getChefProfile = (userId) =>
  chefrequest.get('/profile', {
    params: { userId }
  })

export const saveChefProfile = (data) => chefrequest.post('/profile', data)

export async function getChefOrderPool(chefId) {
  const [newRes, servingRes, historyRes] = await Promise.all([getNewOrders(chefId), getOrders(chefId), getHistoryOrders(chefId)])

  const newOrders = Array.isArray(newRes?.data) ? newRes.data : []
  const servingOrders = Array.isArray(servingRes?.data) ? servingRes.data : []
  const historyOrders = Array.isArray(historyRes?.data) ? historyRes.data : []

  return {
    code: 200,
    data: [...newOrders, ...servingOrders, ...historyOrders],
    newOrders,
    servingOrders,
    historyOrders
  }
}

export const getServingOrders = getOrders
export const startService = (orderId) => updateOrderStatus(orderId, 2)
export const endService = (orderId) => updateOrderStatus(orderId, 3)
