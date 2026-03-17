<template>
  <div class="service-center">
    <!-- 科技感背景装饰 -->
    <div class="tech-background">
      <div class="grid-overlay"></div>
      <div class="floating-particles"></div>
      <div class="gradient-orb gradient-orb-1"></div>
      <div class="gradient-orb gradient-orb-2"></div>
      <div class="gradient-orb gradient-orb-3"></div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="sc-header">
      <div class="header-content">
        <div class="logo-section">
          <el-icon class="logo-icon"><Food /></el-icon>
          <h1 class="app-title">厨师上门服务中心</h1>
        </div>
        
        <div class="header-actions">
          <!-- 返回首页按钮 -->
          <el-tooltip content="返回首页" placement="bottom">
            <el-button circle class="home-btn" @click="goToHome">
              <el-icon><House /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip content="退出登录" placement="bottom">
            <el-button circle class="logout-btn" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </header>

    <!-- 横向功能导航栏 -->
    <nav class="function-nav" :class="{ 'nav-hidden': isNavHidden }">
      <div class="nav-container">
        <div 
          v-for="item in navItems" 
          :key="item.key"
          class="nav-item"
          :class="{ active: activeNav === item.key }"
          @click="navigateTo(item.key)"
        >
          <div class="nav-icon-wrapper">
            <component :is="item.icon" class="nav-icon" />
          </div>
          <span class="nav-label">{{ item.label }}</span>
          <div class="nav-indicator"></div>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 - 使用动态组件而不是 router-view -->
    <main class="sc-main" ref="mainContainer">
      <transition
        :name="navDirection ? `slide-${navDirection}` : 'fade'"
        mode="out-in"
      >
        <!-- 根据 activeNav 动态渲染对应组件 -->
        <component
          :is="currentComponent"
          v-bind="currentComponentProps"
        />
      </transition>
    </main>

    <!-- 底部 -->
    <footer class="sc-footer">
      <div class="footer-content">
        <p>© 2026 厨师上门服务平台 - 专业厨师上门服务</p>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">联系方式</a>
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
        </div>
      </div>
    </footer>

    <!-- 退出确认对话框 -->
    <el-dialog 
      v-model="logoutDialogVisible" 
      title="退出确认"
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="logout-dialog"
      append-to-body
      top="30vh"
    >
      <div class="logout-message">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <span>确定要退出登录吗？</span>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="logoutDialogVisible = false">再想想</el-button>
          <el-button type="danger" @click="confirmLogout">确定退出</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Food, SwitchButton, Camera, Phone,
  ShoppingCart, CircleCheck, Clock, Star, Top, Right, Finished,
  Menu, Document, Location, Collection, ChatDotRound, 
  Ticket, Service, Calendar, User, Money, Setting, Close, Bell, Lock, Delete, Warning, House
} from '@element-plus/icons-vue'
import { getAccessToken, removeToken, getUserInfo } from '@/utils/token'
import { getUserAvatar } from '@/utils/avatar'
import { ElMessage } from 'element-plus'

// 导入所有子组件 (同步导入，避免 transition 动画问题)
import PersonalCenter from '@/views/service/PersonalCenter.vue'
import BookingChef from '@/views/service/BookingChef.vue'
import Activities from '@/views/service/Activities.vue'
import MyOrders from '@/views/service/MyOrders.vue'
import Favorites from '@/views/service/Favorites.vue'
import Settings from '@/views/service/Settings.vue'

const router = useRouter()

// 当前激活的导航项
const activeNav = ref('profile')
const navDirection = ref('') // 导航方向:'left' | 'right' | ''

// 导航菜单配置
const navItems = [
  { key: 'profile', label: '个人中心', icon: User },
  { key: 'booking', label: '预约厨师', icon: Calendar },
  { key: 'activity', label: '优惠活动', icon: Ticket },
  { key: 'orders', label: '我的订单', icon: Document },
  { key: 'favorites', label: '收藏夹', icon: Collection },
  { key: 'settings', label: '设置', icon: Setting }
]

// 当前组件 - 根据 activeNav 动态返回对应组件 (使用同步组件)
const currentComponent = computed(() => {
 const componentMap = {
  'profile': PersonalCenter,
  'booking': BookingChef,
  'activity': Activities,
  'orders': MyOrders,
  'favorites': Favorites,
  'settings': Settings
 }
 return componentMap[activeNav.value]
})

