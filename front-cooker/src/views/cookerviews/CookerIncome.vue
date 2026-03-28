<template>
  <div class="page">
    <section class="summary-grid">
      <el-card shadow="hover"><div class="summary"><span>总收入</span><strong>¥{{ money(summary.total) }}</strong></div></el-card>
      <el-card shadow="hover"><div class="summary"><span>已完成订单</span><strong>{{ summary.count }}</strong></div></el-card>
      <el-card shadow="hover"><div class="summary"><span>客单均价</span><strong>¥{{ money(summary.avg) }}</strong></div></el-card>
    </section>

    <!-- 收入趋势区域：图表 + 表格横排展示 -->
    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>按月收入趋势</span>
          <el-button @click="exportMonthlyTrend">导出趋势</el-button>
        </div>
      </template>

      <div class="trend-content">
        <!-- 左侧：趋势图表 -->
        <div class="trend-chart-wrapper">
          <TrendLineChart :data="monthlyTrend" value-key="income" />
        </div>

        <!-- 右侧：月度收入表格 -->
        <div class="trend-table-wrapper">
          <el-table :data="monthlyTableData" border v-loading="loading" :row-class-name="tableRowClassName" max-height="400">
            <el-table-column prop="month" label="月份" width="90" align="center">
              <template #default="scope">{{ formatMonth(scope.row.month) }}</template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单数" width="70" align="center" />
            <el-table-column label="已完成" width="70" align="center">
              <template #default="scope">
                <el-tag type="success" size="small">{{ scope.row.completedCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="已取消" width="70" align="center">
              <template #default="scope">
                <el-tag type="danger" size="small">{{ scope.row.cancelledCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="总收入" width="110" align="center">
              <template #default="scope">
                <span class="income-text">¥{{ money(scope.row.income) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="预估到账" width="110" align="center">
              <template #default="scope">
                <span class="income-highlight">¥{{ money(scope.row.income * 0.82) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="客单均价" width="90" align="center">
              <template #default="scope">¥{{ money(scope.row.avgOrderValue) }}</template>
            </el-table-column>
            <el-table-column label="环比" width="70" align="center">
              <template #default="scope">
                <span :class="getGrowthClass(scope.row.momGrowth)">
                  {{ scope.row.momGrowth >= 0 ? '+' : '' }}{{ scope.row.momGrowth }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 收益明细表格 -->
    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>收益明细</span>
          <el-button @click="exportIncome">导出明细</el-button>
        </div>
      </template>

      <div class="filters">
        <el-input v-model="keyword" clearable placeholder="订单号/客户" class="w-220" />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </div>

      <el-table :data="filteredOrders" border v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" min-width="170" />
        <el-table-column label="服务时间" min-width="160">
          <template #default="scope">{{ formatDate(scope.row.reserveDate) }} {{ scope.row.reserveTime || '' }}</template>
        </el-table-column>
        <el-table-column prop="orderType" label="类型" width="120" />
        <el-table-column label="订单金额" width="120">
          <template #default="scope">¥{{ money(scope.row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="平台服务费" width="120">
          <template #default="scope">¥{{ money(scope.row.totalAmount * 0.18) }}</template>
        </el-table-column>
        <el-table-column label="预估到账" width="120">
          <template #default="scope">¥{{ money(scope.row.estimatedIncome) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TrendLineChart from '@/components/charts/TrendLineChart.vue'
import { getChefOrderPool } from '@/api/cooker'
import { formatDate, money, monthlyIncomeByOrders, normalizeOrders, formatMonth, calculateMomGrowth } from '@/utils/cookerOrder'
import { parseChefId } from '@/utils/cookerSession'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const keyword = ref('')
const dateRange = ref([])

function matchDate(order) {
  if (!dateRange.value || dateRange.value.length !== 2) return true
  const date = new Date(order.reserveDate || order.createTime)
  if (Number.isNaN(date.getTime())) return false

  const start = new Date(`${dateRange.value[0]}T00:00:00`)
  const end = new Date(`${dateRange.value[1]}T23:59:59`)
  return date >= start && date <= end
}

const filteredOrders = computed(() => {
  return orders.value.filter((item) => {
    const text = `${item.orderNo || ''} ${item.customerName || ''}`
    const hitKeyword = !keyword.value || text.includes(keyword.value.trim())
    return hitKeyword && matchDate(item)
  })
})

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

const summary = computed(() => {
  const total = filteredOrders.value.reduce((sum, item) => sum + Number(item.estimatedIncome || 0), 0)
  const count = filteredOrders.value.length
  return {
    total,
    count,
    avg: count ? total / count : 0
  }
})

function exportIncome() {
  const rows = filteredOrders.value.map((item) => [
    item.orderNo,
    `${formatDate(item.reserveDate)} ${item.reserveTime || ''}`,
    item.orderType,
    money(item.totalAmount),
    money(item.totalAmount * 0.18),
    money(item.estimatedIncome)
  ])

  const csv = [['订单号', '服务时间', '类型', '订单金额', '平台服务费', '预估到账'], ...rows]
    .map((row) => row.map((cell) => `"${String(cell).replaceAll('"', '""')}"`).join(','))
    .join('\n')

  const blob = new Blob([`\uFEFF${csv}`], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `收益明细_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success('收益明细已导出')
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

async function loadIncome() {
  loading.value = true
  try {
    const chefId = parseChefId()
    if (!chefId) {
      ElMessage.warning('请先登录厨师账号')
      router.push('/cooker/login')
      return
    }

    const pool = await getChefOrderPool(chefId)
    const merged = normalizeOrders([...pool.servingOrders, ...pool.historyOrders])
    orders.value = merged.filter((item) => [3, 4].includes(item.status))
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载收益明细失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadIncome)
</script>

<style scoped>
.page {
  max-width: 1200px;
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

.summary {
  display: grid;
  gap: 8px;
}

.summary span {
  color: #748197;
}

.summary strong {
  font-size: 28px;
  color: #243548;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* 收入趋势区域：图表 + 表格横排布局 */
.trend-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  align-items: start;
}

.trend-chart-wrapper,
.trend-table-wrapper {
  min-width: 0;
}

.trend-chart-wrapper :deep(svg) {
  height: 280px;
}

.trend-table-wrapper :deep(.el-table) {
  font-size: 13px;
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

  .w-220 {
    width: 100%;
  }

  /* 平板和移动端改为竖排 */
  .trend-content {
    grid-template-columns: 1fr;
  }

  .trend-chart-wrapper :deep(svg) {
    height: 220px;
  }
}
</style>
