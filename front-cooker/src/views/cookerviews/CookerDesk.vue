<template>
  <div class="page">
    <el-card class="hero-card" shadow="never">
      <div class="hero-head">
        <div>
          <h2>厨师端</h2>
          <p>厨师ID：{{ chefId || '-' }} · 今日数据实时更新</p>
        </div>
        <div class="status-box">
          <span>接单状态</span>
          <el-switch v-model="online" @change="handleStatusToggle" />
          <el-tag :type="online ? 'success' : 'info'" effect="dark">{{ online ? '在线接单' : '休息中' }}</el-tag>
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
      </el-card>
    </section>

    <!-- 快捷入口区域 -->
    <section class="quick-entry-section">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>快捷入口</span>
            <el-tag v-if="settings.autoRecommend" type="success" size="small">已启用智能推荐</el-tag>
          </div>
        </template>

        <div class="quick-actions">
          <el-button
            v-for="action in quickActions"
            :key="action.path"
            :type="action.recommended ? 'primary' : 'default'"
            @click="$router.push(action.path)"
          >
            {{ action.label }}
          </el-button>
        </div>

        <el-divider />

        <div class="status-log">
          <div class="log-header" @click="toggleStatusLog">
            <div class="log-title">
              <el-icon :class="{ 'collapsed': !statusLogExpanded }"><ArrowUp /></el-icon>
              <span>最近状态记录</span>
            </div>
            <el-tag size="small" type="info">{{ statusReasonLog.length }} 条</el-tag>
          </div>
          <template v-if="statusReasonLog.length === 0">
            <el-empty description="暂无切换记录" :image-size="80" />
          </template>
          <template v-else>
            <el-collapse-transition>
              <ul v-show="statusLogExpanded">
                <li v-for="(item, index) in statusReasonLog.slice(0, 10)" :key="`${item.at}-${index}`">
                  {{ formatDateTime(item.at) }} · {{ item.statusText }} · {{ item.reason || '未填写原因' }}
                </li>
              </ul>
            </el-collapse-transition>
            <div class="log-expand-btn" @click="toggleStatusLog">
              {{ statusLogExpanded ? '收起' : '展开更多' }}
              <el-icon><ArrowUp /></el-icon>
            </div>
          </template>
        </div>
      </el-card>
    </section>

    <section class="charts-grid">
      <el-card class="trend-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>接单与收入趋势</span>
            <div class="ops">
              <el-segmented v-model="trendDays" :options="trendOptions" size="small" />
              <el-radio-group v-model="trendValueKey" size="small">
                <el-radio-button label="orderCount">接单量</el-radio-button>
                <el-radio-button label="income">收入</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </template>

        <el-skeleton :rows="7" animated :loading="loading">
          <template #default>
            <TrendLineChart v-if="trendData.length" :data="trendData" :value-key="trendValueKey" />
            <el-empty v-else description="暂无趋势数据" />
          </template>
        </el-skeleton>
      </el-card>

      <el-card shadow="hover">
        <template #header>订单类型占比</template>
        <el-skeleton :rows="5" animated :loading="loading">
          <template #default>
            <DonutChart v-if="orderTypeShare.length" :data="orderTypeShare" />
            <el-empty v-else description="暂无类型数据" />
          </template>
        </el-skeleton>
      </el-card>

      <el-card shadow="hover">
        <template #header>客户评价分布</template>
        <el-skeleton :rows="5" animated :loading="loading">
          <template #default>
            <RatingDistribution v-if="ratingResult.distribution.length" :data="ratingResult.distribution" :good-rate="ratingResult.goodRate" />
            <el-empty v-else description="暂无评价数据" />
          </template>
        </el-skeleton>
      </el-card>

      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>实时提醒</span>
            <el-link type="primary" :underline="false" @click="$router.push('/cooker/message')">查看消息</el-link>
          </div>
        </template>
        <div class="notice-list">
          <div class="notice-item">
            <span>待服务订单</span>
            <el-badge :value="waitOrderCount" :max="99" />
          </div>
          <div class="notice-item">
            <span>待接单订单</span>
            <el-badge :value="newOrderCount" :max="99" type="warning" />
          </div>
          <div class="notice-item">
            <span>未读消息</span>
            <el-badge :value="unreadCount" :max="99" type="danger" />
          </div>
        </div>
      </el-card>
    </section>

    <section class="timeline-grid">
      <el-card shadow="hover">
        <template #header>今日日程时间轴</template>
        <el-empty v-if="todaySchedule.length === 0" description="今日暂未安排服务" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in todaySchedule"
            :key="item.id"
            :timestamp="item.time"
            placement="top"
          >
            <div class="timeline-item">
              <strong>{{ item.orderType }} · {{ item.customerName }}</strong>
              <p>{{ item.address }}</p>
              <p>联系方式：{{ item.customerPhone }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </section>

    <el-dialog v-model="statusDialogVisible" title="确认接单状态" width="420px" @close="cancelStatusChange">
      <el-form label-width="78px">
        <el-form-item label="目标状态">
          <el-tag :type="pendingStatus ? 'success' : 'info'">{{ pendingStatus ? '在线接单' : '休息中' }}</el-tag>
        </el-form-item>
        <el-form-item label="切换原因" required>
          <el-select v-model="statusReason" placeholder="请选择原因" style="width: 100%">
            <el-option v-for="item in statusReasonOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="statusRemark" type="textarea" :rows="2" maxlength="120" show-word-limit placeholder="可选填写" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelStatusChange">取消</el-button>
        <el-button type="primary" :loading="statusSaving" @click="confirmStatusChange">确认切换</el-button>
      </template>
    </el-dialog>
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
.page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 20px 20px;
  display: grid;
  gap: 14px;
}