// 导航跳转
const navigateTo = (key) => {
  // 如果点击的是当前激活的导航项，则隐藏导航栏
  if (activeNav.value === key) {
    isNavHidden.value = !isNavHidden.value  // 切换显示/隐藏状态
    return
  }
  
  const currentIndex = navItems.findIndex(item => item.key === activeNav.value)
  const targetIndex = navItems.findIndex(item => item.key === key)
  
  // 根据导航项位置判断方向
  if (targetIndex > currentIndex) {
    navDirection.value = 'left'
  } else if (targetIndex < currentIndex) {
    navDirection.value = 'right'
  }
  
  const routeMap = {
   'profile': '/service/personal',
   'booking': '/service/booking',
   'activity': '/service/activity',
   'orders': '/service/orders',
   'favorites': '/service/favorites',
   'settings': '/service/settings'
  }
  
  // 先设置 activeNav，等待 DOM 更新后再执行路由跳转
  activeNav.value = key
  
  // 使用 nextTick 确保组件已经准备好
  nextTick(() => {
   // 使用 replace 而不是 push，避免在历史记录中堆叠
   // 重要：使用 replaceState 来避免触发组件重新创建
   window.history.replaceState({}, '', routeMap[key])
  })
}

// 当前组件属性
const currentComponentProps = computed(() => ({
 userPhone: userInfo.value.phone ? formatPhone(userInfo.value.phone) : ''
}))
const isNavHidden = ref(false)  // 导航栏隐藏状态 - 默认显示
let lastScrollPosition = 0  // 上次滚动位置
const mainContainer = ref(null)  // 主容器引用

// 监听滚动事件
const handleScroll = (event) => {
 const currentScrollPosition = window.scrollY || document.documentElement.scrollTop
  
  // 接近顶部时显示导航栏
  if (currentScrollPosition < 10) {
  isNavHidden.value = false
  }
  // 向上滚动时显示导航栏
  else if (currentScrollPosition < lastScrollPosition) {
  isNavHidden.value = false
  }
  // 向下滚动且超过一定距离时隐藏
  else if (currentScrollPosition > lastScrollPosition && currentScrollPosition > 100) {
  isNavHidden.value = true
  }
  
  lastScrollPosition = currentScrollPosition
}

// 处理来自内部元素的滚动
const handleScrollFromElement = (event) => {
 const target = event.target
 const currentScrollPosition = target.scrollTop
  
  // 接近顶部时显示导航栏
  if (currentScrollPosition < 10) {
  isNavHidden.value = false
  }
  // 向上滚动时显示导航栏
  else if (currentScrollPosition < lastScrollPosition) {
  isNavHidden.value = false
  }
  // 向下滚动时隐藏
  else if (currentScrollPosition > lastScrollPosition) {
  isNavHidden.value = true
  }
  
  lastScrollPosition = currentScrollPosition
}

const toggleTheme = () => {
  // 已移除主题切换功能
}

// 用户信息
const userInfo = ref({
  id: null,
  name: '',
  phone: '',
  avatar: '',
  points: 0
})

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'



// 退出登录对话框
const logoutDialogVisible = ref(false)

// 返回首页
const goToHome = () => {
  router.push('/home')
}

// 退出登录
const handleLogout = () => {
  logoutDialogVisible.value = true
}

