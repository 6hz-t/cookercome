/**
 * 头像上传 API（前端传后端模式）
 */

import request from '../utils/request'

/**
 * 上传头像到后端（后端再传 OSS）
 * @param file 头像文件
 */
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/customer/settings/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取图片的预签名 URL
 * @param objectKey 文件在 OSS 中的路径
 * @param expireMinutes 有效期（分钟）
 */
export function getImageSignatureUrl(objectKey: string, expireMinutes?: number) {
  return request({
    url: '/oss/signature',
    method: 'get',
    params: {
      objectKey,
      expireMinutes: expireMinutes || 60  // 默认 60 分钟
    }
  })
}
