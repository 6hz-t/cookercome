import request from '@/utils/request'

/**
 * 获取当前客户个人信息
 */
export function getProfile() {
  return request({
    url: '/customer/settings/profile',
    method: 'get'
  })
}

/**
 * 更新客户个人信息
 */
export function updateProfile(data: any) {
  return request({
    url: '/customer/settings/profile',
    method: 'put',
    data
  })
}

/**
 * 获取 OSS 上传签名（用于头像直传）
 */
export function getOssSignature(fileName: string) {
  return request({
    url: '/customer/settings/oss/signature',
    method: 'get',
    params: { fileName }
  })
}

/**
 * 后端代理上传头像到 OSS（避免 CORS 问题）
 */
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/customer/settings/avatar/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
