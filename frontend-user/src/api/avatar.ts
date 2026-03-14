/**
 * 头像上传 API（前端直传 OSS 模式）
 */

import request from '../utils/request'

/**
 * 获取上传签名（前端直传 OSS 模式）
 * @param filename 文件名
 */
export function getUploadSignature(filename: string) {
 return request({
  url: '/customer/settings/oss/signature',
  method: 'get',
  params: { filename }
  })
}

/**
 * 直接上传文件到 OSS（使用签名 URL）
 * @param signatureUrl 签名 URL
 * @param file 文件
 */
export async function uploadToOSS(signatureUrl: string, file: File) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest()
    xhr.open('PUT', signatureUrl, true)
    xhr.setRequestHeader('Content-Type', file.type)
    
    xhr.onload = function () {
      if (xhr.status === 200) {
        resolve({ success: true })
      } else {
        reject(new Error(`上传失败：${xhr.status}`))
      }
    }
    
    xhr.onerror = function () {
      reject(new Error('网络错误'))
    }
    
    xhr.send(file)
  })
}
