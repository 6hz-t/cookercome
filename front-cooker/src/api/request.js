import axios from 'axios'

const chefrequest = axios.create({
  baseURL: 'http://localhost:8080/api/chef', // 后端 API 基础路径
  timeout: 10000



  //api/chef
})

const authrequest = axios.create({
  baseURL: 'http://localhost:8080/api/auth',
  timeout: 10000
})

// 请求拦截器
chefrequest.interceptors.request.use(
  config => {
    const token = localStorage.getItem('accesstoken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
chefrequest.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accesstoken')
      window.location.href = '/cooker/login'
    }
    return Promise.reject(error)
  }
)

<<<<<<< Updated upstream


export default request
=======
export default { chefrequest, authrequest }
>>>>>>> Stashed changes
