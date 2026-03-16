<template>
  <div class="chefInfo-dashboard">
    <!-- 顶部：欢迎语 + 在线状态 -->
    <header class="header">
      <div class="welcome-section">
        <div class="avatar">
          <el-icon :size="32"><UserFilled /></el-icon>
        </div>
        <div class="welcome">
          <h2>欢迎您，{{ chefName }}</h2>
          <p class="date">{{ currentDate }}</p>
        </div>
      </div>
      <div class="status-switch">
        <span class="status-label">在线状态</span>
        <el-switch
          v-model="isOnline"
          active-color="#52c41a"
          inactive-color="#d9d9d9"
          @change="handleStatusChange"
          size="large"
        />
        <span class="status-text" :class="{ online: isOnline }">
          {{ isOnline ? '接单中' : '休息中' }}
        </span>
      </div>
    </header>

    <!-- 数据统计卡片 -->
    <section class="stats">
      <div class="stats-card" v-for="(item, index) in statsData" :key="index">
        <div class="stats-icon" :style="{ background: item.color }">
          {{ item.icon }}
        </div>
        <div class="stats-content">
          <div class="num" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="label">{{ item.label }}</div>
        </div>
      </div>
    </section>

    <!-- 核心功能区：常用功能 + 收入统计 -->
    <section class="core-container">
      <!-- 常用功能卡片 -->
      <el-card class="core-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Menu /></el-icon>
              常用功能
            </span>
          </div>
        </template>
        <div class="func-grid">
          <div 
            class="func-item" 
            v-for="(item, index) in funcList" 
            :key="index"
            @click="handleFuncClick(item)"
          >
            <div class="func-icon" :style="{ background: item.bgColor, color: item.color }">
              {{ item.icon }}
            </div>
            <div class="func-text">{{ item.name }}</div>
          </div>
        </div>
      </el-card>

      <!-- 收入统计卡片 -->
      <el-card class="core-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">
              <el-icon><Money /></el-icon>
              收入统计
            </span>
          </div>
        </template>
        <!-- 收入趋势图 -->
        <div class="income-chart" ref="chartRef">
          <div class="chart-placeholder">
            <el-icon :size="48" color="#1890ff"><TrendCharts /></el-icon>
            <p>月度收入趋势图</p>
            <span>可接入 ECharts 展示详细数据</span>
          </div>
        </div>
        <!-- 收入明细 -->
        <div class="income-detail">
          <div class="detail-item" v-for="(item, index) in incomeData" :key="index">
            <div class="detail-icon">
              <el-icon :size="20"><component :is="item.icon" /></el-icon>
            </div>
            <div class="detail-content">
              <div class="detail-num" :style="{ color: item.color }">{{ item.value }}</div>
              <div class="detail-label">{{ item.label }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  UserFilled, 
  Menu, 
  Money, 
  TrendCharts,
  User,
  Food,
  Wallet,
  Bell,
  Setting,
  Service,
  Document,
  DataAnalysis
} from '@element-plus/icons-vue'

// 响应式数据
const chefName = ref('王师傅')
const isOnline = ref(true)
const chartRef = ref(null)

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const week = weekDays[now.getDay()]
  return `${year}-${month}-${day} ${week}`
})

// 数据统计
const statsData = ref([
  { value: 3, label: '今日已接订单', icon: '📋', color: '#409eff' },
  { value: 2, label: '待服务订单', icon: '⏰', color: '#e6a23c' },
  { value: '¥860', label: '今日收入', icon: '💰', color: '#67c23a' }
])

// 常用功能列表
const funcList = ref([
  { icon: '👤', name: '个人资料', action: 'profile', bgColor: '#ecf5ff', color: '#409eff' },
  { icon: '🍳', name: '我的菜单', action: 'menu', bgColor: '#fdf6ec', color: '#e6a23c' },
  { icon: '💰', name: '收入明细', action: 'income', bgColor: '#f0f9eb', color: '#67c23a' },
  { icon: '📩', name: '消息通知', action: 'notice', bgColor: '#fef0f0', color: '#f56c6c' },
  { icon: '⚙️', name: '系统设置', action: 'setting', bgColor: '#ecf5ff', color: '#909399' },
  { icon: '📞', name: '联系客服', action: 'service', bgColor: '#f0f9eb', color: '#67c23a' }
])

