<template>
  <div class="page">
    <section class="summary-grid">
      <el-card shadow="hover">
        <div class="summary-item">
          <span>本月订单数</span>
          <strong>{{ monthSummary.count }}</strong>
          <p>收入 ¥{{ money(monthSummary.income) }}</p>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="summary-item">
          <span>本年订单数</span>
          <strong>{{ yearSummary.count }}</strong>
          <p>收入 ¥{{ money(yearSummary.income) }}</p>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="summary-item">
          <span>当前筛选结果</span>
          <strong>{{ filteredOrders.length }}</strong>
          <p>收入 ¥{{ money(filteredIncome) }}</p>
        </div>
      </el-card>
    </section>

    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>历史订单</span>
          <div class="tools">
            <el-button @click="exportReport">导出年报</el-button>
          </div>
        </div>
      </template>

      <div class="filters">
        <el-input v-model="keyword" clearable placeholder="订单号/客户/地址" class="w-220" />

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />

        <el-select v-model="statusFilter" clearable placeholder="订单状态" class="w-160">
          <el-option label="已完成" :value="4" />
          <el-option label="已取消" :value="5" />
          <el-option label="已退款" :value="6" />
        </el-select>

        <el-input-number v-model="amountMin" :min="0" :step="50" placeholder="最低金额" />
        <el-input-number v-model="amountMax" :min="0" :step="50" placeholder="最高金额" />

        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="filteredOrders" border style="width: 100%" v-loading="loading" max-height="520">
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="orderType" label="服务类型" width="110" />
        <el-table-column label="客户" min-width="130">
          <template #default="scope">{{ scope.row.customerName || '-' }}</template>
        </el-table-column>
        <el-table-column label="预约时间" min-width="150">
          <template #default="scope">{{ formatDate(scope.row.reserveDate) }} {{ scope.row.reserveTime || '' }}</template>
        </el-table-column>
        <el-table-column label="金额" width="110">
          <template #default="scope">¥{{ money(scope.row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户评价" width="120">
          <template #default="scope">
            <el-rate :model-value="Math.round(scope.row.customerRating)" disabled size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="$router.push(`/cooker/order/${scope.row.id}`)">详情</el-button>
            <el-button link type="success" @click="openReview(scope.row)">回复评价</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>收入趋势（月）</span>
          <el-button @click="exportMonthlyTrend">导出趋势</el-button>
        </div>
      </template>

      <!-- 趋势图表 -->
      <TrendLineChart :data="monthlyTrend" value-key="income" />

      <!-- 月度收入表格 -->
      <div class="monthly-table-container">
        <el-table :data="monthlyTableData" border v-loading="loading" :row-class-name="tableRowClassName">
          <el-table-column prop="month" label="月份" width="120" align="center">
            <template #default="scope">{{ formatMonth(scope.row.month) }}</template>
          </el-table-column>
          <el-table-column prop="orderCount" label="订单数" width="100" align="center" />
          <el-table-column label="已完成" width="100" align="center">
            <template #default="scope">
              <el-tag type="success" size="small">{{ scope.row.completedCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="已取消" width="100" align="center">
            <template #default="scope">
              <el-tag type="danger" size="small">{{ scope.row.cancelledCount }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="总收入" width="140" align="center">
            <template #default="scope">
              <span class="income-text">¥{{ money(scope.row.income) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="预估到账" width="140" align="center">
            <template #default="scope">
              <span class="income-highlight">¥{{ money(scope.row.income * 0.82) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="客单均价" width="120" align="center">
            <template #default="scope">¥{{ money(scope.row.avgOrderValue) }}</template>
          </el-table-column>
          <el-table-column label="月收入环比" width="120" align="center">
            <template #default="scope">
              <span :class="getGrowthClass(scope.row.momGrowth)">
                {{ scope.row.momGrowth >= 0 ? '+' : '' }}{{ scope.row.momGrowth }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="reviewDialogVisible" width="560px" title="评价详情与回复">
      <template v-if="selectedReviewOrder">
        <div class="review-box">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">{{ selectedReviewOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="客户">{{ selectedReviewOrder.customerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="评分">
              <el-rate :model-value="Math.round(selectedReviewOrder.customerRating)" disabled />
            </el-descriptions-item>
            <el-descriptions-item label="评价内容">服务整体满意，沟通高效，期待再次合作。</el-descriptions-item>
          </el-descriptions>

          <el-input v-model="reviewReply" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="输入回复内容" />
        </div>
      </template>

      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReviewReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TrendLineChart from '@/components/charts/TrendLineChart.vue'
import { getHistoryOrders } from '@/api/cooker'
import {
  formatDate,
  money,
  monthlyIncomeByOrders,
  normalizeOrders,
  statusTagType,
  statusText,
  formatMonth,
  calculateMomGrowth
} from '@/utils/cookerOrder'
import { parseChefId } from '@/utils/cookerSession'

const router = useRouter()

const loading = ref(false)
const chefId = ref(null)
const orders = ref([])

const keyword = ref('')
const dateRange = ref([])
const statusFilter = ref(null)
const amountMin = ref(null)
const amountMax = ref(null)

const reviewDialogVisible = ref(false)
const selectedReviewOrder = ref(null)
const reviewReply = ref('')

function inDateRange(order) {
  if (!dateRange.value || dateRange.value.length !== 2) return true
  const [start, end] = dateRange.value
  if (!start || !end) return true

  const reserveDate = new Date(order.reserveDate)
  if (Number.isNaN(reserveDate.getTime())) return false

  const startDate = new Date(`${start}T00:00:00`)
  const endDate = new Date(`${end}T23:59:59`)
  return reserveDate >= startDate && reserveDate <= endDate
}

const filteredOrders = computed(() => {
  return orders.value.filter((order) => {
    const text = `${order.orderNo || ''} ${order.customerName || ''} ${order.address || ''}`
    const byKeyword = !keyword.value || text.includes(keyword.value.trim())
    const byStatus = statusFilter.value == null || order.status === statusFilter.value
    const amount = Number(order.totalAmount || 0)
    const byMin = amountMin.value == null || amount >= amountMin.value
    const byMax = amountMax.value == null || amount <= amountMax.value
    return byKeyword && byStatus && byMin && byMax && inDateRange(order)
  })
})

const filteredIncome = computed(() => filteredOrders.value.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0))
const monthlyTrend = computed(() => monthlyIncomeByOrders(filteredOrders.value).map((item) => ({ label: item.month, ...item })))

const monthlyTableData = computed(() => {
  const data = monthlyIncomeByOrders(filteredOrders.value)
  // 添加环比增长率
  return data.map((item, index) => {
    const previous = index > 0 ? data[index - 1].income : 0
    return {
      ...item,
      momGrowth: calculateMomGrowth(item.income, previous)
    }
  })
})

const monthSummary = computed(() => {
  const now = new Date()
  const monthOrders = orders.value.filter((order) => {
    const date = new Date(order.reserveDate)
    return date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth()
  })
  return {
    count: monthOrders.length,
    income: monthOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
  }
})

const yearSummary = computed(() => {
  const now = new Date()
  const yearOrders = orders.value.filter((order) => new Date(order.reserveDate).getFullYear() === now.getFullYear())
  return {
    count: yearOrders.length,
    income: yearOrders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
  }
})

function resetFilters() {
  keyword.value = ''
  dateRange.value = []
  statusFilter.value = null
  amountMin.value = null
  amountMax.value = null
}

function openReview(order) {
  selectedReviewOrder.value = order
  reviewReply.value = ''
  reviewDialogVisible.value = true
}

function submitReviewReply() {
  if (!reviewReply.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  const raw = localStorage.getItem('cooker_review_reply_v1')
  const replies = raw ? JSON.parse(raw) : []
  replies.unshift({
    orderId: selectedReviewOrder.value.id,
    orderNo: selectedReviewOrder.value.orderNo,
    reply: reviewReply.value,
    time: new Date().toISOString()
  })
  localStorage.setItem('cooker_review_reply_v1', JSON.stringify(replies.slice(0, 40)))

  ElMessage.success('评价回复已发送')
  reviewDialogVisible.value = false
}

function exportReport() {
  const now = new Date()
  const currentYear = now.getFullYear()

  console.log('[ExportReport] Current year:', currentYear)
  console.log('[ExportReport] Total orders:', orders.value.length)

  // 导出当年所有订单
  const list = orders.value.filter((order) => {
    const date = new Date(order.reserveDate)
    const year = date.getFullYear()
    return year === currentYear
  })

  console.log('[ExportReport] Filtered orders:', list.length)

  if (list.length === 0) {
    ElMessage.warning(`当前年份没有订单数据`)
    return
  }

  const header = ['订单号', '服务类型', '客户', '日期', '金额', '状态', '地址']
  const rows = list.map((item) => [
    item.orderNo || '',
    item.orderType || '',
    item.customerName || '',
    `${formatDate(item.reserveDate)} ${item.reserveTime || ''}`,
    money(item.totalAmount),
    statusText(item.status),
    item.address || ''
  ])

  const csv = [header, ...rows].map((row) => row.map((cell) => `"${String(cell).replaceAll('"', '""')}"`).join(',')).join('\n')
  const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `年度订单报表_${currentYear}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success(`已导出年报，共${list.length}条记录`)
}

function exportMonthlyTrend() {
  if (monthlyTableData.value.length === 0) {
    ElMessage.warning('没有可导出的月度趋势数据')
    return
  }

  const rows = monthlyTableData.value.map((item) => [
    formatMonth(item.month),
    item.orderCount,
    item.completedCount,
    item.cancelledCount,
    money(item.income),
    money(item.income * 0.82),
    money(item.avgOrderValue),
    `${item.momGrowth >= 0 ? '+' : ''}${item.momGrowth}%`
  ])

  const csv = [
    ['月份', '订单数', '已完成', '已取消', '总收入', '预估到账', '客单均价', '月收入环比'],
    ...rows
  ]
    .map((row) => row.map((cell) => `"${String(cell).replaceAll('"', '""')}"`).join(','))
    .join('\n')

  const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `月度收入趋势_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success('月度收入趋势已导出')
}

function getGrowthClass(growth) {
  if (growth > 0) return 'growth-positive'
  if (growth < 0) return 'growth-negative'
  return 'growth-neutral'
}

function tableRowClassName({ row, rowIndex }) {
  // 为收入最高的月份添加特殊样式
  if (monthlyTableData.value.length > 0) {
    const maxIncome = Math.max(...monthlyTableData.value.map((item) => item.income))
    if (row.income === maxIncome) {
      return 'top-income-row'
    }
  }
  return ''
}

async function loadOrders() {
  loading.value = true
  try {
    const res = await getHistoryOrders(chefId.value)
    if (res.code !== 200) {
      ElMessage.error(res.message || '获取历史订单失败')
      return
    }
    orders.value = normalizeOrders(Array.isArray(res.data) ? res.data : [])
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '获取历史订单失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  chefId.value = parseChefId()
  if (!chefId.value) {
    ElMessage.warning('请先登录厨师账号')
    router.push('/cooker/login')
    return
  }
  await loadOrders()
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

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.summary-item {
  display: grid;
  gap: 8px;
}

.summary-item span {
  color: #6f7c91;
  font-size: 13px;
}

.summary-item strong {
  font-size: 30px;
  color: #1f3044;
}

.summary-item p {
  color: #4f5c6f;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tools {
  display: flex;
  gap: 8px;
}

.filters {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.w-220 {
  width: 220px;
}

.review-box {
  display: grid;
  gap: 12px;
}

.monthly-table-container {
  margin-top: 16px;
}

.income-text {
  color: #606266;
  font-weight: 500;
}

.income-highlight {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

.growth-positive {
  color: #f56c6c;
  font-weight: 500;
}

.growth-negative {
  color: #67c23a;
  font-weight: 500;
}

.growth-neutral {
  color: #909399;
}

:deep(.top-income-row) {
  background-color: #fdf6ec !important;
}

:deep(.top-income-row:hover) {
  background-color: #f5e1d0 !important;
}

@media (max-width: 992px) {
  .page {
    padding: 0 12px 14px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .head-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
