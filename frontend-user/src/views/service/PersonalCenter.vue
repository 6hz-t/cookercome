<template>
  <div class="personal-center">
    <!-- 用户信息卡片 -->
    <div class="user-card holographic">
      <div class="card-border-glow"></div>
      <div class="card-corner tl"></div>
      <div class="card-corner tr"></div>
      <div class="card-corner bl"></div>
      <div class="card-corner br"></div>

      <div class="user-card-content">
        <div class="avatar-wrapper">
          <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar" class="user-avatar" />
          <div class="level-badge" :class="`level-${userInfo.memberLevel}`">
            <span>{{ getMemberLevelText(userInfo.memberLevel) }}</span>
          </div>
        </div>

        <div class="user-details">
          <h2 class="username">
            <span class="name-text">{{ userInfo.name || '未知用户' }}</span>
            <el-tag 
              v-if="userInfo.memberLevel > 0"
              type="danger"
              size="small" 
              effect="dark" 
              class="vip-tag"
            >
              VIP
            </el-tag>
          </h2>
          <p class="user-phone">
            <el-icon><Phone /></el-icon>
            {{ formatPhone(userInfo.phone) }}
          </p>

          <div class="points-display">
            <div class="points-circle">
              <svg viewBox="0 0 100 100" class="progress-svg">
                <defs>
                  <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" style="stop-color:#00f3ff;stop-opacity:1" />
                    <stop offset="100%" style="stop-color:#bc13fe;stop-opacity:1" />
                  </linearGradient>
                </defs>
                <!-- 背景圆环 -->
                <circle cx="50" cy="50" r="45" class="progress-bg" />
                <!-- 进度圆环 -->
                <circle 
                  cx="50" 
                  cy="50" 
                  r="45" 
                  class="progress-bar"
                  :stroke-dasharray="circumference"
                  :stroke-dashoffset="dashOffset"
                />
              </svg>
              <span class="points-value">{{ userInfo.points || 0 }}</span>
            </div>
            <div class="points-info">
              <span class="points-label">当前积分</span>
              <span v-if="nextLevelPoints > 0" class="level-tip">再得 {{ nextLevelPoints }} 分升级{{ getNextLevelText(userInfo.memberLevel) }}</span>
              <span v-else class="level-tip max-level">已达最高等级</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-dashboard">
      <div class="stat-item stat-orders">
        <div class="stat-hexagon">
          <svg viewBox="0 0 100 100" class="hex-svg">
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-bg" />
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-border" />
          </svg>
          <div class="stat-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ stats.totalOrders }}</div>
        <div class="stat-label">总订单</div>
      </div>

      <div class="stat-item stat-completed">
        <div class="stat-hexagon">
          <svg viewBox="0 0 100 100" class="hex-svg">
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-bg" />
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-border" />
          </svg>
          <div class="stat-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ stats.completedOrders }}</div>
        <div class="stat-label">已完成</div>
      </div>

      <div class="stat-item stat-ongoing">
        <div class="stat-hexagon">
          <svg viewBox="0 0 100 100" class="hex-svg">
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-bg" />
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-border" />
          </svg>
          <div class="stat-icon">
            <el-icon><Clock /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ stats.ongoingOrders }}</div>
        <div class="stat-label">进行中</div>
      </div>

      <div class="stat-item stat-rating">
        <div class="stat-hexagon">
          <svg viewBox="0 0 100 100" class="hex-svg">
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-bg" />
            <polygon points="50,5 95,27.5 95,72.5 50,95 5,72.5 5,27.5" class="hex-border" />
          </svg>
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
        </div>
        <div class="stat-value">{{ stats.averageRating }}</div>
        <div class="stat-label">平均评分</div>
      </div>
    </div>

    <!-- 核心功能 -->
    <div class="functions-section">
      <div class="section-header-tech">
        <h3 class="section-title">
          <el-icon class="title-icon"><Menu /></el-icon>
          <span class="title-text">我的服务</span>
        </h3>
      </div>

      <div class="function-grid">
        <div class="function-card func-1" @click="$router.push('/order')">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><Document /></el-icon>
          </div>
          <span class="func-label">我的订单</span>
          <el-badge :value="stats.ongoingOrders" :hidden="stats.ongoingOrders === 0" class="func-badge" />
        </div>

        <div class="function-card func-2" @click="showAddressDialog = true">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><Location /></el-icon>
          </div>
          <span class="func-label">地址管理</span>
        </div>

        <div class="function-card func-3">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><Collection /></el-icon>
          </div>
          <span class="func-label">我的收藏</span>
          <el-badge :value="favoritesCount" :hidden="favoritesCount === 0" class="func-badge" />
        </div>

        <div class="function-card func-4">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><ChatDotRound /></el-icon>
          </div>
          <span class="func-label">评价记录</span>
        </div>

        <div class="function-card func-5">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><Ticket /></el-icon>
          </div>
          <span class="func-label">优惠券</span>
          <el-badge :value="couponsCount" :hidden="couponsCount === 0" class="func-badge" />
        </div>

        <div class="function-card func-6">
          <div class="func-icon-wrapper">
            <el-icon class="func-icon"><Service /></el-icon>
          </div>
          <span class="func-label">客服中心</span>
        </div>
      </div>
    </div>

    <!-- 预约记录 -->
    <div class="booking-section">
      <div class="section-header-tech">
        <h3 class="section-title">
          <el-icon class="title-icon"><Calendar /></el-icon>
          <span class="title-text">预约记录</span>
        </h3>
      </div>

      <el-tabs v-model="activeTab" class="booking-tabs tech-tabs">
        <el-tab-pane name="ongoing">
          <template #label>
            <span class="tab-label">
              <el-icon><Clock /></el-icon>
              进行中
            </span>
          </template>

          <div class="booking-list">
            <div
              v-for="order in ongoingOrders"
              :key="order.id"
              class="booking-card"
              @click="$router.push(`/order/${order.id}`)"
            >
              <div class="booking-content">
                <div class="chef-info">
                  <el-avatar :size="50" :src="order.chefAvatar" />
                  <div class="chef-details">
                    <h4>{{ order.chefName }}</h4>
                    <p>{{ order.serviceType }}</p>
                  </div>
                </div>
                <el-tag :type="getStatusType(order.status)" size="small" effect="dark" class="status-tag">{{ getStatusText(order.status) }}</el-tag>
              </div>

              <div class="booking-info">
                <div class="info-row">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ order.address }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ order.guestCount }}人</span>
                  <el-divider direction="vertical" />
                  <el-icon><Money /></el-icon>
                  <span class="price-highlight">¥{{ order.totalAmount }}</span>
                </div>
              </div>

              <div class="booking-actions" v-if="order.status === 'confirmed'">
                <el-button size="small" plain @click.stop="cancelOrder(order.id)">取消订单</el-button>
                <el-button size="small" type="primary" @click.stop="contactChef(order)">联系厨师</el-button>
              </div>
            </div>

            <el-empty v-if="ongoingOrders.length === 0" description="暂无进行中的订单" />
          </div>
        </el-tab-pane>

        <el-tab-pane name="history">
          <template #label>
            <span class="tab-label">
              <el-icon><Finished /></el-icon>
              历史订单
            </span>
          </template>

          <div class="booking-list">
            <div
              v-for="order in historyOrders"
              :key="order.id"
              class="booking-card history"
              @click="$router.push(`/order/${order.id}`)"
            >
              <div class="booking-content">
                <div class="chef-info">
                  <el-avatar :size="50" :src="order.chefAvatar" />
                  <div class="chef-details">
                    <h4>{{ order.chefName }}</h4>
                    <p>{{ order.serviceType }}</p>
                  </div>
                </div>
                <el-tag :type="getStatusType(order.status)" size="small" effect="dark">{{ getStatusText(order.status) }}</el-tag>
              </div>

              <div class="booking-info">
                <div class="info-row">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Money /></el-icon>
                  <span class="price-highlight">¥{{ order.totalAmount }}</span>
                </div>
              </div>

              <div class="booking-actions">
                <el-button size="small" plain @click.stop="reviewOrder(order)">评价</el-button>
                <el-button size="small" type="primary" @click.stop="bookAgain(order)">再次预约</el-button>
              </div>
            </div>

            <el-empty v-if="historyOrders.length === 0" description="暂无历史订单" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

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

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { 
  Phone, ShoppingCart, CircleCheck, Clock, Star,Finished,
  Menu, Document, Location, Collection, ChatDotRound, Ticket, Service, Calendar, User, Money
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getPersonalCenterStats, getCustomerProfile } from '@/api/personalCenter'

