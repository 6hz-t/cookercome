<template>
  <div class="chef-dashboard">
    <!-- 顶部：欢迎语 + 在线状态 -->
    <header class="header">
      <div class="welcome">欢迎您，{{ chefName }}</div>
      <div class="status-switch">
        <span>在线状态</span>
        <el-switch
          v-model="isOnline"
          active-color="#1890ff"
          inactive-color="#e0e0e0"
          @change="handleStatusChange"
        />
      </div>
    </header>

    <!-- 数据统计卡片 -->
    <section class="stats">
      <div class="stats-card" v-for="(item, index) in statsData" :key="index">
        <div class="num">{{ item.value }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </section>

    <!-- 核心功能区：常用功能 + 收入统计 -->
    <section class="core-container">
      <!-- 常用功能卡片 -->
      <el-card class="core-card">
        <template #header>
          <div class="card-title">常用功能</div>
        </template>
        <div class="func-grid">
          <div 
            class="func-item" 
            v-for="(item, index) in funcList" 
            :key="index"
            @click="handleFuncClick(item)"
          >
            <div class="func-icon">{{ item.icon }}</div>
            <div class="func-text">{{ item.name }}</div>
          </div>
        </div>
      </el-card>

      <!-- 收入统计卡片 -->
      <el-card class="core-card">
        <template #header>
          <div class="card-title">收入统计</div>
        </template>
        <!-- 收入趋势图（ECharts占位，可替换） -->
        <div class="income-chart" ref="chartRef">
          <span>月度收入趋势图（可接入ECharts）</span>
        </div>
        <!-- 收入明细 -->
        <div class="income-detail">
          <div class="detail-item" v-for="(item, index) in incomeData" :key="index">
            <div class="detail-num">{{ item.value }}</div>
            <div class="detail-label">{{ item.label }}</div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElSwitch, ElCard } from 'element-plus'
// 如需集成ECharts，取消下面注释
// import * as echarts from 'echarts'



// 响应式数据
const chefName = ref('王师傅') // 厨师姓名
const isOnline = ref(true) // 在线状态
const chartRef = ref(null) // 图表容器ref

// 数据统计模拟数据
const statsData = ref([
  { value: 3, label: '今日已接订单' },
  { value: 2, label: '待服务订单' },
  { value: '¥860', label: '今日收入' }
])

// 常用功能列表
const funcList = ref([
  { icon: '👤', name: '个人资料', action: 'profile' },
  { icon: '🍳', name: '我的菜单', action: 'menu' },
  { icon: '💰', name: '收入明细', action: 'income' },
  { icon: '📩', name: '消息通知', action: 'notice' },
  { icon: '⚙️', name: '系统设置', action: 'setting' },
  { icon: '📞', name: '联系客服', action: 'service' }
])

// 收入统计数据
const incomeData = ref([
  { value: '¥2580', label: '本月总收入' },
  { value: 12, label: '本月订单数' },
  { value: '¥215', label: '平均单价' }
])

// 在线状态切换处理
const handleStatusChange = (val) => {
  const status = val ? '在线' : '离线'
  ElMessage.success(`已切换为${status}状态`)
}

// 常用功能点击处理
const handleFuncClick = (item) => {
  switch (item.action) {
    case 'profile':
      ElMessage.info('进入个人资料页面')
      // 可添加路由跳转：router.push('/chef/profile')
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

// 初始化ECharts（可选，取消注释即可使用）
onMounted(() => {
//   if (chartRef.value) {
//     const myChart = echarts.init(chartRef.value)
//     const option = {
//       xAxis: { type: 'category', data: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'] },
//       yAxis: { type: 'value' },
//       series: [{
//         data: [120, 200, 150, 80, 250, 220, 300],
//         type: 'line',
//         smooth: true,
//         itemStyle: { color: '#1890ff' },
//         areaStyle: { 
//           color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
//             { offset: 0, color: '#e6f7ff' },
//             { offset: 1, color: '#f0f7ff' }
//           ]) 
//         }
//       }]
//     }
//     myChart.setOption(option)
//     // 自适应窗口大小
//     window.addEventListener('resize', () => myChart.resize())
//   }
})
</script>

<style scoped>
.chef-dashboard {
    /* 宽度与浏览器宽度一致 */
  width: 1000px;
  background-color: #FFF;
  min-height: 100vh;
  padding-bottom: 20px;
}

/* 顶部样式 */
.header {
  background-color: #fff;
  padding: 15px 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 99;
}
.header .welcome {
  font-size: 16px;
  font-weight: 500;
}
.header .status-switch {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 数据统计卡片 */
.stats {
  display: flex;
  gap: 15px;
  padding: 20px;
  flex-wrap: wrap;
}
.stats-card {
  flex: 1;
  min-width: 120px;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  text-align: center;
}
.stats-card .num {
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
  margin: 8px 0;
}
.stats-card .label {
  font-size: 14px;
  color: #666;
}

/* 核心功能区 */
.core-container {
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 15px;
}
.core-card {
  --el-card-border-radius: 8px;
  --el-card-box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  --el-card-padding: 20px;
}
.core-card .card-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  margin: 0;
}

/* 常用功能样式 */
.func-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-top: 18px;
}
.func-item {
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
}
.func-item:hover {
  transform: scale(1.05);
}
.func-icon {
  width: 45px;
  height: 45px;
  line-height: 45px;
  border-radius: 50%;
  background-color: #f0f7ff;
  color: #1890ff;
  font-size: 20px;
  margin: 0 auto 10px;
}
.func-text {
  font-size: 13px;
  color: #333;
}

/* 收入统计样式 */
.income-chart {
  height: 200px;
  background-color: #f9f9f9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  margin: 18px 0 15px;
  position: relative;
  overflow: hidden;
}
.income-chart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f7ff 100%);
  z-index: 1;
}
.income-chart span {
  position: relative;
  z-index: 2;
  font-size: 14px;
  color: #666;
}
.income-detail {
  display: flex;
  justify-content: space-between;
  padding: 0 5px;
}
.detail-item {
  text-align: center;
  flex: 1;
}
.detail-num {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 5px;
}
.detail-label {
  font-size: 12px;
  color: #666;
}
</style>