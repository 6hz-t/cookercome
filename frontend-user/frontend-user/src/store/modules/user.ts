import { defineStore } from 'pinia'
import { login, register, logout, getCurrentUser } from '@/api/auth'
import router from '@/router'
import { saveToken, removeToken, saveUserInfo, getUserInfo, saveAccountInfo, clearAccountInfo, saveAvatarCache } from '@/utils/token'

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
    async loginAction(phone: string, password: string, rememberMe: boolean = true) {
      try {
        const res = await login({ phone, password })
        
        // 保存双 Token（注意：后端返回的是 Result 包装，数据在 res.data 中）
        saveToken(res.data.accessToken, res.data.refreshToken, rememberMe)
        
        // ✅ 先清除旧的账号信息（避免取消勾选后仍保留）
        clearAccountInfo()
        
        // 如果勾选记住我，保存加密的账号信息
        if (rememberMe) {
          saveAccountInfo(phone, password)
        }
        
        // 保存用户信息（根据是否记住我选择存储位置）
        this.userInfo = res.data.userInfo
        saveUserInfo(res.data.userInfo, rememberMe)
        
        // 💡 保存头像缓存
        if (res.data.userInfo && res.data.userInfo.avatar) {
          saveAvatarCache(res.data.userInfo.avatar, rememberMe)
        }
        
        return Promise.resolve(res.data)
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
        
        // 💡 如果后端返回了头像，更新缓存
        if (res && res.avatar) {
          // 根据当前存储位置判断是否记住我
          const rememberMe = localStorage.getItem('access_token') !== null
          saveAvatarCache(res.avatar, rememberMe)
        }
        
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
        clearAccountInfo()  // 清除记住的账号信息
        router.push('/login')
      }
    }
  }
})
