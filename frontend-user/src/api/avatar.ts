/**
 * 头像上传 API
 */

import request from '../utils/request'

/**
 * 上传头像（后端代理模式）
 * @param file 头像文件
 */
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
 return request({
  url: '/avatar/upload',
  method: 'post',
  data: formData,
  headers: {
     'Content-Type': 'multipart/form-data'
   }
  })
}

/**
 * 获取上传签名（前端直传 OSS 模式）
 * @param filename 文件名
 */
export function getUploadSignature(filename: string) {
 return request({
  url: '/avatar/signature',
  method: 'get',
  params: { filename }
  })
}

/**
 * 保存头像路径到数据库（前端直传 OSS 后调用）
 * @param relativePath 相对路径
 */
export function saveAvatar(relativePath: string) {
 return request({
  url: '/avatar/save',
  method: 'post',
  data: { relativePath },
  headers: {
     'Content-Type': 'application/x-www-form-urlencoded'
   },
  transformRequest: [(data) => {
    let ret = ''
    for (const key in data) {
     ret += encodeURIComponent(key) + '=' + encodeURIComponent(data[key]) + '&'
    }
   return ret
  }]
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
