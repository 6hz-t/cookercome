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
    </el-card>

    <section class="cards-grid">
      <el-card v-for="card in dashboardCards" :key="card.key" shadow="hover">
        <div class="metric-card">
          <span class="label">{{ card.label }}</span>
          <strong>{{ card.value }}</strong>
          <span class="compare" :class="card.compare.ratio >= 0 ? 'up' : 'down'">{{ card.compare.text }}</span>
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
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowUp } from '@element-plus/icons-vue'
import TrendLineChart from '@/components/charts/TrendLineChart.vue'
import DonutChart from '@/components/charts/DonutChart.vue'
import RatingDistribution from '@/components/charts/RatingDistribution.vue'
import { getChefOrderPool, getTodayOrders, updateChefStatus } from '@/api/cooker'
import {
  buildOrderTypeShare,
  buildRatingDistribution,
  buildTrend,
  compareWithYesterday,
  formatDateTime,
  money,
  normalizeOrders,
  todayTimeline
} from '@/utils/cookerOrder'
import { getCookerSettings } from '@/utils/cookerSettings'
import {
  appendStatusReason,
  getStatusReasonLog,
  getUnreadMessageCount,
  parseChefId
} from '@/utils/cookerSession'

const router = useRouter()

const loading = ref(true)
const chefId = ref(null)
const online = ref(localStorage.getItem('cooker_online_state') !== '0')
const unreadCount = ref(getUnreadMessageCount())

const newOrders = ref([])
const servingOrders = ref([])
const historyOrders = ref([])

const summary = ref({
  todayOrder: 0,
  waitOrder: 0,
  todayIncome: 0
})

const trendDays = ref(7)
const trendValueKey = ref('orderCount')
const trendOptions = [
  { label: '本周', value: 7 },
  { label: '本月', value: 30 }
]

const statusDialogVisible = ref(false)
const pendingStatus = ref(true)
const statusReason = ref('')
const statusRemark = ref('')
const statusSaving = ref(false)
const statusReasonLog = ref(getStatusReasonLog())
const statusLogExpanded = ref(false)

function toggleStatusLog() {
  statusLogExpanded.value = !statusLogExpanded.value
}

const statusReasonOptions = ['休息中', '忙碌中', '临时外出', '设备维护', '自定义安排']
const settings = getCookerSettings()

const allOrders = computed(() => normalizeOrders([...newOrders.value, ...servingOrders.value, ...historyOrders.value]))
const trendData = computed(() => buildTrend(allOrders.value, trendDays.value))
const orderTypeShare = computed(() => buildOrderTypeShare(allOrders.value))
const ratingResult = computed(() => buildRatingDistribution(allOrders.value))
const todaySchedule = computed(() => todayTimeline([...newOrders.value, ...servingOrders.value, ...historyOrders.value]))

const newOrderCount = computed(() => newOrders.value.length)
const waitOrderCount = computed(() => servingOrders.value.length)

const dashboardCards = computed(() => {
  const orderCompare = compareWithYesterday(summary.value.todayOrder)
  const waitCompare = compareWithYesterday(summary.value.waitOrder)
  const incomeCompare = compareWithYesterday(summary.value.todayIncome)

  return [
    {
      key: 'todayOrder',
      label: '总接单',
      value: `${summary.value.todayOrder} 单`,
      compare: orderCompare
    },
    {
      key: 'waitOrder',
      label: '待服务',
      value: `${summary.value.waitOrder} 单`,
      compare: waitCompare
    },
    {
      key: 'todayIncome',
      label: '总收入',
      value: `¥ ${money(summary.value.todayIncome)}`,
      compare: incomeCompare
    }
  ]
})

const quickActions = computed(() => {
  const base = [
    { label: '待接单', path: '/cooker/todo', recommendType: '家宴' },
    { label: '服务中订单', path: '/cooker/serving', recommendType: '团建' },
    { label: '我的菜单', path: '/cooker/menu', recommendType: '私厨' },
    { label: '收益明细', path: '/cooker/income', recommendType: '宴会' },
    { label: '系统设置', path: '/cooker/settings', recommendType: '家宴' }
  ]

  if (!settings.autoRecommend) {
    return base.map((item) => ({ ...item, recommended: false }))
  }

  return base.map((item) => ({
    ...item,
    recommended: settings.preferredOrderTypes.includes(item.recommendType)
  }))
})

async function loadDashboard() {
  loading.value = true
  try {
    const [todayRes, pool] = await Promise.all([getTodayOrders(chefId.value), getChefOrderPool(chefId.value)])

    newOrders.value = pool.newOrders
    servingOrders.value = pool.servingOrders
    historyOrders.value = pool.historyOrders
    
    // 使用实际订单数据计算统计值，不依赖后端返回的统计
    const allTodayOrders = [...newOrders.value, ...servingOrders.value, ...historyOrders.value]
    summary.value = {
      todayOrder: allTodayOrders.length,
      waitOrder: servingOrders.value.length,
      todayIncome: allTodayOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
    }
    
    console.log('[Desk] 今日订单统计:', summary.value)
    console.log('[Desk] 后端返回的统计:', todayRes?.data)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载工作台数据失败')
  } finally {
    loading.value = false
  }
}

function handleStatusToggle(value) {
  pendingStatus.value = value
  statusReason.value = value ? '恢复接单' : '休息中'
  statusRemark.value = ''
  statusDialogVisible.value = true
}

function cancelStatusChange() {
  online.value = !pendingStatus.value
  statusDialogVisible.value = false
}

async function confirmStatusChange() {
  if (!statusReason.value) {
    ElMessage.warning('请选择切换原因')
    return
  }

  statusSaving.value = true
  try {
    const target = pendingStatus.value ? 1 : 0
    const reasonText = statusRemark.value ? `${statusReason.value}（${statusRemark.value}）` : statusReason.value

    const res = await updateChefStatus(chefId.value, target, reasonText)
    if (res.code !== 200) {
      ElMessage.error(res.message || '状态切换失败')
      online.value = !pendingStatus.value
      return
    }

    localStorage.setItem('cooker_online_state', String(target))
    appendStatusReason({
      status: target,
      statusText: pendingStatus.value ? '在线接单' : '休息中',
      reason: reasonText
    })
    statusReasonLog.value = getStatusReasonLog()
    ElMessage.success('接单状态已更新')
    statusDialogVisible.value = false
  } catch (error) {
    online.value = !pendingStatus.value
    ElMessage.error(error?.response?.data?.message || error?.message || '状态切换失败')
  } finally {
    statusSaving.value = false
  }
}

onMounted(async () => {
  chefId.value = parseChefId()
  if (!chefId.value) {
    ElMessage.warning('请先登录厨师账号')
    router.push('/cooker/login')
    return
  }

  await loadDashboard()

  window.addEventListener('cooker-message-updated', (event) => {
    unreadCount.value = Number(event?.detail?.unreadCount || 0)
  })
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
