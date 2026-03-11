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
        
        <div class="user-info-mini">
          <el-avatar :size="40" :src="userInfo.avatar || defaultAvatar" />
          <span class="user-name">{{ userInfo.name || '用户' }}</span>
        </div>
        
        <div class="header-actions">
          <el-tooltip content="退出登录" placement="bottom">
            <el-button circle type="danger" @click="handleLogout">
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
            <el-icon class="nav-icon" :component="item.icon" />
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
        @before-enter="onBeforeEnter"
        @enter="onEnter"
        @after-enter="onAfterEnter"
        @before-leave="onBeforeLeave"
        @leave="onLeave"
        @after-leave="onAfterLeave"
      >
        <!-- 根据 activeNav 动态渲染对应组件 -->
        <component
          :is="currentComponent"
          v-bind="currentComponentProps"
          @show-address="showAddressDialog = true"
          @cancel-order="handleCancelOrder"
          @contact-chef="handleContactChef"
          @review-order="handleReviewOrder"
          @book-again="handleBookAgain"
          @book-chef="handleBookChef"
          @join-activity="handleJoinActivity"
          @view-detail="handleViewOrderDetail"
          @remove-favorite="handleRemoveFavorite"
          @edit-profile="handleEditProfile"
          @change-phone="handleChangePhone"
          @change-password="handleChangePassword"
          @delete-account="handleDeleteAccount"
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

    <!-- 地址管理对话框 -->
    <el-dialog v-model="showAddressDialog" title="地址管理" width="600px" class="dark-dialog">
      <div class="address-list">
        <div 
          v-for="(addr, index) in addresses" 
          :key="index"
          class="address-item"
          :class="{ active: addr.isDefault }"
          @click="selectAddress(addr)"
        >
          <div class="address-header">
            <div class="address-info">
              <span class="contact-name">{{ addr.name }}</span>
              <span class="contact-phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" size="small" type="success">默认</el-tag>
            </div>
            <div class="address-actions">
              <el-button size="small" @click.stop="editAddress(addr)">编辑</el-button>
              <el-button size="small" type="danger" @click.stop="deleteAddress(index)">删除</el-button>
            </div>
          </div>
          <p class="address-detail">
            <el-icon><Location /></el-icon>
            {{ addr.fullAddress }}
          </p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showAddAddressForm = true">新增地址</el-button>
        <el-button type="primary" @click="showAddressDialog = false">完成</el-button>
      </template>
    </el-dialog>

    <!-- 添加地址对话框 -->
    <el-dialog v-model="showAddAddressForm" title="添加新地址" width="500px" class="dark-dialog">
      <el-form :model="newAddress" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="newAddress.name" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="newAddress.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-input v-model="newAddress.district" placeholder="请选择所在地区" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input 
            v-model="newAddress.detail" 
            type="textarea" 
            :rows="3"
            placeholder="请输入详细地址，如街道、门牌号等" 
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="newAddress.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAddressForm = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
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
  Ticket, Service, Calendar, User, Money, Setting, Close, Bell, Lock, Delete
} from '@element-plus/icons-vue'
import { getAccessToken, removeToken, getUserInfo } from '@/utils/token'
import { getUserAvatar } from '@/utils/avatar'
import { ElMessage, ElMessageBox } from 'element-plus'

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
 console.log('[currentComponent] 计算属性触发，activeNav.value:', activeNav.value)
 const componentMap = {
  'profile': PersonalCenter,
  'booking': BookingChef,
  'activity': Activities,
  'orders': MyOrders,
  'favorites': Favorites,
  'settings': Settings
 }
 const result = componentMap[activeNav.value]
 console.log('[currentComponent] 返回组件:', result ? result.__name || 'unknown' : 'undefined')
 return result
})