const emit = defineEmits(['show-address', 'cancel-order', 'contact-chef', 'review-order', 'book-again'])

// 用户信息
const userInfo = ref({
  id: null,
  name: '',
  phone: '',
  avatar: '',
  points: 0,
  memberLevel: 0
})

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CiAgPGNpcmNsZSBjeD0iMTAwIiBjeT0iMTAwIiByPSIxMDAiIGZpbGw9IiNFMEUwRTAiLz4KICA8Y2lyY2xlIGN4PSIxMDAiIGN5PSI3NSIgcj0iMzAiIGZpbGw9IiM5RTlFOUUiLz4KICA8ZWxsaXBzZSBjeD0iMTAwIiBjeT0iMTQ1IiByeD0iNTAiIHJ5PSI0MCIgZmlsbD0iIzlFOUU5RSIvPgo8L3N2Zz4='

// 会员等级配置（积分升会员策略）
interface MemberLevelConfig {
  name: string
  nextPoints: number
  tagType: string
}

// 圆环周长
const radius = 45
const circumference = 2 * Math.PI * radius

const MEMBER_LEVEL_CONFIG: Record<number, MemberLevelConfig> = {
  0: { name: '普通会员', nextPoints: 1000, tagType: 'info' },           // 0-999 分：普通会员
  1: { name: '白银会员', nextPoints: 3000, tagType: 'success' },        // 1000-2999 分：白银会员（升级扣除 1000 分）
  2: { name: '黄金会员', nextPoints: 5000, tagType: 'warning' },        // 3000-4999 分：黄金会员（升级扣除 3000 分）
  3: { name: '铂金会员', nextPoints: 10000, tagType: '' },              // 5000-9999 分：铂金会员（升级扣除 5000 分）
  4: { name: '钻石会员', nextPoints: 0, tagType: 'danger' }             // 10000+ 分：钻石会员（升级扣除 10000 分，最高级）
}

