import { defineStore } from 'pinia'
import { login, register, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || ''
  },
  
  actions: {
    // 登录
    async loginAction(username, password) {
      try {
        const res = await login(username, password)
        this.token = res.data
        localStorage.setItem('token', this.token)
        
        // 获取用户信息
        await this.getCurrentUserAction()
        
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 注册
    async registerAction(username, password, phone, userType) {
      try {
        const res = await register(username, password, phone, userType)
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 获取当前用户信息
    async getCurrentUserAction() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 退出登录
    async logoutAction() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
        router.push('/login')
      }
    }
  }
})
