<template>
  <div class="page">
    <section class="stats-grid">
      <el-card shadow="hover"><div class="stat"><span>待开始</span><strong>{{ stats.pending }}</strong></div></el-card>
      <el-card shadow="hover"><div class="stat"><span>服务中</span><strong>{{ stats.serving }}</strong></div></el-card>
      <el-card shadow="hover"><div class="stat"><span>总数</span><strong>{{ stats.total }}</strong></div></el-card>
    </section>

    <el-empty v-if="orders.length === 0" description="暂无服务中订单" />

    <section v-else class="order-grid">
      <el-card v-for="order in orders" :key="order.id" class="order-card" shadow="hover">
        <div class="head">
          <strong>{{ order.orderNo }}</strong>
          <el-tag :type="statusTagType(order.status)">{{ statusText(order.status) }}</el-tag>
        </div>

        <div class="row"><span>客户</span>{{ order.customerName || '-' }}</div>
        <div class="row"><span>电话</span>{{ order.customerPhone || '-' }}</div>
        <div class="row"><span>时间</span>{{ formatDate(order.reserveDate) }} {{ order.reserveTime || '' }}</div>
        <div class="row"><span>地址</span>{{ order.address || '-' }}</div>

        <div class="footer">
          <div class="price">¥{{ money(order.totalAmount) }}</div>
          <div class="actions">
            <el-button link type="primary" @click="$router.push(`/cooker/order/${order.id}`)">详情</el-button>
            <el-button v-if="order.status === 2" type="success" @click="completeService(order)">完成服务</el-button>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, updateOrderStatus } from '@/api/cooker'
import { formatDate, money, normalizeOrders, statusTagType, statusText } from '@/utils/cookerOrder'
import { parseChefId } from '@/utils/cookerSession'

const router = useRouter()
const chefId = ref(null)
const orders = ref([])

const stats = computed(() => {
  // 状态 2 = 服务中，状态 3 = 已完成
  const serving = orders.value.filter((item) => item.status === 2).length
  return {
    pending: 0,
    serving,
    total: orders.value.length
  }
})

async function loadOrders() {
  try {
    const res = await getOrders(chefId.value)
    if (res.code !== 200) {
      ElMessage.error(res.message || '获取订单失败')
      return
    }
    
    const allOrders = normalizeOrders(Array.isArray(res.data) ? res.data : [])
    console.log('[Serving] 后端返回的所有订单:', allOrders.map(o => ({ 
      id: o.id, 
      orderNo: o.orderNo, 
      status: o.status,
      statusText: statusText(o.status)
    })))
    
    // 只保留状态为 2（服务中）的订单
    orders.value = allOrders.filter((item) => item.status === 2)
    
    console.log('[Serving] 过滤后的服务中订单:', orders.value.map(o => ({
      id: o.id,
      orderNo: o.orderNo,
      status: o.status
    })))
    console.log('[Serving] 服务中订单数量:', orders.value.length)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '获取订单失败')
  }
}

/**
 * 解析预约时间字符串为 Date 对象
 */
function parseReserveTime(order) {
  if (!order.reserveDate) return null
  
  const dateStr = formatDate(order.reserveDate)
  if (!order.reserveTime) return new Date(dateStr)
  
  // 解析时间格式 "14:00-16:00" 或 "14:00"
  const timeMatch = order.reserveTime.match(/(\d{1,2}):(\d{2})/)
  if (!timeMatch) return new Date(dateStr)
  
  const [, hour, minute] = timeMatch
  const date = new Date(dateStr)
  date.setHours(parseInt(hour), parseInt(minute), 0, 0)
  return date
}

/**
 * 完成服务（状态 2 -> 4）
 */
function completeService(order) {
  const reserveTime = parseReserveTime(order)
  const now = new Date()

  // 如果还没到预约时间，显示二次确认
  if (reserveTime && now < reserveTime) {
    const timeStr = reserveTime.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })

    ElMessageBox.confirm(
      `预约时间为 ${timeStr}，当前还未到预约时间，是否确认完成服务？`,
      '时间校验提示',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      await updateOrderStatusAndReload(order.id, 4)
    }).catch(() => {
      // 用户取消，不做任何操作
    })
  } else {
    // 已到时间或无预约时间，直接确认
    ElMessageBox.confirm('确认完成服务？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'success'
    }).then(async () => {
      await updateOrderStatusAndReload(order.id, 4)
    }).catch(() => {
      // 用户取消
    })
  }
}

/**
 * 更新订单状态并重新加载
 */
async function updateOrderStatusAndReload(orderId, status) {
  try {
    const res = await updateOrderStatus(orderId, status)
    if (res.code !== 200) {
      ElMessage.error(res.message || '更新订单状态失败')
      return
    }
    ElMessage.success('已完成服务')
    await loadOrders()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '操作失败')
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
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 14px;
}

.stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat span {
  color: #738096;
}

.stat strong {
  font-size: 26px;
  color: #213247;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 12px;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.row {
  margin: 6px 0;
  color: #536179;
  line-height: 1.5;
}

.row span {
  color: #758199;
  margin-right: 8px;
}

.footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 8px;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