// 计算当前积分进度
const pointsProgress = computed(() => {
  const currentPoints = userInfo.value.points || 0
  const currentLevel = userInfo.value.memberLevel || 0
  const config = MEMBER_LEVEL_CONFIG[currentLevel]
  
  console.log('积分进度计算:', {
    currentPoints,
    currentLevel,
    levelName: config?.name,
    nextPoints: config?.nextPoints
  })
  
  if (!config || config.nextPoints === 0) {
    console.log('最高等级，返回 1')
    return 1
  }
  
  // 获取当前等级的起始积分（升级到当前等级所需的积分）
  const levelStartPoints = currentLevel === 0 ? 0 : MEMBER_LEVEL_CONFIG[currentLevel - 1].nextPoints
  
  // 计算在当前等级的进度：使用当前积分直接除以当前等级所需积分
  // 例如：白银会员有 200 积分，下一级需要 3000 积分，进度 = 200/3000 = 6.67%
  const progress = currentPoints / config.nextPoints
  const result = Math.min(Math.max(progress, 0), 1)
  
  console.log('进度计算详情:', {
    levelStartPoints,
    nextLevelPoints: config.nextPoints,
    rawProgress: progress,
    finalProgress: result
  })
  
  return result
})

// 计算 stroke-dashoffset
const dashOffset = computed(() => {
  const progress = pointsProgress.value
  return circumference * (1 - progress)
})

// 计算距离下一级还需多少积分
const nextLevelPoints = computed(() => {
  const currentPoints = userInfo.value.points || 0
  const currentLevel = userInfo.value.memberLevel || 0
  const config = MEMBER_LEVEL_CONFIG[currentLevel]
  
  if (!config || config.nextPoints === 0) return 0
  
  return Math.max(config.nextPoints - currentPoints, 0)
})

// 获取会员等级文字
const getMemberLevelText = (level: number) => {
  return MEMBER_LEVEL_CONFIG[level]?.name || '普通会员'
}

// 获取下一等级文字
const getNextLevelText = (level: number) => {
  const nextLevel = level + 1
  return MEMBER_LEVEL_CONFIG[nextLevel]?.name || '最高等级'
}

// 统计数据（初始值设为 0，从后端加载）
const stats = ref({
  totalOrders: 0,
  completedOrders: 0,
  ongoingOrders: 0,
  averageRating: 0
})

const favoritesCount = ref(0)
const couponsCount = ref(0)

// 预约订单
const activeTab = ref('ongoing')
const ongoingOrders = ref([
  {
    id: 1,
    chefName: '王师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '川菜定制',
    status: 'confirmed',
    serviceDate: '2026-03-08',
    serviceTime: '18:00-20:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    guestCount: 4,
    totalAmount: 598
  },
  {
    id: 2,
    chefName: '李师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '粤菜海鲜',
    status: 'pending',
    serviceDate: '2026-03-10',
    serviceTime: '12:00-14:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    guestCount: 6,
    totalAmount: 888
  }
])

const historyOrders = ref([
  {
    id: 3,
    chefName: '张师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '家常宴席',
    status: 'completed',
    serviceDate: '2026-02-28',
    serviceTime: '18:00-20:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    totalAmount: 468
  },
  {
    id: 4,
    chefName: '刘师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '火锅烧烤',
    status: 'completed',
    serviceDate: '2026-02-14',
    serviceTime: '17:00-19:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    totalAmount: 398
  }
])

