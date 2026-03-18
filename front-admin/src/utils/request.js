import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例，指向你的后端地址
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端 Spring Boot 的地址 + 端口
  timeout: 30000 // 增加到 30 秒，适配大数据量查询
})

// 是否正在刷新 Token 的标志
let isRefreshing = false
// 重试队列
let refreshSubscribers = []

// 添加到重试队列
function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb)
}

// 执行重试队列
function onRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken))
  refreshSubscribers = []
}

// 请求拦截器：加 Token（登录后鉴权）
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin-token')
    if (token) {
      // 使用 Authorization 头，格式：Bearer <token>
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理后端返回结果，支持 Token 自动刷新
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端返回的 code=200 代表成功
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(res)
    }
    // 返回 data 字段，这样调用方可以直接获取业务数据
    return res.data
  },
  async (error) => {
    // 如果是 401 错误，尝试刷新 Token
    if (error.response && error.response.status === 401) {
      const refreshToken = localStorage.getItem('admin-refreshToken')
      
      // 如果没有 refresh token，直接跳转登录页
      if (!refreshToken) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('admin-token')
        localStorage.removeItem('admin-refreshToken')
        localStorage.removeItem('admin-userInfo')
        // 延迟跳转到登录页
        setTimeout(() => {
          window.location.href = '/login'
        }, 1000)
        return Promise.reject(error)
      }

      // 如果正在刷新 Token，将请求加入队列
      if (isRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((newToken) => {
            error.config.headers['Authorization'] = 'Bearer ' + newToken
            resolve(request(error.config))
          })
        })
      }

      // 开始刷新 Token
      isRefreshing = true
      
      try {
        // 调用刷新 Token 接口
        const refreshResponse = await axios.post('http://localhost:8080/api/auth/refresh', {
          refreshToken: refreshToken
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        })

        if (refreshResponse.data && refreshResponse.data.code === 200 && refreshResponse.data.data) {
          const newAccessToken = refreshResponse.data.data.accessToken
          const newRefreshToken = refreshResponse.data.data.refreshToken

          // 保存新的 Token
          localStorage.setItem('admin-token', newAccessToken)
          localStorage.setItem('admin-refreshToken', newRefreshToken)

          // 执行重试队列
          onRefreshed(newAccessToken)

          // 重新发送原请求
          error.config.headers['Authorization'] = 'Bearer ' + newAccessToken
          return request(error.config)
        } else {
          // 刷新失败，跳转登录页
          throw new Error('刷新 Token 失败')
        }
      } catch (refreshError) {
        // 刷新失败，清除 Token 并跳转登录页
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('admin-token')
        localStorage.removeItem('admin-refreshToken')
        localStorage.removeItem('admin-userInfo')
        setTimeout(() => {
          window.location.href = '/login'
        }, 1000)
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    ElMessage.error(error.message || '服务器错误')
    return Promise.reject(error)
  }
)

export default request
