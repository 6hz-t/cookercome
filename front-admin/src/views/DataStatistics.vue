<template>
  <div class="data-statistics">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <el-icon size="24" color="#1989fa"><UserFilled /></el-icon>
        <span class="sidebar-title">管理后台</span>
      </div>
      <div class="sidebar-menu">
        <div class="menu-group">
          <div class="group-title">核心管理</div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'account' }"
            @click="navigateTo('/dashboard')"
          >
            <el-icon><User /></el-icon>
            <span>账号管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'chef' }"
            @click="navigateTo('/chef-audit')"
          >
            <el-icon><UserFilled /></el-icon>
            <span>厨师审核</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'data' }"
            @click="navigateTo('/data-management')"
          >
            <el-icon><Grid /></el-icon>
            <span>基础数据管理</span>
          </div>
        </div>
        <div class="menu-group">
          <div class="group-title">业务运营</div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'order' }"
            @click="navigateTo('/order-control')"
          >
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'stats' }"
            @click="navigateTo('/statistics')"
          >
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </div>
        </div>
      </div>
      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="32" src="https://img.yzcdn.cn/vant/cat.jpeg"></el-avatar>
          <div class="user-text">
            <div class="user-name">超级管理员</div>
            <div class="user-id">ID: 10001</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="content">
      <!-- 顶部信息栏 -->
      <div class="top-info">
        <div class="breadcrumb">业务运营 &gt; 数据统计</div>
        <div class="top-right">
          <div class="date-info">当前日期 {{ currentDate }}</div>
          <el-icon size="20" class="bell-icon"><Bell /></el-icon>
          <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">安全退出</el-button>
        </div>
      </div>

      <!-- 数据统计主体内容 -->
      <div class="data-statistics-section">
        <!-- 核心数据看板 -->
        <div class="overview-cards">
          <el-card class="card">
            <div class="card-content">
              <div class="card-info">
                <div class="card-title">用户总数</div>
                <div class="card-value">{{ statistics.userCount }}</div>
                <div class="card-tip" :class="growthClass(statistics.userGrowthRate)">
                  <span>较昨日 {{ statistics.userGrowthRate >= 0 ? '+' : '' }}{{ statistics.userGrowthRate }}%</span>
                </div>
              </div>
              <div class="card-icon" style="background-color: #e8f3ff;">
                <el-icon size="24" color="#1989fa"><User /></el-icon>
              </div>
            </div>
          </el-card>
          <el-card class="card">
            <div class="card-content">
              <div class="card-info">
                <div class="card-title">厨师总数</div>
                <div class="card-value">{{ statistics.chefCount }}</div>
                <div class="card-tip" :class="growthClass(statistics.chefGrowthRate)">
                  <span>较昨日 {{ statistics.chefGrowthRate >= 0 ? '+' : '' }}{{ statistics.chefGrowthRate }}%</span>
                </div>
              </div>
              <div class="card-icon" style="background-color: #fff2e8;">
                <el-icon size="24" color="#ff6b35;"><Shop /></el-icon>
              </div>
            </div>
          </el-card>
          <el-card class="card">
            <div class="card-content">
              <div class="card-info">
                <div class="card-title">订单总数</div>
                <div class="card-value">{{ statistics.orderCount }}</div>
                <div class="card-tip" :class="growthClass(statistics.orderGrowthRate)">
                  <span>较昨日 {{ statistics.orderGrowthRate >= 0 ? '+' : '' }}{{ statistics.orderGrowthRate }}%</span>
                </div>
              </div>
              <div class="card-icon" style="background-color: #e6fffa;">
                <el-icon size="24" color="#00b42a;"><ShoppingCart /></el-icon>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 趋势分析图表 -->
        <el-card class="chart-container">
          <div class="chart-header">
            <span>趋势分析</span>
            <div class="time-range-selector">
              <el-radio-group v-model="timeRange" size="small" @change="onTimeRangeChange">
                <el-radio-button :label="7">7 天</el-radio-button>
                <el-radio-button :label="30">30 天</el-radio-button>
                <el-radio-button :label="90">90 天</el-radio-button>
              </el-radio-group>
            </div>
          </div>

          <div ref="chartRef" class="chart" style="height: 400px;"></div>
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="refreshData" :loading="loading">刷新数据</el-button>
          <el-button type="success" @click="exportExcel">导出 Excel</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
// 引入 Element Plus 图标
import {
  User, UserFilled, Grid, ShoppingCart, TrendCharts,
  Bell, Shop
} from '@element-plus/icons-vue'
// 引入 Element Plus 提示框
import { ElMessageBox, ElMessage } from 'element-plus'
// 引入 ECharts
import * as echarts from 'echarts'
// 引入统计接口
import { getStatistics as getStatisticsApi } from '@/api/statistics'

// 创建路由器实例
const router = useRouter()

// 当前激活菜单
const activeMenu = ref('stats')

// 当前日期（动态获取）
const currentDate = ref('')

// 格式化日期为 YYYY 年 MM 月 DD 日
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

// 初始化当前日期
currentDate.value = formatDate(new Date())

