<template>
  <div class="order-page">
    <!-- 顶部导航栏 -->
    <header class="order-header">
      <div class="header-content">
        <el-button circle @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h1 class="page-title">我的订单</h1>
        <div class="header-spacer"></div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="order-main">
      <!-- 订单筛选 -->
      <div class="order-filters scroll-animate">
        <el-tabs v-model="filterType" class="filter-tabs">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="待确认" name="pending" />
          <el-tab-pane label="已确认" name="confirmed" />
          <el-tab-pane label="烹饪中" name="cooking" />
          <el-tab-pane label="已完成" name="completed" />
          <el-tab-pane label="已取消" name="cancelled" />
        </el-tabs>
      </div>

      <!-- 订单列表 -->
      <div class="order-list scroll-animate">
        <div 
          v-for="order in filteredOrders" 
          :key="order.id"
          class="order-card"
          @click="viewOrderDetail(order)"
        >
          <div class="order-status-bar" :class="order.status"></div>
          
          <div class="order-content">
            <div class="order-header-info">
              <div class="chefInfo-info">
                <el-avatar :size="45" :src="order.chefAvatar" />
                <div class="chefInfo-details">
                  <h4>{{ order.chefName }}</h4>
                  <p>{{ order.serviceType }}</p>
                </div>
              </div>
              <el-tag :type="getStatusType(order.status)" size="small">{{ getStatusText(order.status) }}</el-tag>
            </div>

            <div class="order-details">
              <div class="detail-row">
                <el-icon><Calendar /></el-icon>
                <span>服务时间：{{ order.serviceDate }} {{ order.serviceTime }}</span>
              </div>
              <div class="detail-row">
                <el-icon><Location /></el-icon>
                <span>服务地址：{{ order.address }}</span>
              </div>
              <div class="detail-row">
                <el-icon><User /></el-icon>
                <span>用餐人数：{{ order.guestCount }}人</span>
                <span style="margin-left: 20px;">
                  <el-icon><Money /></el-icon>
                  ¥{{ order.totalAmount }}
                </span>
              </div>
            </div>

            <div class="order-actions">
              <el-button 
                v-if="order.status === 'confirmed'" 
                size="small" 
                type="danger"
                @click.stop="cancelOrder(order.id)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.status === 'confirmed'" 
                size="small" 
                type="primary"
                @click.stop="contactChef(order)"
              >
                联系厨师
              </el-button>
              <el-button 
                v-if="order.status === 'completed'" 
                size="small"
                @click.stop="reviewOrder(order)"
              >
                评价
              </el-button>
              <el-button 
                v-if="order.status === 'completed' || order.status === 'cancelled'" 
                size="small" 
                type="primary"
                @click.stop="bookAgain(order)"
              >
                再次预约
              </el-button>
              <el-button 
                v-if="order.status === 'pending'" 
                size="small" 
                type="warning"
                @click.stop="payOrder(order)"
              >
                立即支付
              </el-button>
            </div>
          </div>
        </div>

        <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
      </div>
    </main>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="showDetail" title="订单详情" width="600px" class="dark-dialog">
      <div v-if="selectedOrder" class="order-detail-content">
        <div class="detail-section">
          <h4>厨师信息</h4>
          <div class="chefInfo-detail-row">
            <el-avatar :size="60" :src="selectedOrder.chefAvatar" />
            <div class="chefInfo-detail-info">
              <p class="chefInfo-name">{{ selectedOrder.chefName }}</p>
              <p class="chefInfo-type">{{ selectedOrder.serviceType }}</p>
              <el-rate v-model="selectedOrder.chefRating" disabled text-color="#ff9900" />
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>服务信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">服务日期</span>
              <span class="value">{{ selectedOrder.serviceDate }}</span>
            </div>
            <div class="info-item">
              <span class="label">服务时间</span>
              <span class="value">{{ selectedOrder.serviceTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">用餐人数</span>
              <span class="value">{{ selectedOrder.guestCount }}人</span>
            </div>
            <div class="info-item">
              <span class="label">订单状态</span>
              <el-tag :type="getStatusType(selectedOrder.status)">{{ getStatusText(selectedOrder.status) }}</el-tag>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4>服务地址</h4>
          <p class="address-text">
            <el-icon><Location /></el-icon>
            {{ selectedOrder.address }}
          </p>
        </div>

        <div class="detail-section">
          <h4>费用明细</h4>
          <div class="price-breakdown">
            <div class="price-row">
              <span>服务费</span>
              <span>¥{{ (selectedOrder.totalAmount * 0.8).toFixed(0) }}</span>
            </div>
            <div class="price-row">
              <span>食材费</span>
              <span>¥{{ (selectedOrder.totalAmount * 0.15).toFixed(0) }}</span>
            </div>
            <div class="price-row">
              <span>交通费</span>
              <span>¥30</span>
            </div>
            <div class="price-row total">
              <span>订单总额</span>
              <span class="total-price">¥{{ selectedOrder.totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showDetail = false">关闭</el-button>
        <el-button type="primary" @click="viewOrderMap(selectedOrder)">查看位置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowLeft, Calendar, Location, User, Money,
  Star, Phone, ChatDotRound
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
// 主题状态 (已移除)
// const isDarkMode = ref(false)
const filterType = ref('all')
const showDetail = ref(false)
const selectedOrder = ref(null)

// 模拟订单数据
const orders = ref([
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
    totalAmount: 598,
    chefRating: 4.9
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
    totalAmount: 888,
    chefRating: 4.8
  },
  {
    id: 3,
    chefName: '张师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '家常宴席',
    status: 'completed',
    serviceDate: '2026-02-28',
    serviceTime: '18:00-20:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    guestCount: 4,
    totalAmount: 468,
    chefRating: 5.0
  }
])

// 过滤后的订单
const filteredOrders = computed(() => {
  if (filterType.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === filterType.value)
})

// 状态映射
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'confirmed': 'success',
    'cooking': 'primary',
    'completed': 'info',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'cooking': '烹饪中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

// 订单操作
const viewOrderDetail = (order) => {
  selectedOrder.value = { ...order }
  showDetail.value = true
}

const cancelOrder = (orderId) => {
  ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = orders.value.findIndex(o => o.id === orderId)
    if (index !== -1) {
      orders.value[index].status = 'cancelled'
    }
    ElMessage.success('订单已取消')
  }).catch(() => {})
}

const contactChef = (order) => {
  ElMessageBox.confirm(`是否拨打${order.chefName}的电话？`, '联系厨师', {
    confirmButtonText: '拨打',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.info('正在拨打电话...')
  }).catch(() => {})
}

const reviewOrder = (order) => {
  ElMessageBox.prompt('请为本次服务打分并写下评价', '评价订单', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '评价内容不能为空'
  }).then(({ value }) => {
    ElMessage.success('评价提交成功')
  }).catch(() => {})
}

