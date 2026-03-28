import axios from 'axios'

const chefrequest = axios.create({
  baseURL: 'http://localhost:8080/api/chef',
  timeout: 10000
})

const authrequest = axios.create({
  baseURL: 'http://localhost:8080/api/auth',
  timeout: 10000
})

function attachToken(config) {
  const token = localStorage.getItem('accesstoken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}

function unwrapResponse(response) {
  return response.data
}

function handleResponseError(error) {
  if (error.response?.status === 401) {
    localStorage.removeItem('accesstoken')
    window.location.href = '/cooker/login'
  }
  return Promise.reject(error)
}

chefrequest.interceptors.request.use(attachToken, Promise.reject)
chefrequest.interceptors.response.use(unwrapResponse, handleResponseError)

authrequest.interceptors.request.use(attachToken, Promise.reject)
authrequest.interceptors.response.use(unwrapResponse, handleResponseError)

export { chefrequest, authrequest }