// 导航跳转
const navigateTo = (key) => {
 console.log('=== 导航切换开始 ===')
 console.log('[navigateTo] 当前激活的导航项:', activeNav.value)
 console.log('[navigateTo] 目标导航项:', key)
 console.log('[navigateTo] 调用时间:', new Date().toISOString())

 const currentIndex = navItems.findIndex(item => item.key === activeNav.value)
 const targetIndex = navItems.findIndex(item => item.key === key)
 
 console.log('[navigateTo] 当前位置索引:', currentIndex)
 console.log('[navigateTo] 目标位置索引:', targetIndex)
 
 // 根据导航项位置判断方向
 if (targetIndex > currentIndex) {
 navDirection.value = 'left' // 向右切换，内容向左移出
 console.log('[navigateTo] 设置动画方向：向左 slide-left')
 } else if (targetIndex < currentIndex) {
 navDirection.value = 'right' // 向左切换，内容向右移出
 console.log('[navigateTo] 设置动画方向：向右 slide-right')
 } else {
 console.log('[navigateTo] 位置相同，不设置方向')
 }
 
 console.log('[navigateTo] navDirection 当前值:', navDirection.value)
 
 const routeMap = {
  'profile': '/service/personal',
  'booking': '/service/booking',
  'activity': '/service/activity',
  'orders': '/service/orders',
  'favorites': '/service/favorites',
  'settings': '/service/settings'
 }
 console.log('[navigateTo] 即将路由跳转到:', routeMap[key])
 
 // 先设置 activeNav，等待 DOM 更新后再执行路由跳转
 console.log('[navigateTo] 设置 activeNav.value =', key)
 activeNav.value = key
 
 console.log('[navigateTo] 等待 nextTick...')
 // 使用 nextTick 确保组件已经准备好
 nextTick(() => {
 console.log('[navigateTo] nextTick 回调执行，当前 activeNav.value:', activeNav.value)
 console.log('[navigateTo] 执行 router.replace:', routeMap[key])
  // 使用 replace 而不是 push，避免在历史记录中堆叠
  // 重要：使用 replaceState 来避免触发组件重新创建
  window.history.replaceState({}, '', routeMap[key])
 console.log('[navigateTo] history.replaceState 完成')
 console.log('=== 导航切换结束 ===\n')
 })
}

// 当前组件属性
const currentComponentProps = computed(() => ({
 userPhone: userInfo.value.phone ? formatPhone(userInfo.value.phone) : ''
}))

// 主题切换 (已移除，固定使用暗色模式)
// const isDarkMode = ref(false)
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

// 地址管理
const showAddressDialog = ref(false)
const showAddAddressForm = ref(false)
const addresses = ref([
  {
    name: '张三',
    phone: '138****1234',
    isDefault: true,
    fullAddress: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室'
  },
  {
    name: '李四',
    phone: '139****5678',
    isDefault: false,
    fullAddress: '上海市浦东新区陆家嘴环路 1000 号 B 座 2002 室'
  }
])

const newAddress = ref({
  name: '',
  phone: '',
  district: '',
  detail: '',
  isDefault: false
})

// 格式化手机号
const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    removeToken()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {})
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
    
    // 2. （可选）如果需要，可以从后端获取最新数据
    // await userStore.getCurrentUserAction()
    // userInfo.value.avatar = getUserAvatar(userInfo.value)
    
    // 临时测试数据
    if (!userInfo.value.name) {
      userInfo.value = {
        id: 7,
        name: '张先生',
        phone: '13579246810',
        avatar: getUserAvatar(),  // 使用缓存头像或默认头像
        points: 2580
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 地址操作
const selectAddress = (addr) => {
  console.log('选择地址:', addr)
}

const editAddress = (addr) => {
  newAddress.value = { ...addr }
  showAddAddressForm.value = true
}

const deleteAddress = (index) => {
  addresses.value.splice(index, 1)
  ElMessage.success('地址已删除')
}

const saveAddress = () => {
  if (!newAddress.value.name || !newAddress.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  addresses.value.push({
    ...newAddress.value,
    fullAddress: `${newAddress.value.district}${newAddress.value.detail}`
  })
  
  showAddAddressForm.value = false
  newAddress.value = {
    name: '',
    phone: '',
    district: '',
    detail: '',
    isDefault: false
  }
  
  ElMessage.success('地址保存成功')
}

// 订单操作 - 基础方法
const cancelOrder = (orderId) => {
  ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('订单已取消')
  }).catch(() => {})
}

const contactChef = (order) => {
  ElMessage.info(`正在联系${order.chefName}...`)
}

const reviewOrder = (order) => {
  ElMessageBox.prompt('请为本次服务打分并写下评价', '评价', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '评价内容不能为空'
  }).then(({ value }) => {
    ElMessage.success('评价提交成功')
  }).catch(() => {})
}

const bookAgain = (order) => {
  ElMessage.success('正在为您重新预约...')
}