.hero-card {
  border-radius: 14px;
  background: linear-gradient(125deg, #1d4ed8 0%, #0369a1 52%, #0e7490 100%);
  border: none;
}

.hero-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  gap: 12px;
}

.hero-head h2 {
  font-size: 24px;
  font-weight: 700;
}

.hero-head p {
  margin-top: 6px;
  opacity: 0.9;
}

.status-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.quick-entry-section {
  margin-top: 4px;
}

.metric-card {
  display: grid;
  gap: 8px;
}

.metric-card .label {
  color: #6a7585;
  font-size: 13px;
}

.metric-card strong {
  font-size: 28px;
  color: #1f2c3d;
}

.metric-card .compare {
  font-size: 12px;
  font-weight: 600;
}

.metric-card .compare.up {
  color: #12a150;
}

.metric-card .compare.down {
  color: #d64545;
}

.charts-grid {
  display: grid;
  gap: 12px;
  grid-template-columns: 2fr 1fr;
}

.trend-card {
  grid-row: span 2;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.ops {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-list {
  display: grid;
  gap: 12px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f3f6fb;
}

.timeline-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 12px;
}

.timeline-item {
  display: grid;
  gap: 4px;
}

.timeline-item p {
  color: #5f6878;
  font-size: 13px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-log .log-title {
  font-weight: 600;
  color: #344052;
  margin-bottom: 8px;
}

.status-log .log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 4px 0;
  user-select: none;
}

.status-log .log-header:hover {
  opacity: 0.8;
}

.status-log .log-title {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-log .log-title .el-icon {
  transition: transform 0.3s;
}

.status-log .log-title .el-icon.collapsed {
  transform: rotate(-180deg);
}

.status-log ul {
  list-style: none;
  display: grid;
  gap: 8px;
  padding: 8px 0 0 0;
  margin: 0;
}

.status-log li {
  font-size: 12px;
  color: #5f6878;
  padding: 8px 10px;
  border-radius: 8px;
  background: #f5f7fb;
}

.status-log .log-expand-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 8px;
  padding: 6px 0;
  font-size: 12px;
  color: #1989fa;
  cursor: pointer;
  transition: opacity 0.2s;
}

.status-log .log-expand-btn:hover {
  opacity: 0.8;
}

.status-log .log-expand-btn .el-icon {
  transition: transform 0.3s;
}

.status-log .log-expand-btn:hover .el-icon {
  transform: rotate(180deg);
}

@media (max-width: 1100px) {
  .cards-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid,
  .timeline-grid {
    grid-template-columns: 1fr;
  }

  .trend-card {
    grid-row: auto;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }

  .hero-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .status-box {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