// 收入统计数据
const incomeData = ref([
  { value: '¥2580', label: '本月总收入', icon: 'Wallet', color: '#67c23a' },
  { value: 12, label: '本月订单数', icon: 'Document', color: '#409eff' },
  { value: '¥215', label: '平均单价', icon: 'DataAnalysis', color: '#e6a23c' }
])

// 在线状态切换处理
const handleStatusChange = (val) => {
  const status = val ? '在线接单' : '休息中'
  ElMessage.success(`已切换为${status}状态`)
}

// 常用功能点击处理
const handleFuncClick = (item) => {
  switch (item.action) {
    case 'profile':
      ElMessage.info('进入个人资料页面')
      break
    case 'menu':
      ElMessage.info('进入我的菜单页面')
      break
    case 'income':
      ElMessage.info('进入收入明细页面')
      break
    case 'notice':
      ElMessage.info('进入消息通知页面')
      break
    case 'setting':
      ElMessage.info('进入系统设置页面')
      break
    case 'service':
      ElMessage.info('联系客服：400-123-4567')
      break
    default:
      ElMessage.info(`点击了${item.name}`)
  }
}

onMounted(() => {
  // 初始化逻辑
})
</script>

<style scoped>
.chefInfo-dashboard {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background-color: var(--color-bg-white);
  min-height: calc(100vh - 60px);
}

/* 顶部样式 */
.header {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-bg-soft) 100%);
  padding: 24px 32px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  color: var(--color-text-primary);
}

.welcome-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: var(--color-text-primary);
}

.welcome .date {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.status-switch {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--color-bg-white);
  padding: 10px 20px;
  border-radius: 20px;
}

.status-label {
  font-size: 14px;
  color: var(--color-text-primary);
}

.status-text {
  font-size: 13px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
  background: var(--color-bg-mute);
  color: var(--color-text-secondary);
}

.status-text.online {
  background: var(--color-success);
  color: #fff;
}

/* 数据统计卡片 */
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stats-card {
  background: var(--color-bg-white);
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(74, 68, 62, 0.08);
  transition: all 0.3s ease;
  border: 1px solid var(--color-border);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(74, 68, 62, 0.12);
}

.stats-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.stats-content {
  flex: 1;
}

.stats-content .num {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stats-content .label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* 核心功能区 */
.core-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.core-card {
  --el-card-border-radius: 16px;
  --el-card-box-shadow: 0 4px 20px rgba(74, 68, 62, 0.06);
  --el-card-padding: 24px;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.core-card:hover {
  box-shadow: 0 8px 32px rgba(74, 68, 62, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.card-title .el-icon {
  color: var(--color-accent);
}

/* 常用功能样式 */
.func-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 8px;
}

.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: var(--color-bg-soft);
}

.func-item:hover {
  transform: translateY(-4px);
  background: var(--color-bg-white);
  box-shadow: 0 8px 24px rgba(74, 68, 62, 0.08);
}

.func-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.func-item:hover .func-icon {
  transform: scale(1.1);
}

.func-text {
  font-size: 13px;
  color: var(--color-text-primary);
  font-weight: 500;
}

/* 收入统计样式 */
.income-chart {
  height: 220px;
  background-color: var(--color-bg-soft);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 16px 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.chart-placeholder {
  text-align: center;
  color: var(--color-text-secondary);
}

.chart-placeholder p {
  font-size: 16px;
  font-weight: 500;
  margin: 12px 0 4px 0;
  color: var(--color-text-primary);
}

.chart-placeholder span {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.income-detail {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 8px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--color-bg-soft);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.detail-item:hover {
  background: var(--color-bg-white);
  box-shadow: 0 4px 16px rgba(74, 68, 62, 0.06);
}

.detail-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: var(--color-bg-white);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-accent);
}

.detail-content {
  flex: 1;
}

.detail-num {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 2px;
}

.detail-label {
  font-size: 12px;
  color: var(--color-text-secondary);
}

/* 响应式 */
@media (max-width: 768px) {
  .chefInfo-dashboard {
    padding: 16px;
  }

  .header {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }

  .welcome-section {
    width: 100%;
  }

  .stats {
    grid-template-columns: 1fr;
  }

  .core-container {
    grid-template-columns: 1fr;
  }

  .func-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .income-detail {
    grid-template-columns: 1fr;
  }
}
</style>
