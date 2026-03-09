import { defineStore } from 'pinia'
import { login, register, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'
import { saveToken, removeToken, saveUserInfo, getUserInfo } from '@/utils/token'

interface UserInfo {
  id?: number
  phone?: string
  role?: number  // 角色：0-客户，1-厨师，2-管理员
  [key: string]: any
}

interface UserState {
  userInfo: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userInfo: getUserInfo()
  }),
  
  getters: {
    isLoggedIn: (state): boolean => !!state.userInfo,
    username: (state): string => state.userInfo?.username || ''
  },
  
  actions: {
    // 登录（使用手机号）
    async loginAction(phone: string, password: string) {
      try {
        const res = await login({ phone, password })
        
        // 保存双 Token
        saveToken(res.accessToken, res.refreshToken)
        
        // 保存用户信息
        this.userInfo = res.userInfo
        saveUserInfo(res.userInfo)
        
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 注册（手机号 + 密码，默认客户）
    async registerAction(phone: string, password: string) {
      try {
        const res = await register({ 
          phone,
          password,
          role: 0  // 默认为客户（role=0）
        })
        return Promise.resolve(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 获取当前用户信息
    async getCurrentUserAction() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res
        saveUserInfo(res)
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
        this.userInfo = null
        removeToken()
        router.push('/login')
      }
    }
  }
})
