import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例，指向你的后端地址
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端Spring Boot的地址+端口
  timeout: 5000
})

// 请求拦截器：加Token（登录后鉴权）
request.interceptors.request.use(
  (config) => {
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

// 响应拦截器：统一处理后端返回结果
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端返回的code=200代表成功
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