// 确认退出
const confirmLogout = () => {
  logoutDialogVisible.value = false
  removeToken()
  router.push('/login')
  ElMessage.success('已退出登录')
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const token = getAccessToken()
    if (!token) return
    
    // 1. 先从本地存储获取用户信息（包括头像）
    const cachedUser = getUserInfo()
    if (cachedUser) {
      userInfo.value = {
        ...cachedUser,
        avatar: getUserAvatar(cachedUser)  // 使用缓存的头像
      }
    }
    
    // 2. 如果没有缓存，从 store 重新获取最新数据
    if (!userInfo.value.id) {
      await userStore.getCurrentUserAction()
      const freshUser = getUserInfo()
      if (freshUser) {
        userInfo.value = {
          ...freshUser,
          avatar: getUserAvatar(freshUser)
        }
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}



// 订单操作 - 基础方法（已移除，由子组件自行处理）
// 联系厨师、评价订单等功能应该在各自的子组件中实现

// 事件处理代理方法（已移除，由子组件自行处理）

// 事件处理代理方法（已移除，由子组件自行处理）



// 初始化
onMounted(() => {
 // 延迟添加滚动监听，确保 DOM 已完全渲染
 setTimeout(() => {
   // 检查所有可能有滚动条的元素
 const allElements = document.querySelectorAll('*')
 const scrollableElements = []
   
   allElements.forEach((el) => {
  const hasScroll = el.scrollHeight > el.clientHeight
  const style = getComputedStyle(el)
  const hasOverflow = ['auto', 'scroll', 'overlay'].includes(style.overflowY) || 
                        ['auto', 'scroll', 'overlay'].includes(style.overflow)
     
 if ((hasScroll || hasOverflow) && el.clientHeight > 0) {
      scrollableElements.push(el)
    }
  })
  
   // 为所有找到的可滚动元素添加监听
   scrollableElements.forEach((el) => {
   el.addEventListener('scroll', handleScrollFromElement, { passive: true })
  })

   // 同时也监听 window
   window.addEventListener('scroll', handleScroll, { passive: true })
 }, 500)
})

// 根据路由路径更新激活的导航项
const updateActiveNavFromRoute = (path) => {
 const currentPath = path || router.currentRoute.value.path
 const routeMap = {
  '/service/personal': 'profile',
  '/service/booking': 'booking',
  '/service/activity': 'activity',
  '/service/orders': 'orders',
  '/service/favorites': 'favorites',
  '/service/settings': 'settings'
 }
 
if (routeMap[currentPath]) {
 activeNav.value = routeMap[currentPath]
 }
}

// 组件卸载时移除监听
const cleanupListeners = () => {
  window.removeEventListener('scroll', handleScroll)
  
  const allElements = document.querySelectorAll('*')
  allElements.forEach((el) => {
    const hasScroll = el.scrollHeight > el.clientHeight
    const style = getComputedStyle(el)
    const hasOverflow = ['auto', 'scroll', 'overlay'].includes(style.overflowY) || 
                       ['auto', 'scroll', 'overlay'].includes(style.overflow)
    
    if ((hasScroll || hasOverflow) && el.clientHeight > 0) {
      el.removeEventListener('scroll', handleScrollFromElement)
    }
  })
}

onUnmounted(cleanupListeners)
</script>

<style scoped>
/* CSS 变量定义 - 固定使用暗色科技感主题 */
.service-center {
  --bg-color: #1a1a2e;
  --card-bg: #16213e;
  --text-primary: #fff;
  --text-secondary: #ccc;
  --text-muted: #999;
  --border-color: #334155;
  --shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  --shadow-hover: 0 8px 30px rgba(0, 0, 0, 0.6);
  --tech-bg: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  --grid-color: rgba(0, 217, 255, 0.06);
  --particle-color: rgba(0, 217, 255, 0.2);
  --orb-color-1: rgba(0, 217, 255, 0.12);
  --orb-color-2: rgba(168, 85, 247, 0.1);
  --orb-color-3: rgba(236, 72, 153, 0.08);
  --gradient-primary: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  min-height: 100vh;
  background: var(--tech-bg);
  transition: var(--transition);
  position: relative;
  overflow-x: hidden;
}

/* 科技感背景装饰 */
.tech-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* 网格线效果 */
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(var(--grid-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--grid-color) 1px, transparent 1px);
  background-size: 60px 60px;
  opacity: 0.4;
  animation: gridMove 30s linear infinite;
}

@keyframes gridMove {
  0% { transform: translateY(0); }
  100% { transform: translateY(60px); }
}

/* 漂浮粒子 */
.floating-particles {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(var(--particle-color) 1.5px, transparent 1.5px);
  background-size: 80px 80px;
  opacity: 0.5;
  animation: particleFloat 40s ease-in-out infinite;
}

@keyframes particleFloat {
  0%, 100% { opacity: 0.5; transform: translateY(0) scale(1); }
  50% { opacity: 0.7; transform: translateY(-20px) scale(1.05); }
}

/* 渐变光球 - 营造科技感氛围 */
.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: orbFloat 20s ease-in-out infinite;
}

