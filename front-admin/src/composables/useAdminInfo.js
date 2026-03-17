import { ref, onMounted } from 'vue'

/**
 * 获取并管理当前登录的管理员信息
 */
export function useAdminInfo() {
  // 管理员信息（从 localStorage 获取）
  const adminInfo = ref({
    userId: null,
    realName: '',
    avatar: ''
  })

  // 加载管理员信息
  const loadAdminInfo = () => {
    const userInfoStr = localStorage.getItem('admin-userInfo')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        adminInfo.value = {
          userId: userInfo.id || userInfo.userId,
          realName: userInfo.realName || userInfo.username || '管理员',
          avatar: userInfo.avatar || ''
        }
      } catch (e) {
        console.error('解析管理员信息失败:', e)
      }
    }
  }

  // 清除管理员信息
  const clearAdminInfo = () => {
    adminInfo.value = {
      userId: null,
      realName: '',
      avatar: ''
    }
  }

  // 组件挂载时自动加载
  onMounted(() => {
    loadAdminInfo()
  })

  return {
    adminInfo,
    loadAdminInfo,
    clearAdminInfo
  }
}
