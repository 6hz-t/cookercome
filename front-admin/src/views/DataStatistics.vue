<template>
  <div>数据统计页面测试</div>
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
                <div class="card-value">{{ userCount }}</div>
                <div class="card-tip" style="color: #07c160;">↗ 较昨日 +{{ userGrowth }}%</div>
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
                <div class="card-value">{{ chefCount }}</div>
                <div class="card-tip" style="color: #ff6b35;">↗ 较昨日 +{{ chefGrowth }}%</div>
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
                <div class="card-value">{{ orderCount }}</div>
                <div class="card-tip" style="color: #07c160;">↗ 较昨日 +{{ orderGrowth }}%</div>
              </div>
              <div class="card-icon" style="background-color: #e6fffa;">
                <el-icon size="24" color="#00b42a;"><ShoppingCart /></el-icon>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 趋势分析图表 -->
        <el-card class="chart-container">
          <div slot="header" class="clearfix">
            <span>趋势分析</span>
            <div class="time-range-selector">
              <el-radio-group v-model="timeRange" size="small" @change="onTimeRangeChange">
                <el-radio-button label="7天"></el-radio-button>
                <el-radio-button label="30天"></el-radio-button>
                <el-radio-button label="90天"></el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <div ref="chartRef" class="chart" style="height: 400px;"></div>
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
          <el-button type="success" @click="exportExcel">导出Excel</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
// 引入Element Plus图标
import { 
  User, UserFilled, Grid, ShoppingCart, TrendCharts, 
  Bell, InfoFilled, Shop, Search 
} from '@element-plus/icons-vue'
// 引入Element Plus提示框
import { ElMessageBox, ElMessage, ElInput } from 'element-plus'

// 模拟 ECharts（在真实项目中需要安装 echarts）
let echarts = null
try {
  echarts = require('echarts')
} catch(e) {
  console.warn('ECharts is not installed, using mock chart')
}

// 创建路由器实例
const router = useRouter()

// 当前激活菜单
const activeMenu = ref('stats')

// 当前日期
const currentDate = ref('2026年03月07日')

// 核心数据
const userCount = ref(12842)
const chefCount = ref(2845)
const orderCount = ref(856)
const userGrowth = ref(12)
const chefGrowth = ref(8)
const orderGrowth = ref(5)

// 时间范围
const timeRange = ref('30天')

// 图表引用
const chartRef = ref(null)
let chartInstance = null

// 模拟从缓存获取数据
const getDataFromCache = () => {
  console.log('尝试从缓存获取数据...')
  // 模拟缓存命中
  console.log('缓存命中，返回缓存数据')
  
  // 根据时间范围返回不同的数据
  const days = timeRange.value === '7天' ? 7 : timeRange.value === '30天' ? 30 : 90
  const dates = []
  const newUserCounts = []
  const newChefCounts = []
  const newOrderCounts = []

  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}-${date.getDate()}`)

    newUserCounts.push(Math.floor(Math.random() * 100) + 50)
    newChefCounts.push(Math.floor(Math.random() * 30) + 10)
    newOrderCounts.push(Math.floor(Math.random() * 200) + 100)
  }

  return {
    dates,
    newUserCounts,
    newChefCounts,
    newOrderCounts
  }
}

// 初始化图表
const initChart = () => {
  nextTick(() => {
    if (chartRef.value) {
      if (echarts) {
        chartInstance = echarts.init(chartRef.value)
      } else {
        // 模拟图表初始化
        console.log('初始化模拟图表')
        chartInstance = {
          setOption: (option) => console.log('设置图表选项:', option),
          resize: () => console.log('调整图表大小'),
          dispose: () => console.log('销毁图表实例')
        }
      }
      
      renderChart()
    }
  })
}

// 渲染图表
const renderChart = () => {
  const data = getDataFromCache()
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增用户', '新增厨师', '新增订单']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        stack: '总量',
        data: data.newUserCounts
      },
      {
        name: '新增厨师',
        type: 'line',
        stack: '总量',
        data: data.newChefCounts
      },
      {
        name: '新增订单',
        type: 'line',
        stack: '总量',
        data: data.newOrderCounts
      }
    ]
  }
  
  if (chartInstance) {
    chartInstance.setOption(option)
  }
}

// 时间范围变化
const onTimeRangeChange = () => {
  refreshData()
}

// 刷新数据
const refreshData = () => {
  // 模拟从缓存刷新数据
  const data = getDataFromCache()
  
  // 更新核心数据
  userCount.value = Math.floor(Math.random() * 1000) + 12500
  chefCount.value = Math.floor(Math.random() * 500) + 2500
  orderCount.value = Math.floor(Math.random() * 200) + 800
  
  // 更新增长率
  userGrowth.value = Math.floor(Math.random() * 5) + 10
  chefGrowth.value = Math.floor(Math.random() * 3) + 5
  orderGrowth.value = Math.floor(Math.random() * 4) + 3
  
  // 重新渲染图表
  renderChart()
  
  ElMessage.success('数据已刷新')
  
  // 记录操作日志
  console.log('Refresh data statistics:', {
    operator: '超级管理员',
    timestamp: new Date().toLocaleString(),
    action: 'refresh_data',
    timeRange: timeRange.value
  })
}

// 导出Excel
const exportExcel = () => {
  ElMessageBox.confirm(
    '确定要导出当前统计数据为Excel文件吗？',
    '导出Excel',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(() => {
    // 模拟下载Excel文件
    const data = getDataFromCache()
    
    // 创建CSV内容（简单模拟Excel）
    let csvContent = '日期,新增用户,新增厨师,新增订单\n'
    for (let i = 0; i < data.dates.length; i++) {
      csvContent += `${data.dates[i]},${data.newUserCounts[i]},${data.newChefCounts[i]},${data.newOrderCounts[i]}\n`
    }
    
    // 创建Blob对象并下载
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', `data-statistics-${new Date().toISOString().slice(0, 10)}.csv`)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('Excel文件已导出')
    
    // 记录操作日志
    console.log('Export data statistics:', {
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'export_excel',
      timeRange: timeRange.value
    })
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
    
    // 清空本地存储中的登录相关数据
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    
    // 跳转到登录页面并防止通过浏览器返回按钮返回
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

// 组件挂载时初始化
onMounted(() => {
  initChart()
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
  margin-bottom: 8px;
}

.card-tip {
  font-size: 12px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图表容器 */
.chart-container {
  margin-bottom: 20px;
}

.chart-container .clearfix {
  display: flex;
  justify-content: space-between;
  align-items: center;
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