// 事件处理代理方法
const handleCancelOrder = (orderId) => cancelOrder(orderId)
const handleContactChef = (order) => contactChef(order)
const handleReviewOrder = (order) => reviewOrder(order)
const handleBookAgain = (order) => bookAgain(order)

const handleBookChef = (chef) => {
  ElMessageBox.confirm(`确定要预约${chef.name}师傅吗？`, '预约确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    router.push('/chefs')
    ElMessage.success('跳转至预约页面')
  }).catch(() => {})
}

const handleJoinActivity = (activity) => {
  ElMessage.success(`已参与${activity.title}`)
}

const handleViewOrderDetail = (order) => {
  router.push(`/order/${order.id}`)
}

const handleRemoveFavorite = (id) => {
  ElMessage.success('已取消收藏')
}

const handleEditProfile = () => {
  ElMessage.info('个人信息编辑功能开发中')
}

const handleChangePhone = () => {
  ElMessage.info('手机号修改功能开发中')
}

const handleChangePassword = () => {
  ElMessage.info('密码修改功能开发中')
}

const handleDeleteAccount = () => {
  ElMessageBox.confirm('确定要注销账户吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    ElMessage.error('注销功能开发中')
  }).catch(() => {})
}

// 动画钩子函数 - 用于调试
const onBeforeEnter= (el) => {
 console.log('🎬 [动画钩子] before-enter')
 console.log('  navDirection:', navDirection.value)
 console.log('   el:', el)
 console.log('   el 类型:', el.constructor.name)
}

const onEnter = (el) => {
 console.log('🎬 [动画钩子] enter')
 console.log('  navDirection:', navDirection.value)
  // 不需要调用 done，Vue 会自动等待 CSS 动画完成
}

const onAfterEnter= (el) => {
 console.log('🎬 [动画钩子] after-enter')
 console.log('  el:', el)
}

const onBeforeLeave = (el) => {
 console.log('🎬 [动画钩子] before-leave')
 console.log('  navDirection:', navDirection.value)
 console.log('   el:', el)
}

const onLeave = (el) => {
 console.log('🎬 [动画钩子] leave')
 console.log('  navDirection:', navDirection.value)
 console.log('   el:', el)
  // 不要调用 done！Vue 会自动等待 CSS 动画完成
  // 手动调用 done 会导致元素被立即移除而报错
}

const onAfterLeave = (el) => {
 console.log('🎬 [动画钩子] after-leave')
 console.log('  el:', el)
 // 动画完成后重置方向 - 移到下一帧执行，确保不会干扰渲染
 requestAnimationFrame(() => {
  navDirection.value = ''
 console.log('动画完成，已重置 navDirection 为空')
 })
}

// 监听 navDirection 变化
watch(navDirection, (newVal, oldVal) => {
  console.log('👀 [Watch 监听] navDirection 变化:')
  console.log('  旧值:', oldVal)
  console.log('  新值:', newVal)
  console.trace('调用堆栈:') // 打印调用堆栈，找出是谁修改了值
})

// 监听 activeNav 变化
watch(activeNav, (newVal, oldVal) => {
 console.log('👀 [Watch 监听] activeNav 变化:')
 console.log('  旧值:', oldVal)
 console.log('  新值:', newVal)
 console.trace('调用堆栈:') // 打印调用堆栈，找出是谁修改了值
})

// 初始化
onMounted(() => {
 console.log('[onMounted] ServiceCenter 组件挂载')
 console.log('[onMounted] 当前路由路径:', router.currentRoute.value.path)
 console.log('[onMounted] 当前 activeNav:', activeNav.value)

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
 console.log('[updateActiveNavFromRoute] 当前路径:', currentPath)
 const routeMap = {
  '/service/personal': 'profile',
  '/service/booking': 'booking',
  '/service/activity': 'activity',
  '/service/orders': 'orders',
  '/service/favorites': 'favorites',
  '/service/settings': 'settings'
 }
 
if (routeMap[currentPath]) {
 console.log('[updateActiveNavFromRoute] 设置 activeNav 为:', routeMap[currentPath])
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

/* 横向功能导航栏 */
.function-nav {
  background: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow);
  position: fixed;  /* 改为 fixed 以获得更好的控制 */
  top: 70px;  /* 紧贴顶部导航栏下方 (顶部导航栏高度约 70px) */
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
  width: 40px;
  height: 40px;
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
  color: #667eea;
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
