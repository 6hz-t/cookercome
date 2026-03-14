import axios, { AxiosInstance } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { getAccessToken, getRefreshToken, saveToken, removeToken } from './token'
import { refreshToken as refreshApi } from '@/api/auth'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// ✅ 创建独立的 axios 实例用于刷新 token（不受请求拦截器影响）
const refreshRequest: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 为刷新请求添加独立的请求拦截器（不添加 Authorization 头）
refreshRequest.interceptors.request.use(
  config => {
    // ✅ 刷新 token 时不携带 access_token，避免死循环
    return config
  },
  error => {
    console.error('Refresh request error:', error)
    return Promise.reject(error)
  }
)

// ✅ 为刷新请求添加响应拦截器（解包 response.data）
refreshRequest.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res  // ✅ 返回解包后的数据
  },
  error => {
    console.error('Refresh response error:', error)
    return Promise.reject(error)
  }
)

// ✅ 导出 refreshRequest 供 auth.ts 使用
export { refreshRequest }

// 响应拦截器
let isRefreshing = false
let failedQueue: Array<{
  resolve: (value?: any) => void
  reject: (reason?: any) => void
}> = []

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  
  failedQueue = []
}

request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 打印完整响应以便调试
      console.error('接口返回错误:', res)
      
      // 401: 未授权
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  async error => {
    console.error('Response error:', error)
    
    const originalRequest = error.config
    
    // ✅ 添加调试日志
    console.log('=== 401 刷新调试 ===')
    console.log('HTTP Status:', error.response?.status)
    console.log('Is Retry:', originalRequest._retry)
    console.log('Has Refresh Token:', !!getRefreshToken())
    
    // 如果是 401 错误且不是重试请求
    if (error.response?.status === 401 && !originalRequest._retry) {
      const refreshToken = getRefreshToken()
      
      if (!refreshToken) {
        // 没有 refresh token，跳转登录
        console.warn('❌ 没有 refresh token，无法刷新')
        ElMessage.error('登录已过期，请重新登录')
        removeToken()
        router.push('/login')
        return Promise.reject(error)
      }
      
      console.log('✅ 开始刷新 token...')
      console.log('🔍 当前存储检查:')
      console.log('  - localStorage refreshToken:', localStorage.getItem('refresh_token') ? '存在' : '不存在')
      console.log('  - sessionStorage refreshToken:', sessionStorage.getItem('refresh_token') ? '存在' : '不存在')
      console.log('  - localStorage accessToken:', localStorage.getItem('access_token') ? '存在' : '不存在')
      console.log('  - sessionStorage accessToken:', sessionStorage.getItem('access_token') ? '存在' : '不存在')
      
      // 如果已经在刷新 token，加入队列
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return request(originalRequest)
        }).catch(err => {
          return Promise.reject(err)
        })
      }
      
      originalRequest._retry = true
      isRefreshing = true
      
      try {
        console.log('📡 调用刷新接口...')
        // 调用刷新 token 接口（使用 refreshRequest，已自动解包）
        const res = await refreshApi({ refreshToken })
        console.log('🎉 刷新成功:', res)
        console.log('🔍 res 结构:', res)
        
        // ✅ res 是 Result<AuthResponse>，真正数据在 res.data 中
        const { accessToken, refreshToken: newRefreshToken } = res.data || res
        
        console.log('💡 解包后的数据:')
        console.log('  - accessToken:', accessToken)
        console.log('  - newRefreshToken:', newRefreshToken)
        
        if (!accessToken || !newRefreshToken) {
          console.error('❌ Token 数据无效！')
          throw new Error('刷新返回的 token 为空')
        }
        
        console.log('💾 保存新 token...')
        console.log('  - 新 accessToken 长度:', accessToken?.length)
        console.log('  - 新 refreshToken 长度:', newRefreshToken?.length)
        
        // 🔥 关键：根据 refresh_token 的存储位置判断是否记住我
        const rememberMe = localStorage.getItem('refresh_token') !== null
        console.log('📌 刷新时 rememberMe 判断:', rememberMe ? 'localStorage' : 'sessionStorage')
        
        // 保存新的 token（传递 rememberMe 参数，确保本地刷本地，会话刷会话）
        saveToken(accessToken, newRefreshToken, rememberMe)
        
        // 🔍 验证保存后是否真的存入了
        console.log('💾 保存后立即验证:')
        console.log('  - localStorage accessToken:', localStorage.getItem('access_token') ? '✅ 已保存' : '❌ 未保存')
        console.log('  - sessionStorage accessToken:', sessionStorage.getItem('access_token') ? '✅ 已保存' : '❌ 未保存')
        console.log('  - getAccessToken():', getAccessToken() ? '✅ 可获取' : '❌ 无法获取')
        
        // 处理队列中的请求
        processQueue(null, accessToken)
        
        console.log('🔄 重试原请求...')
        // 重试原请求
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return request(originalRequest)
      } catch (refreshError) {
        console.error('❌ 刷新失败:', refreshError)
        // 刷新失败，清除 token 并跳转登录
        processQueue(refreshError, null)
        ElMessage.error('登录已过期，请重新登录')
        removeToken()
        router.push('/login')
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request