.gradient-orb-1 {
  width: 400px;
  height: 400px;
  background: var(--orb-color-1);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.gradient-orb-2 {
  width: 300px;
  height: 300px;
  background: var(--orb-color-2);
  bottom: -50px;
  left: -50px;
  animation-delay: 7s;
}

.gradient-orb-3 {
  width: 250px;
  height: 250px;
  background: var(--orb-color-3);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 14s;
}

@keyframes orbFloat {
  0%, 100% { 
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(30px, -30px) scale(1.1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-20px, 20px) scale(0.9);
    opacity: 0.7;
  }
  75% {
    transform: translate(20px, 30px) scale(1.05);
    opacity: 0.55;
  }
}

/* 确保内容在背景之上 */
.sc-header,
.function-nav,
.sc-main,
.sc-footer {
  position: relative;
  z-index: 1;
}

/* 滚动渐入动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(60px);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.scroll-animate.animate-active {
  opacity: 1;
  transform: translateY(0);
}

/* 顶部导航栏样式（保留原有的 sc-header 样式）*/
.sc-header {
  background: var(--card-bg);
  box-shadow: var(--shadow);
  position: fixed;  /* 改为 fixed 始终固定在顶部 */
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
  color: #667eea;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.app-title {
  font-size: 20px;
  font-weight: bold;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-info-mini {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
  color: var(--text-secondary);
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 返回首页按钮样式 - 与退出按钮一致 */
.home-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.home-btn:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.home-btn:active {
  transform: translateY(0) scale(0.98);
}

.home-btn .el-icon {
  font-size: 18px;
  color: white;
}

/* 退出按钮样式 */
.logout-btn {
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(245, 108, 108, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logout-btn:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 20px rgba(245, 108, 108, 0.5);
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}

.logout-btn:active {
  transform: translateY(0) scale(0.98);
}

.logout-btn .el-icon {
  font-size: 18px;
  color: white;
}

/* 横向功能导航栏 */
.function-nav {
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow);
  position: fixed;  /* 改为 fixed 以获得更好的控制 */
  top: 62px;  /* 紧贴顶部导航栏下方 (顶部导航栏高度约 70px) */
  left: 0;
  right: 0;
  z-index: 99;
  backdrop-filter: blur(10px);
  padding: 5px 0;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateY(0);
}

/* 导航栏隐藏状态 */
.function-nav.nav-hidden {
  transform: translateY(-100%) !important;  /* 使用 !important 确保优先级 */
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 5px;
}

.nav-item {
  position: relative;
  padding: 10px 8px;  /* 从 20px 10px 减小到 10px 8px */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;  /* 从 8px 减小到 6px */
  cursor: pointer;
  transition: var(--transition);
  border-bottom: 3px solid transparent;
  user-select: none;
}

.nav-item::before {
  content: '';
  position: absolute;
  inset: 5px;
  border-radius: 8px;
  background: transparent;
  transition: var(--transition);
}

.nav-item:hover::before {
  background: rgba(102, 126, 234, 0.08);
}

.nav-item.active::before {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.nav-item.active {
  border-bottom-color: #667eea;
}

.nav-icon-wrapper {
  width: 30px;
  height: 30px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3d3d3d 0%, #2d2d2d 100%);
  transition: var(--transition);
}

.nav-item:hover .nav-icon-wrapper {
  transform: translateY(-3px) scale(1.05);
}

.nav-item.active .nav-icon-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.nav-icon {
  font-size: 20px;  /* 从 24px 减小到 20px */
  color: #a5b4fc;  /* 使用更亮的颜色，提高对比度 */
  transition: var(--transition);
}

.nav-item.active .nav-icon {
  color: white;
}

.nav-label {
  font-size: 12px;  /* 从 14px 减小到 12px */
  color: var(--text-secondary);
  font-weight: 500;
  transition: var(--transition);
}

.nav-item:hover .nav-label {
  color: var(--text-primary);
}

.nav-item.active .nav-label {
  color: #667eea;
  font-weight: 600;
}

.nav-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 30px;  /* 从 40px 减小到 30px */
  height: 2px;  /* 从 3px 减小到 2px */
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px 3px 0 0;
  transition: transform 0.3s ease;
}

.nav-item.active .nav-indicator {
  transform: translateX(-50%) scaleX(1);
}

.content-panel {
  min-height: 600px;
}

/* 内容面板过渡动画 - 基础淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from {
  opacity: 0;
}

.fade-leave-to {
  opacity: 0;
}

/* 向左滑动动画 (切换到右侧导航项) - 增强版 */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55); /* 回弹效果 */
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100%) scale(0.9);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%) scale(0.9);
}

/* 向右滑动动画 (切换到左侧导航项) - 增强版 */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55); /* 回弹效果 */
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-100%) scale(0.9);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(100%) scale(0.9);
}

/* 主要内容区域 */
.sc-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 130px 30px 30px;  /* 顶部留出两个导航栏的高度：70px(顶部) + 50px(横向导航) +10px(间距) */
}

/* 底部 */
.sc-footer {
  background: var(--card-bg);
  border-top: 1px solid var(--border-color);
  padding: 30px;
  margin-top: 50px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-content p {
  color: var(--text-muted);
  margin-bottom: 15px;
  font-size: 14px;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.footer-links a {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: var(--transition);
}

.footer-links a:hover {
  color: #667eea;
}

/* 对话框暗色模式 */
.dark-dialog :deep(.el-dialog) {
  background: var(--card-bg);
}

.dark-dialog :deep(.el-dialog__title),
.dark-dialog :deep(.el-form-item__label) {
  color: var(--text-primary);
}

/* 退出对话框美化 - 科技感风格 */
:deep(.logout-dialog) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border: 1px solid rgba(102, 126, 234, 0.3);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.6), 0 0 60px rgba(102, 126, 234, 0.2);
  max-width: 420px;
}

