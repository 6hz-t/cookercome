import request from '@/utils/request'

// 1. 获取用户列表（分页 + 搜索）
export function getUserList(query) {
  return request({
    url: '/api/user-manage/list',
    method: 'get',
    params: query
  })
}

// 2. 切换用户状态
export function changeUserStatus(userId, status) {
  return request({
    url: `/api/user-manage/status/${userId}`,
    method: 'put',
    params: { status }
  })
}

// 3. 重置用户密码
export function resetPassword(userId) {
  return request({
    url: `/api/user-manage/reset-password/${userId}`,
    method: 'put'
  })
}
