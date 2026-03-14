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
 * 获取 OSS 上传签名（用于头像前端直传）
 */
export function getOssSignature(fileName: string) {
  return request({
    url: '/customer/settings/oss/signature',
    method: 'get',
    params: { fileName }
  })
}

// 注意：后端代理上传接口已删除，现在使用前端直传 OSS 模式
// 请使用 avatar.ts 中的 getUploadSignature 和 uploadToOSS 方法