// 格式化手机号
const formatPhone = (phone: string) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 状态类型映射
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'pending': 'warning',
    'confirmed': 'success',
    'cooking': 'primary',
    'completed': 'info',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    'pending': '待确认',
    'confirmed': '已确认',
    'cooking': '烹饪中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

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

// 地址操作
const selectAddress = (addr: any) => {
  // TODO: 选择地址后通知父组件或更新用户默认地址
  console.log('选择地址:', addr)
}

const editAddress = (addr: any) => {
  newAddress.value = { ...addr }
  showAddAddressForm.value = true
}

const deleteAddress = (index: number) => {
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

// 订单操作
const cancelOrder = (orderId :any) => {
  // TODO: 调用后端 API 取消订单
  console.log('取消订单:', orderId)
  ElMessage.success('订单已取消')
}

const contactChef = (order:any) => {
  // TODO: 实现联系厨师功能（拨打电话或在线聊天）
  ElMessage.info(`正在联系${order.chefName}...`)
}

const reviewOrder = (order:any) => {
  // TODO: 打开评价对话框并提交到后端
  console.log('评价订单:', order)
  ElMessage.success('评价功能开发中')
}

const bookAgain = (order:any) => {
  // TODO: 跳转到预约页面并填充订单信息
  console.log('再次预约:', order)
  ElMessage.info('再次预约功能开发中')
}

// 加载用户信息和统计数据
const loadUserInfo = async () => {
  try {
    // 加载个人信息（包含订单统计）
    const profileRes = await getCustomerProfile()
    if (profileRes.data) {
      userInfo.value = {
        id: profileRes.data.userId,
        name: profileRes.data.username ? profileRes.data.username : '未知用户',
        phone: profileRes.data.phone || '',
        avatar: profileRes.data.avatar || '',
        points: profileRes.data.points || 0,
        memberLevel: profileRes.data.memberLevel || 0
      }
      
      // 更新统计数据
      stats.value = {
        totalOrders: profileRes.data.totalOrders || 0,
        completedOrders: profileRes.data.completedOrders || 0,
        ongoingOrders: (profileRes.data.totalOrders || 0) - (profileRes.data.completedOrders || 0),
        averageRating: profileRes.data.averageRating || 0
      }
    }
    
    // 加载收藏和优惠券数量
    const statsRes = await getPersonalCenterStats()
    if (statsRes) {
      favoritesCount.value = statsRes.favoritesCount || 0
      couponsCount.value = statsRes.couponsCount || 0
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
    ElMessage.error('加载信息失败，请刷新重试')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
/* 科技感配色方案 */
.personal-center {
  --tech-bg: linear-gradient(135deg, #0c0c1e 0%, #1a1a3e 50%, #0f0f2d 100%);
  --neon-blue: #00f3ff;
  --neon-purple: #bc13fe;
  --neon-green: #0aff0a;
  --neon-pink: #ff00ff;
  --tech-gray: #2a2a4a;
  --tech-border: rgba(0, 243, 255, 0.3);
  --glass-bg: rgba(20, 20, 50, 0.6);
  --glass-border: rgba(0, 243, 255, 0.2);

  position: relative;
  min-height: 600px;
  padding: 20px;
  background: var(--tech-bg);
  overflow: hidden;
  color: white; /* 确保文字颜色为白色 */
}

/* 科技感背景动画 */
.tech-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: -1; /* 修改为 -1，确保在内容下方 */
}

.grid-line {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(0, 243, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 243, 255, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% { transform: translateY(0); }
  100% { transform: translateY(50px); }
}

.floating-particles {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(0, 243, 255, 0.3) 1px, transparent 1px);
  background-size: 100px 100px;
  animation: particleFloat 30s ease-in-out infinite;
}

@keyframes particleFloat {
  0%, 100% { opacity: 0.3; transform: translateY(0); }
  50% { opacity: 0.6; transform: translateY(-20px); }
}

/* 滚动渐入动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(60px);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

.scroll-animate.animate-active {
  opacity: 1;
  transform: translateY(0);
}

/* 全息投影风格用户卡片 */
.user-card.holographic {
  position: relative;
  background: rgba(30, 30, 60, 0.85); /* 增加不透明度，提高对比度 */
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  border: 1px solid var(--glass-border);
  box-shadow: 
    0 0 40px rgba(0, 243, 255, 0.1),
    inset 0 0 40px rgba(0, 243, 255, 0.05);
  overflow: visible;
}

.card-border-glow {
  position: absolute;
  inset: -2px;
  background: linear-gradient(45deg, 
    var(--neon-blue), 
    var(--neon-purple), 
    var(--neon-blue));
  background-size: 200% 200%;
  border-radius: 20px;
  z-index: -1;
  opacity: 0.5;
  animation: borderGlow 3s ease-in-out infinite;
  filter: blur(10px);
}

@keyframes borderGlow {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.card-corner {
  position: absolute;
  width: 30px;
  height: 30px;
  border: 2px solid var(--neon-blue);
  transition: all 0.3s ease;
}

.card-corner.tl { top: -2px; left: -2px; border-right: none; border-bottom: none; }
.card-corner.tr { top: -2px; right: -2px; border-left: none; border-bottom: none; }
.card-corner.bl { bottom: -2px; left: -2px; border-right: none; border-top: none; }
.card-corner.br { bottom: -2px; right: -2px; border-left: none; border-top: none; }

.user-card:hover .card-corner {
  width: 40px;
  height: 40px;
  box-shadow: 0 0 20px var(--neon-blue);
}

.user-card-content {
  display: flex;
  align-items: center;
  gap: 40px;
  position: relative;
  z-index: 1;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-ring, .avatar-ring-2 {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  border: 2px solid var(--neon-blue);
  animation: avatarRingRotate 4s linear infinite;
}

.avatar-ring {
  width: 120px;
  height: 120px;
  border-top-color: transparent;
}

.avatar-ring-2 {
  width: 140px;
  height: 140px;
  border-bottom-color: transparent;
  animation-direction: reverse;
  animation-duration: 6s;
}

@keyframes avatarRingRotate {
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

.user-avatar {
  border: 3px solid var(--neon-blue);
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.4);
  position: relative;
  z-index: 2;
}

.level-badge {
  position: absolute;
  bottom: -10px;
  right: -10px;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  z-index: 3;
  animation: badgePulse 3s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 普通会员徽章 (level-0) - 灰色风格 */
.level-badge.level-0 {
  background: linear-gradient(135deg, #6b7280, #9ca3af);
  box-shadow: 0 4px 15px rgba(107, 114, 128, 0.5);
  border: 2px solid #4b5563;
  color: #f3f4f6 !important;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5), 0 0 10px rgba(255, 255, 255, 0.3);
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 白银会员徽章 (level-1) */
.level-badge.level-1 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  box-shadow: 0 4px 15px rgba(192, 192, 192, 0.5);
  border: 2px solid #ffffff;
  color: #2d3748 !important;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.8), 0 0 15px rgba(192, 192, 192, 0.5);
  font-weight: 700;
  letter-spacing: 0.8px;
}

/* 黄金会员徽章 (level-2) */
.level-badge.level-2 {
  background: linear-gradient(135deg, #ffd700, #ffec8b);
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.6);
  border: 2px solid #ffffff;
  color: #744210 !important;
  text-shadow: 0 2px 6px rgba(255, 255, 255, 0.9), 0 0 20px rgba(255, 215, 0, 0.6);
  font-weight: 700;
  letter-spacing: 1px;
}

/* 铂金会员徽章 (level-3) */
.level-badge.level-3 {
  background: linear-gradient(135deg, #e5e4e2, #ffffff);
  box-shadow: 0 4px 15px rgba(229, 228, 226, 0.6);
  border: 2px solid #e5e4e2;
  animation: platinumShine 2s ease-in-out infinite;
  color: #4a5568 !important;
  text-shadow: 0 2px 8px rgba(255, 255, 255, 1), 0 0 25px rgba(229, 228, 226, 0.8);
  font-weight: 700;
  letter-spacing: 1px;
}

@keyframes platinumShine {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(229, 228, 226, 0.6), 0 0 20px rgba(255, 255, 255, 0.3);
  }
  50% { 
    box-shadow: 0 4px 25px rgba(229, 228, 226, 0.8), 0 0 30px rgba(255, 255, 255, 0.6);
  }
}

/* 钻石会员徽章 (level-4) */
.level-badge.level-4 {
  background: linear-gradient(135deg, #b9f2ff, #00ffff);
  box-shadow: 0 4px 15px rgba(0, 255, 255, 0.6);
  border: 2px solid #ffffff;
  animation: diamondSparkle 2s ease-in-out infinite;
  color: #034078 !important;
  text-shadow: 0 2px 6px rgba(255, 255, 255, 0.9), 0 0 30px rgba(0, 255, 255, 0.8);
  font-weight: 700;
  letter-spacing: 1.2px;
}

@keyframes diamondSparkle {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(0, 255, 255, 0.6), 0 0 20px rgba(185, 242, 255, 0.4);
  }
  50% { 
    box-shadow: 0 4px 25px rgba(0, 255, 255, 0.9), 0 0 35px rgba(185, 242, 255, 0.8);
  }
}

.user-details {
  flex: 1;
}

.username {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.name-text {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, var(--neon-blue), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(0, 243, 255, 0.3);
}

.member-tag {
  animation: tagPulse 2s ease-in-out infinite;
  border-radius: 12px;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.member-tag:hover {
  transform: scale(1.1) rotate(-3deg);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.5);
}

@keyframes tagPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 普通会员 (level-0) - 灰色风格 */
.member-tag.member-level-0 {
  background: linear-gradient(135deg, #6b7280, #9ca3af, #4b5563);
  color: #f3f4f6 !important;
  border-color: #4b5563;
  box-shadow: 0 4px 15px rgba(107, 114, 128, 0.5), inset 0 0 10px rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.member-tag.member-level-0::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 70%
  );
  animation: bronzeShine 3s ease-in-out infinite;
}

@keyframes bronzeShine {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.member-tag.member-level-0 .el-tag__content {
  position: relative;
  z-index: 1;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5), 0 0 10px rgba(255, 255, 255, 0.3);
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 白银会员 (level-1) - 银色风格 */
.member-tag.member-level-1 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8, #a8a8a8);
  color: #2d3748 !important;
  border-color: #c0c0c0;
  box-shadow: 0 4px 15px rgba(192, 192, 192, 0.6), inset 0 0 10px rgba(255, 255, 255, 0.3);
  animation: silverGlow 2s ease-in-out infinite;
  position: relative;
}

.member-tag.member-level-1 .el-tag__content {
  position: relative;
  z-index: 1;
  background: linear-gradient(180deg, #1a202c, #4a5568);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.8), 0 0 15px rgba(192, 192, 192, 0.5);
  font-weight: 700;
  letter-spacing: 0.8px;
}

@keyframes silverGlow {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(192, 192, 192, 0.6), inset 0 0 10px rgba(255, 255, 255, 0.3);
  }
  50% { 
    box-shadow: 0 6px 20px rgba(192, 192, 192, 0.8), inset 0 0 15px rgba(255, 255, 255, 0.5);
  }
}

/* 黄金会员 (level-2) - 金色风格 */
.member-tag.member-level-2 {
  background: linear-gradient(135deg, #ffd700, #ffec8b, #daa520);
  color: #744210 !important;
  border-color: #ffd700;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.6), inset 0 0 10px rgba(255, 255, 255, 0.4);
  animation: goldSparkle 1.5s ease-in-out infinite;
  position: relative;
}

.member-tag.member-level-2 .el-tag__content {
  position: relative;
  z-index: 1;
  background: linear-gradient(180deg, #744210, #b7791f);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 6px rgba(255, 255, 255, 0.9), 0 0 20px rgba(255, 215, 0, 0.6);
  font-weight: 700;
  letter-spacing: 1px;
}

@keyframes goldSparkle {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(255, 215, 0, 0.6), inset 0 0 10px rgba(255, 255, 255, 0.4);
    filter: brightness(1);
  }
  50% { 
    box-shadow: 0 6px 25px rgba(255, 215, 0, 0.9), inset 0 0 20px rgba(255, 255, 255, 0.6);
    filter: brightness(1.2);
  }
}

/* 铂金会员 (level-3) - 铂金色风格 */
.member-tag.member-level-3 {
  background: linear-gradient(135deg, #e5e4e2, #ffffff, #cccccc);
  color: #4a5568 !important;
  border-color: #e5e4e2;
  box-shadow: 0 4px 15px rgba(229, 228, 226, 0.7), 0 0 20px rgba(255, 255, 255, 0.4);
  animation: platinumShine 2s ease-in-out infinite;
  position: relative;
}

.member-tag.member-level-3 .el-tag__content {
  position: relative;
  z-index: 1;
  background: linear-gradient(180deg, #2d3748, #718096);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 8px rgba(255, 255, 255, 1), 0 0 25px rgba(229, 228, 226, 0.8);
  font-weight: 700;
  letter-spacing: 1px;
}

@keyframes platinumShine {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(229, 228, 226, 0.7), 0 0 20px rgba(255, 255, 255, 0.4);
  }
  50% { 
    box-shadow: 0 6px 25px rgba(229, 228, 226, 0.9), 0 0 35px rgba(255, 255, 255, 0.7);
  }
}

/* 钻石会员 (level-4) - 钻石蓝风格 */
.member-tag.member-level-4 {
  background: linear-gradient(135deg, #b9f2ff, #00ffff, #00ced1);
  color: #034078 !important;
  border-color: #00ffff;
  box-shadow: 0 4px 15px rgba(0, 255, 255, 0.7), 0 0 25px rgba(0, 255, 255, 0.5);
  animation: diamondSparkle 1.5s ease-in-out infinite;
  position: relative;
  overflow: hidden;
}

.member-tag.member-level-4 .el-tag__content {
  position: relative;
  z-index: 1;
  background: linear-gradient(180deg, #034078, #1282a2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 6px rgba(255, 255, 255, 0.9), 0 0 30px rgba(0, 255, 255, 0.8);
  font-weight: 700;
  letter-spacing: 1.2px;
}

.member-tag.member-level-4::after {
  content: '♦';
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%) rotate(15deg);
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.8);
  animation: diamondRotate 3s ease-in-out infinite;
}

@keyframes diamondRotate {
  0%, 100% { transform: translateY(-50%) rotate(15deg) scale(1); opacity: 0.8; }
  50% { transform: translateY(-50%) rotate(-15deg) scale(1.2); opacity: 1; }
}

@keyframes diamondSparkle {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(0, 255, 255, 0.7), 0 0 25px rgba(0, 255, 255, 0.5);
  }
  50% { 
    box-shadow: 0 6px 25px rgba(0, 255, 255, 0.9), 0 0 40px rgba(0, 255, 255, 0.8);
  }
}

/* VIP 标识样式 */
.vip-tag {
  background: linear-gradient(135deg, #2d3748, #4a5568) !important;
  border-color: #4a5568 !important;
  box-shadow: 0 4px 15px rgba(74, 85, 114, 0.6), 0 0 20px rgba(74, 85, 114, 0.4);
  animation: vipPulse 2s ease-in-out infinite;
  font-weight: 700;
  letter-spacing: 1px;
}

.vip-tag .el-tag__content {
  background: linear-gradient(180deg, #e2e8f0, #cbd5e0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5), 0 0 15px rgba(255, 255, 255, 0.6);
  font-weight: 800;
  letter-spacing: 1.5px;
}

@keyframes vipPulse {
  0%, 100% { 
    box-shadow: 0 4px 15px rgba(74, 85, 114, 0.6), 0 0 20px rgba(74, 85, 114, 0.4);
    transform: scale(1);
  }
  50% { 
    box-shadow: 0 6px 25px rgba(74, 85, 114, 0.9), 0 0 30px rgba(74, 85, 114, 0.7);
    transform: scale(1.05);
  }
}

.user-phone {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 20px;
}

.icon-glitch {
  color: var(--neon-blue);
  animation: iconGlitch 3s ease-in-out infinite;
}

@keyframes iconGlitch {
  0%, 100% { opacity: 1; transform: translateX(0); }
  50% { opacity: 0.8; transform: translateX(2px); }
}

.points-display {
  display: flex;
  align-items: center;
  gap: 30px;
}

.points-circle {
  position: relative;
  width: 100px;
  height: 100px;
}

.progress-svg {
  width: 100px;
  height: 100px;
  transform: rotate(-90deg);
}

.progress-bg {
  fill: none;
  stroke: rgba(0, 243, 255, 0.15);
  stroke-width: 8;
}

.progress-bar {
  fill: none;
  stroke: url(#gradient);
  stroke-width: 8;
  stroke-linecap: round;
  transition: stroke-dashoffset 1s ease;
  filter: drop-shadow(0 0 6px rgba(0, 243, 255, 0.8));
}

.points-value {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 24px;
  font-weight: bold;
  color: var(--neon-blue);
}

.points-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.points-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.level-tip {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.level-tip.max-level {
  color: rgba(255, 215, 0, 0.8);
  font-weight: bold;
  animation: maxLevelGlow 2s ease-in-out infinite;
}

@keyframes maxLevelGlow {
  0%, 100% { 
    text-shadow: 0 0 5px rgba(255, 215, 0, 0.3);
  }
  50% { 
    text-shadow: 0 0 15px rgba(255, 215, 0, 0.8);
  }
}

/* 仪表盘风格统计 */
.stats-dashboard {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  position: relative;
  background: rgba(30, 30, 60, 0.85); /* 增加不透明度 */
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 25px;
  text-align: center;
  border: 1px solid var(--glass-border);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(0, 243, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.stat-item:hover::before {
  opacity: 1;
}

.stat-item:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 243, 255, 0.3);
  border-color: var(--neon-blue);
}

.stat-hexagon {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 15px;
}

.hex-svg {
  width: 100%;
  height: 100%;
}

.hex-bg {
  fill: rgba(0, 243, 255, 0.05);
  stroke: var(--neon-blue);
  stroke-width: 2;
  transition: all 0.4s ease;
}

.hex-border {
  fill: none;
  stroke: var(--neon-blue);
  stroke-width: 3;
  stroke-dasharray: 300;
  stroke-dashoffset: 300;
  transition: stroke-dashoffset 0.6s ease;
}

.stat-item:hover .hex-border {
  stroke-dashoffset: 0;
}

.stat-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32px;
  color: var(--neon-blue);
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(135deg, var(--neon-blue), var(--neon-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 12px;
}

.stat-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
}

.trend-up {
  color: var(--neon-green);
}

.trend-current {
  color: var(--neon-blue);
}

/* 能量方块功能卡 */
.functions-section {
  background: rgba(30, 30, 60, 0.8); /* 增加不透明度 */
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  border: 1px solid var(--glass-border);
}

.section-header-tech {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
}

.header-line {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-blue), transparent);
  position: relative;
}

.header-line::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, var(--neon-purple), transparent);
  animation: lineFlow 3s ease-in-out infinite;
}

@keyframes lineFlow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: bold;
  color: var(--neon-blue);
  text-shadow: 0 0 20px rgba(0, 243, 255, 0.5);
}

.title-icon {
  animation: iconSpin 10s linear infinite;
}

@keyframes iconSpin {
  to { transform: rotate(360deg); }
}

.function-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
}

.function-card {
  position: relative;
  background: rgba(30, 30, 60, 0.85); /* 增加不透明度 */
  border-radius: 16px;
  padding: 25px 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.card-energy {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at center, rgba(0, 243, 255, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.function-card:hover .card-energy {
  opacity: 1;
}

.func-icon-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-bg-circle {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(0, 243, 255, 0.2), rgba(188, 19, 254, 0.2));
  animation: circlePulse 2s ease-in-out infinite;
}

@keyframes circlePulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.func-icon {
  font-size: 28px;
  color: white;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 0 10px var(--neon-blue));
}

.func-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  text-align: center;
  font-weight: 500;
}

.func-badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.card-hover-effect {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, var(--neon-blue), var(--neon-purple));
  transform: scaleX(0);
  transition: transform 0.4s ease;
}

.function-card:hover {
  transform: translateY(-8px);
  border-color: var(--neon-blue);
  box-shadow: 0 10px 30px rgba(0, 243, 255, 0.3);
}

.function-card:hover .card-hover-effect {
  transform: scaleX(1);
}

/* 数据流风格预约记录 */
.booking-section {
  background: rgba(30, 30, 60, 0.85); /* 增加不透明度 */
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  border: 1px solid var(--glass-border);
}

.tech-tabs :deep(.el-tabs__header) {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
  padding: 5px;
  margin-bottom: 25px;
}

.tech-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.6);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.tech-tabs :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, rgba(0, 243, 255, 0.2), rgba(188, 19, 254, 0.2));
  color: var(--neon-blue);
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-card.data-stream {
  position: relative;
  background: rgba(30, 30, 60, 0.4);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(0, 243, 255, 0.1);
  transition: all 0.4s ease;
  cursor: pointer;
  overflow: hidden;
}

.data-stream-line {
  position: absolute;
  top: 0;
  left: 20px;
  width: 2px;
  height: 100%;
  background: linear-gradient(to bottom, var(--neon-blue), transparent);
  opacity: 0.3;
}

.booking-card:hover {
  transform: translateX(10px);
  border-color: var(--neon-blue);
  box-shadow: 0 10px 30px rgba(0, 243, 255, 0.2);
}

.booking-card:hover .data-stream-line {
  opacity: 1;
  animation: dataFlow 1s ease-in-out infinite;
}

@keyframes dataFlow {
  0%, 100% { transform: scaleY(0); transform-origin: top; }
  50% { transform: scaleY(1); transform-origin: top; }
  51% { transform: scaleY(1); transform-origin: bottom; }
  100% { transform: scaleY(0); transform-origin: bottom; }
}

.booking-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chef-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chef-avatar-wrapper {
  position: relative;
}

.avatar-status-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: var(--neon-green);
  border-radius: 50%;
  border: 2px solid var(--tech-gray);
  animation: dotPulse 2s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
}

.chef-details h4 {
  font-size: 18px;
  font-weight: bold;
  color: white;
  margin: 0 0 5px 0;
}

.chef-details p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.status-tag {
  border-color: var(--neon-blue);
}

.booking-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
  padding: 15px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  border-left: 3px solid var(--neon-blue);
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.info-row .el-icon {
  color: var(--neon-blue);
  font-size: 18px;
}

.price-highlight {
  color: var(--neon-green);
  font-weight: bold;
  font-size: 16px;
}

.booking-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-dashboard {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .function-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .user-card-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stats-dashboard {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .function-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .points-display {
    justify-content: center;
  }
}
</style>
