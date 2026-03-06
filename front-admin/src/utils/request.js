// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端接口地址（对应你之前的Spring Boot后端）
  timeout: 5000 // 请求超时时间
})

// 请求拦截器：给所有请求加token（登录后用）
request.interceptors.request.use(
  (config) => {
    // 从本地存储获取token，添加到请求头
    const token = localStorage.getItem('admin-token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理后端返回的结果
request.interceptors.response.use(
  (response) => {
    // 后端返回的统一格式：{code:200, msg:"成功", data:xxx}
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    ElMessage.error(error.message || '服务器错误')
    return Promise.reject(error)
  }
)

export default request