const bookAgain = (order) => {
  ElMessage.success('正在为您重新预约...')
}

const payOrder = (order) => {
  ElMessageBox.confirm(`订单金额：¥${order.totalAmount}，是否立即支付？`, '支付订单', {
    confirmButtonText: '立即支付',
    cancelButtonText: '稍后支付',
    type: 'warning'
  }).then(() => {
    ElMessage.success('支付成功')
    order.status = 'confirmed'
  }).catch(() => {})
}

const viewOrderMap = (order) => {
  ElMessage.info('打开地图查看服务位置')
}

// 初始化主题
onMounted(() => {
  // 固定使用暗色模式
})
</script>

<style scoped>
.order-page {
  --bg-color: #1a1a1a;
  --card-bg: #2d2d2d;
  --text-primary: #fff;
  --text-secondary: #ccc;
  --border-color: #444;
  
  min-height: 100vh;
  background: var(--bg-color);
  transition: all 0.3s ease;
}

.order-header {
  background: var(--card-bg);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  flex: 1;
  text-align: center;
}

.header-spacer {
  width: 40px;
}

.order-main {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.order-filters {
  background: var(--card-bg);
  border-radius: 12px;
  margin-bottom: 20px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-tabs {
  width: 100%;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  background: var(--card-bg);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.order-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.order-status-bar {
  height: 4px;
  width: 100%;
}

.order-status-bar.confirmed { background: #52c41a; }
.order-status-bar.pending { background: #faad14; }
.order-status-bar.cooking { background: #1890ff; }
.order-status-bar.completed { background: #999; }
.order-status-bar.cancelled { background: #ff4d4f; }

.order-content {
  padding: 20px;
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chefInfo-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chefInfo-details h4 {
  font-size: 16px;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.chefInfo-details p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.order-details {
  margin-bottom: 15px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.detail-row .el-icon {
  color: #667eea;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid var(--border-color);
}

/* 订单详情 */
.order-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  padding: 15px;
  background: var(--bg-color);
  border-radius: 8px;
}

.detail-section h4 {
  font-size: 16px;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  font-weight: bold;
}

.chefInfo-detail-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chefInfo-detail-info {
  flex: 1;
}

.chefInfo-name {
  font-size: 16px;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.chefInfo-type {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 8px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  font-size: 12px;
  color: var(--text-muted);
}

.value {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.address-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.price-breakdown {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-secondary);
}

.price-row.total {
  border-top: 1px solid var(--border-color);
  padding-top: 10px;
  font-weight: bold;
}

.total-price {
  color: #f56c6c;
  font-size: 18px;
}

/* 暗色模式对话框 */
.dark-dialog :deep(.el-dialog) {
  background: var(--card-bg);
}

.dark-dialog :deep(.el-dialog__title),
.dark-dialog :deep(.el-form-item__label) {
  color: var(--text-primary);
}

/* 滚动动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(30px);
  animation: slideUp 0.5s ease forwards;
}

@keyframes slideUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