:deep(.logout-dialog .el-message-box__header) {
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
}

:deep(.logout-dialog .el-message-box__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.logout-dialog .el-message-box__title .el-icon-warning) {
  color: #f56c6c;
  font-size: 22px;
  animation: warning-pulse 2s ease-in-out infinite;
}

@keyframes warning-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

:deep(.logout-dialog .el-message-box__content) {
  padding: 25px 0;
}

:deep(.logout-dialog .logout-message) {
  color: #ccc;
  font-size: 15px;
  line-height: 1.6;
  text-align: center;
}

:deep(.logout-dialog .el-message-box__btns) {
  padding-top: 15px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
  display: flex;
  gap: 12px;
  justify-content: center;
}

:deep(.logout-dialog .el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 10px 30px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.logout-dialog .el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

:deep(.logout-dialog .el-button--primary:active) {
  transform: translateY(0);
}

:deep(.logout-dialog .el-button--default) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(102, 126, 234, 0.3);
  color: #ccc;
  padding: 10px 30px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.logout-dialog .el-button--default:hover) {
  background: rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  color: #fff;
  transform: translateY(-2px);
}

:deep(.logout-dialog .el-button--default:active) {
  transform: translateY(0);
}

/* 地址列表 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  border: 2px solid var(--border-color);
  border-radius: 12px;
  padding: 15px;
  cursor: pointer;
  transition: var(--transition);
}

.address-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.address-item.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.contact-name {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-primary);
}

.contact-phone {
  font-size: 14px;
  color: var(--text-secondary);
}

.address-detail {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.address-actions {
  display: flex;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .nav-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .nav-container {
    grid-template-columns: repeat(2, 1fr);
    padding: 0 15px;
  }
  
  .nav-item {
    padding: 15px 10px;
  }
  
  .header-content {
    padding: 15px;
  }
  
  .sc-main {
    padding: 15px;
  }
}
</style>

<!-- 全局样式：用于覆盖 Element Plus Dialog 默认样式（因为 Dialog 使用 Teleport 挂载到 body，scoped 样式无法生效） -->
<style>
/* 退出对话框美化 - 科技感风格（全局样式，强制覆盖） */
.el-dialog.logout-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  border-radius: 16px !important;
  padding: 20px !important;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.6), 0 0 60px rgba(102, 126, 234, 0.2) !important;
  max-width: 420px;
}

.el-dialog.logout-dialog .el-dialog__header {
  padding-bottom: 15px !important;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2) !important;
}

.el-dialog.logout-dialog .el-dialog__title {
  color: #fff !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
}

/* 警告图标动画 */
.el-dialog.logout-dialog .warning-icon {
  color: #f56c6c !important;
  font-size: 22px !important;
  animation: warning-pulse 2s ease-in-out infinite !important;
}

@keyframes warning-pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

.el-dialog.logout-dialog .el-dialog__body {
  padding: 25px 0 !important;
}

.el-dialog.logout-dialog .logout-message {
  color: #ccc !important;
  font-size: 15px !important;
  line-height: 1.6 !important;
  text-align: center !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 10px !important;
}

.el-dialog.logout-dialog .el-dialog__footer {
  padding-top: 15px !important;
  border-top: 1px solid rgba(102, 126, 234, 0.2) !important;
  display: flex !important;
  gap: 12px !important;
  justify-content: center !important;
}

.el-dialog.logout-dialog .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  padding: 10px 30px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-dialog.logout-dialog .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5) !important;
}

.el-dialog.logout-dialog .el-button--primary:active {
  transform: translateY(0) !important;
}

.el-dialog.logout-dialog .el-button--default {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  color: #ccc !important;
  padding: 10px 30px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  border-radius: 8px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-dialog.logout-dialog .el-button--default:hover {
  background: rgba(102, 126, 234, 0.15) !important;
  border-color: #667eea !important;
  color: #fff !important;
  transform: translateY(-2px) !important;
}

.el-dialog.logout-dialog .el-button--default:active {
  transform: translateY(0) !important;
}

/* 修复 Dialog 打开时滚动条导致的布局偏移问题 */
body.el-popup-parent--hidden {
  width: 100% !important;
  overflow: hidden !important;
  padding-right: 0 !important; /* 强制移除 Element Plus 自动添加的右侧内边距 */
}

/* 确保内容区域不受滚动条影响 */
.service-center {
  width: 100%;
  overflow-x: hidden;
}
</style>
