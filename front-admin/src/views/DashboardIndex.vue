<template>
  <div class="dashboard-index">
    <el-row :gutter="16" class="stats-cards">
      <!-- 总用户数卡片 -->
      <el-col :span="6">
        <el-card class="stat-card card-users">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">总用户数</div>
              <div class="card-value">{{ stats.totalUsers }}</div>
              <div class="card-tip" :class="growthClass(stats.userGrowthRate)">
                <el-icon v-if="stats.userGrowthRate >= 0"><Top /></el-icon>
                <el-icon v-else><Bottom /></el-icon>
                <span>较昨日 {{ stats.userGrowthRate >= 0 ? '+' : '' }}{{ stats.userGrowthRate }}%</span>
              </div>
            </div>
            <div class="card-icon icon-users">
              <el-icon size="28"><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待审核厨师卡片 -->
      <el-col :span="6">
        <el-card class="stat-card card-chefs">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">待审核厨师</div>
              <div class="card-value">{{ stats.pendingChefs }}</div>
              <div class="card-tip tip-warning">
                <el-icon><Warning /></el-icon>
                <span>需尽快处理</span>
              </div>
            </div>
            <div class="card-icon icon-chefs">
              <el-icon size="28"><ChefHat /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 今日订单卡片 -->
      <el-col :span="6">
        <el-card class="stat-card card-orders">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">今日订单</div>
              <div class="card-value">{{ stats.todayOrders }}</div>
              <div class="card-tip" :class="growthClass(stats.orderGrowthRate)">
                <el-icon v-if="stats.orderGrowthRate >= 0"><Top /></el-icon>
                <el-icon v-else><Bottom /></el-icon>
                <span>较昨日 {{ stats.orderGrowthRate >= 0 ? '+' : '' }}{{ stats.orderGrowthRate }}%</span>
              </div>
            </div>
            <div class="card-icon icon-orders">
              <el-icon size="28"><ShoppingCart /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 异常订单卡片 -->
      <el-col :span="6">
        <el-card class="stat-card card-abnormal">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">异常订单</div>
              <div class="card-value">{{ stats.abnormalOrders }}</div>
              <div class="card-tip tip-stable">
                <el-icon><CircleCheck /></el-icon>
                <span>状态稳定</span>
              </div>
            </div>
            <div class="card-icon icon-abnormal">
              <el-icon size="28"><InfoFilled /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
// 引入 Element Plus 图标
import {
  User, ChefHat, ShoppingCart, InfoFilled,
  Top, Bottom, Warning, CircleCheck
} from '@element-plus/icons-vue'
// 引入接口方法
import { getDashboardStats } from '@/api/dashboard'

// 统计数据
const stats = ref({
  totalUsers: 0,
  userGrowthRate: 0,
  pendingChefs: 0,
  todayOrders: 0,
  orderGrowthRate: 0,
  abnormalOrders: 0
})

// 定时器
let refreshTimer = null

// 加载统计数据
const loadStats = () => {
  getDashboardStats().then(res => {
    if (res.data) {
      stats.value = {
        totalUsers: res.data.totalUsers || 0,
        userGrowthRate: res.data.userGrowthRate || 0,
        pendingChefs: res.data.pendingChefs || 0,
        todayOrders: res.data.todayOrders || 0,
        orderGrowthRate: res.data.orderGrowthRate || 0,
        abnormalOrders: res.data.abnormalOrders || 0
      }
    }
  }).catch(() => {
    // 加载失败时使用默认值
    stats.value = {
      totalUsers: 0,
      userGrowthRate: 0,
      pendingChefs: 0,
      todayOrders: 0,
      orderGrowthRate: 0,
      abnormalOrders: 0
    }
  })
}

// 根据增长率返回样式类
const growthClass = (rate) => {
  if (rate > 0) return 'growth-positive'
  if (rate < 0) return 'growth-negative'
  return 'growth-neutral'
}

// 页面加载时获取数据
onMounted(() => {
  loadStats()
  // 每 30 秒自动刷新一次
  refreshTimer = setInterval(loadStats, 30000)
})

// 页面卸载时清除定时器
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.dashboard-index {
  padding: 0;
}

.stats-cards {
  margin: 0;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  margin-bottom: 16px;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #323233;
  margin-bottom: 8px;
}

.card-tip {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-tip .el-icon {
  font-size: 14px;
}

/* 增长率样式 */
.growth-positive {
  color: #07c160;
}

.growth-negative {
  color: #ee0a24;
}

.growth-neutral {
  color: #969799;
}

/* 提示样式 */
.tip-warning {
  color: #ff6b35;
}

.tip-stable {
  color: #969799;
}

/* 卡片图标 */
.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 各卡片主题色 */
.card-users .card-icon {
  background-color: #e8f3ff;
  color: #1989fa;
}

.card-chefs .card-icon {
  background-color: #fff2e8;
  color: #ff6b35;
}

.card-orders .card-icon {
  background-color: #e6fffa;
  color: #00b42a;
}

.card-abnormal .card-icon {
  background-color: #fff0f0;
  color: #f53f3f;
}
</style>
