import request from '@/utils/request'

// 1. 获取用户列表（分页+搜索）
export function getUserList(query) {
  return request({
    url: '/user/list', // 对应后端接口地址 /user/list
    method: 'get',     // GET请求
    params: query      // 传递分页/搜索参数
  })
}

// 2. 切换用户状态
export function changeUserStatus(userId, status) {
  return request({
    url: `/user/status/${userId}`, // 路径传参：用户ID
    method: 'put',                 // PUT请求
    params: { status }             // 请求参数：状态
  })
}

// 3. 重置用户密码
export function resetPassword(userId) {
  return request({
    url: `/user/reset-password/${userId}`,
    method: 'put'
  })
}

// 4. 登录接口
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data: data // POST请求用data传参（JSON格式）
  })
}