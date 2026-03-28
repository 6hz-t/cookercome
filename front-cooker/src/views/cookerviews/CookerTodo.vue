<template>
  <div class="page">
    <el-card shadow="never" class="overview-card">
      <div class="overview-row">
        <div>
          <h3>待接单</h3>
          <p>根据服务类型、预算和客户评分快速决策</p>
        </div>
        <el-space>
          <el-tag type="warning">待接单 {{ filteredOrders.length }} 单</el-tag>
        </el-space>
      </div>

      <div class="filters">
        <el-input v-model="keyword" clearable placeholder="搜索订单号/地址/客户" class="w-280" />
        <el-select v-model="orderType" clearable placeholder="服务类型" class="w-160">
          <el-option v-for="type in ORDER_TYPE_OPTIONS" :key="type" :label="type" :value="type" />
        </el-select>
        <el-button @click="loadOrders">刷新</el-button>
      </div>
    </el-card>

    <el-skeleton :loading="loading" animated :rows="10">
      <template #default>
        <el-empty v-if="filteredOrders.length === 0" description="暂无待接订单" />

        <div v-else class="order-grid">
          <el-card v-for="order in filteredOrders" :key="order.id" class="order-card" shadow="hover">
            <div class="order-head">
              <div>
                <strong>{{ order.orderNo }}</strong>
                <div class="light">{{ formatDate(order.reserveDate) }} {{ order.reserveTime || '时间待定' }}</div>
              </div>
              <div class="head-tags">
                <el-tag type="warning">待接单</el-tag>
                <el-tag effect="plain">{{ order.orderType }}</el-tag>
              </div>
            </div>

            <div class="rows">
              <div class="row"><span>服务地址</span><p>{{ order.address || '-' }}</p></div>
              <div class="row"><span>预算</span><p>¥{{ money(order.totalAmount) }}</p></div>
              <div class="row">
                <span>客户信息</span>
                <p>
                  {{ order.customerName || '匿名客户' }}
                  评分 {{ order.customerRating.toFixed(1) }}
                  信用 {{ order.customerCredit }}
                </p>
              </div>
            </div>

            <div class="actions">
              <el-button link type="primary" @click="$router.push(`/cooker/order/${order.id}`)">详情</el-button>
              <el-button type="danger" plain @click="openReject(order)">拒单</el-button>
            </div>
          </el-card>
        </div>
      </template>
    </el-skeleton>

    <el-dialog v-model="rejectVisible" width="520px" title="拒单原因">
      <el-form label-width="92px">
        <el-form-item label="订单号">
          <strong>{{ selectedOrder?.orderNo || '-' }}</strong>
        </el-form-item>
        <el-form-item label="拒单原因" required>
          <el-select v-model="rejectReason" placeholder="请选择原因" style="width: 100%">
            <el-option v-for="item in REJECT_REASON_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input v-model="rejectRemark" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNewOrders } from '@/api/cooker'
import {
  money,
  normalizeOrders,
  ORDER_TYPE_OPTIONS,
  REJECT_REASON_OPTIONS,
  formatDate
} from '@/utils/cookerOrder'
import { parseChefId } from '@/utils/cookerSession'
import { getOrderLocation } from '@/utils/location'

const router = useRouter()

const chefId = ref(null)
const loading = ref(false)
const orders = ref([])

const keyword = ref('')
const orderType = ref('')

const rejectVisible = ref(false)
const selectedOrder = ref(null)

const rejectReason = ref('')
const rejectRemark = ref('')

const filteredOrders = computed(() => {
  return orders.value.filter((order) => {
    const text = `${order.orderNo || ''} ${order.address || ''} ${order.customerName || ''}`
    const hitKeyword = !keyword.value || text.includes(keyword.value.trim())
    const hitType = !orderType.value || order.orderType === orderType.value
    return hitKeyword && hitType
  })
})

async function loadOrders() {
  loading.value = true
  try {
    const res = await getNewOrders(chefId.value)
    if (res.code !== 200) {
      ElMessage.error(res.message || '获取待接订单失败')
      return
    }
    orders.value = normalizeOrders(Array.isArray(res.data) ? res.data : []).map((order) => ({
      ...order,
      selectedLocation: getOrderLocation(order.id)
    }))
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '获取待接订单失败')
  } finally {
    loading.value = false
  }
}

function openReject(order) {
  selectedOrder.value = order
  rejectReason.value = ''
  rejectRemark.value = ''
  rejectVisible.value = true
}

function confirmReject() {
  if (!selectedOrder.value) return
  if (!rejectReason.value) {
    ElMessage.warning('请选择拒单原因')
    return
  }

  const rejectLogRaw = localStorage.getItem('cooker_reject_log_v1')
  const rejectLog = rejectLogRaw ? JSON.parse(rejectLogRaw) : []
  rejectLog.unshift({
    orderId: selectedOrder.value.id,
    orderNo: selectedOrder.value.orderNo,
    reason: rejectReason.value,
    remark: rejectRemark.value,
    time: new Date().toISOString()
  })
  localStorage.setItem('cooker_reject_log_v1', JSON.stringify(rejectLog.slice(0, 30)))

  orders.value = orders.value.filter((item) => item.id !== selectedOrder.value.id)
  rejectVisible.value = false
  ElMessage.success('拒单原因已保存')
}

onMounted(async () => {
  chefId.value = parseChefId()
  if (!chefId.value) {
    ElMessage.warning('请先登录')
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

.overview-card {
  background: #ffffff;
}

.overview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.overview-row h3 {
  font-size: 20px;
  color: #253447;
}

.overview-row p {
  margin-top: 4px;
  color: #667384;
}

.filters {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.w-280 {
  width: 280px;
}

.w-160 {
  width: 160px;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 14px;
}

.order-card {
  border-radius: 14px;
}

.order-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}

.order-head strong {
  font-size: 16px;
  color: #27364a;
}

.light {
  margin-top: 5px;
  color: #6b7585;
  font-size: 12px;
}

.head-tags {
  display: flex;
  gap: 8px;
}

.rows {
  display: grid;
  gap: 8px;
}

.row {
  display: grid;
  grid-template-columns: 95px 1fr;
  gap: 8px;
  font-size: 13px;
}

.row span {
  color: #768094;
}

.row p {
  color: #334258;
  line-height: 1.6;
}

.inline-tag {
  margin-right: 6px;
}

.actions {
  margin-top: 14px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.preview-item {
  border: 1px solid #e9edf5;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 4px;
}

.preview-item span {
  color: #738096;
  font-size: 12px;
}

.preview-item strong {
  color: #263447;
  line-height: 1.5;
}

.preview-item.full {
  grid-column: 1 / -1;
}

@media (max-width: 992px) {
  .page {
    padding: 0 12px 14px;
  }

  .overview-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .w-280,
  .w-160 {
    width: 100%;
  }
}
</style>