// 统计数据
const statistics = ref({
  userCount: 0,
  userGrowthRate: 0,
  chefCount: 0,
  chefGrowthRate: 0,
  orderCount: 0,
  orderGrowthRate: 0,
  dates: [],
  newUserCounts: [],
  newChefCounts: [],
  newOrderCounts: []
})

// 时间范围
const timeRange = ref(30)

// 加载状态
const loading = ref(false)

// 图表引用
const chartRef = ref(null)
let chartInstance = null

// 根据增长率返回样式类
const growthClass = (rate) => {
  if (rate > 0) return 'growth-positive'
  if (rate < 0) return 'growth-negative'
  return 'growth-neutral'
}

// 加载统计数据
const loadStatistics = () => {
  loading.value = true
  getStatisticsApi(timeRange.value).then(res => {
    if (res.data) {
      statistics.value = {
        userCount: res.data.userCount || 0,
        userGrowthRate: res.data.userGrowthRate || 0,
        chefCount: res.data.chefCount || 0,
        chefGrowthRate: res.data.chefGrowthRate || 0,
        orderCount: res.data.orderCount || 0,
        orderGrowthRate: res.data.orderGrowthRate || 0,
        dates: res.data.dates || [],
        newUserCounts: res.data.newUserCounts || [],
        newChefCounts: res.data.newChefCounts || [],
        newOrderCounts: res.data.newOrderCounts || []
      }
      renderChart()
    }
  }).catch(() => {
    ElMessage.error('加载统计数据失败')
  }).finally(() => {
    loading.value = false
  })
}

// 初始化图表
const initChart = () => {
  nextTick(() => {
    if (chartRef.value) {
      chartInstance = echarts.init(chartRef.value)
      renderChart()
    }
  })
}

// 渲染图表
const renderChart = () => {
  if (!chartInstance) return

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增用户', '新增厨师', '新增订单'],
      bottom: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: statistics.value.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#1989fa' },
        data: statistics.value.newUserCounts
      },
      {
        name: '新增厨师',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#ff6b35' },
        data: statistics.value.newChefCounts
      },
      {
        name: '新增订单',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#00b42a' },
        data: statistics.value.newOrderCounts
      }
    ]
  }

  chartInstance.setOption(option)
}

// 时间范围变化
const onTimeRangeChange = () => {
  loadStatistics()
}

// 刷新数据
const refreshData = () => {
  loadStatistics()
  ElMessage.success('数据已刷新')
}

// 导出 Excel
const exportExcel = () => {
  ElMessageBox.confirm(
    '确定要导出当前统计数据为 Excel 文件吗？',
    '导出 Excel',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(() => {
    // 创建 CSV 内容
    let csvContent = '日期，新增用户，新增厨师，新增订单\n'
    const dates = statistics.value.dates
    const newUserCounts = statistics.value.newUserCounts
    const newChefCounts = statistics.value.newChefCounts
    const newOrderCounts = statistics.value.newOrderCounts

    for (let i = 0; i < dates.length; i++) {
      csvContent += `${dates[i]},${newUserCounts[i] || 0},${newChefCounts[i] || 0},${newOrderCounts[i] || 0}\n`
    }

    // 创建 Blob 对象并下载
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `data-statistics-${new Date().toISOString().slice(0, 10)}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)

    ElMessage.success('Excel 文件已导出')
  }).catch(() => {
    // 用户取消操作
  })
}

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}

// 处理安全退出
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')

    router.replace('/login')

    ElMessage({
      type: 'success',
      message: '退出登录成功！'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Logout error:', error)
    } else {
      ElMessage({
        type: 'info',
        message: '已取消退出登录'
      })
    }
  }
}

// 窗口大小变化时调整图表大小
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 组件挂载时初始化
onMounted(() => {
  initChart()
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.data-statistics {
  display: flex;
  height: 100vh;
  background-color: #f7f8fa;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  background-color: #fff;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.sidebar-title {
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
}

.sidebar-menu {
  flex: 1;
  padding: 10px 0;
}

.menu-group {
  margin-bottom: 20px;
}

.group-title {
  padding: 8px 20px;
  font-size: 12px;
  color: #969799;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item:hover {
  background-color: #f7f8fa;
}

.menu-item.active {
  background-color: #e8f3ff;
  color: #1989fa;
  border-right: 3px solid #1989fa;
}

.menu-item .el-icon {
  margin-right: 10px;
}

.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-text {
  margin-left: 10px;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
}

.user-id {
  font-size: 12px;
  color: #969799;
}

/* 右侧内容区 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.top-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.breadcrumb {
  font-size: 14px;
  color: #969799;
}

.top-right {
  display: flex;
  align-items: center;
}

.date-info {
  margin-right: 15px;
  font-size: 14px;
}

.bell-icon {
  margin-right: 15px;
  cursor: pointer;
}

/* 数据统计 */
.data-statistics-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 数据概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #323233;
  margin-bottom: 8px;
}

.card-tip {
  font-size: 12px;
}

.growth-positive {
  color: #07c160;
}

.growth-negative {
  color: #ee0a24;
}

.growth-neutral {
  color: #969799;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 图表容器 */
.chart-container {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header span {
  font-size: 16px;
  font-weight: bold;
}

.time-range-selector {
  display: flex;
  align-items: center;
}

.chart {
  width: 100%;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}
</style>
