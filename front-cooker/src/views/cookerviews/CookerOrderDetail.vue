<template>
  <div class="page">
    <el-page-header @back="$router.back()" content="订单详情" />

    <el-empty v-if="!order && !loading" description="订单不存在" />

    <template v-else>
      <el-card shadow="hover" v-loading="loading">
        <template #header>
          <div class="head">
            <div>
              <strong>{{ order?.orderNo || '-' }}</strong>
              <p>{{ order?.orderType || '-' }} · {{ formatDate(order?.reserveDate) }} {{ order?.reserveTime || '' }}</p>
            </div>
            <el-tag :type="statusTagType(order?.status)">{{ statusText(order?.status) }}</el-tag>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="客户姓名">{{ order?.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ order?.customerPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务地址" :span="2">{{ order?.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地图定位" :span="2">{{ selectedLocation ? locationToText(selectedLocation) : '未选择' }}</el-descriptions-item>
          <el-descriptions-item label="预算">¥{{ money(order?.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="预计收入">¥{{ money(order?.estimatedIncome) }}</el-descriptions-item>
          <el-descriptions-item label="客户评分">{{ order?.customerRating?.toFixed(1) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="信用值">{{ order?.customerCredit || '-' }}</el-descriptions-item>
          <el-descriptions-item label="菜品要求" :span="2">{{ order?.dishRequirements || '无' }}</el-descriptions-item>
          <el-descriptions-item label="饮食禁忌" :span="2">{{ order?.taboos?.join(' / ') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="厨具情况" :span="2">{{ order?.utensils?.join(' / ') || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="hover">
        <template #header>准备清单</template>
        <el-timeline>
          <el-timeline-item v-for="(item, index) in prepList" :key="`${item}-${index}`" :timestamp="`步骤 ${index + 1}`">
            {{ item }}
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card shadow="hover">
        <template #header>操作</template>
        <div class="actions">
          <el-button type="primary" @click="callCustomer">联系客户</el-button>
          <el-button type="warning" @click="openLocationPicker">选取定位</el-button>
          <el-button @click="openNavigation">导航</el-button>
          <el-button v-if="order?.status === 0" type="success" :loading="submitLoading" @click="acceptCurrent">接单</el-button>
          <el-button v-if="order?.status === 0" type="danger" plain @click="openReject">拒单</el-button>
          <el-button v-if="order?.status === 2" type="primary" :loading="submitLoading" @click="changeStatus(3)">开始服务</el-button>
          <el-button v-if="order?.status === 3" type="success" :loading="submitLoading" @click="changeStatus(4)">完成服务</el-button>
        </div>
      </el-card>
    </template>

    <BaiduMapPickerDialog
      v-model="locationDialogVisible"
      title="地图选点（订单地址）"
      :initial-location="detailLocationInitial"
      @confirm="handleLocationConfirm"
    />

    <BaiduRoutePlannerDialog
      v-model="routeDialogVisible"
      :title="routeDialogTitle"
      :start-location="routeStartLocation"
      :end-location="routeEndLocation"
    />

    <el-dialog v-model="rejectVisible" width="520px" title="拒单原因">
      <el-form label-width="92px">
        <el-form-item label="订单号">
          <strong>{{ order?.orderNo || '-' }}</strong>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BaiduMapPickerDialog from '@/components/cooker/BaiduMapPickerDialog.vue'
import BaiduRoutePlannerDialog from '@/components/cooker/BaiduRoutePlannerDialog.vue'
import { acceptOrder, getChefOrderPool, updateOrderStatus } from '@/api/cooker'
import { buildPrepList, formatDate, money, normalizeOrders, statusTagType, statusText, REJECT_REASON_OPTIONS } from '@/utils/cookerOrder'
import { parseChefId } from '@/utils/cookerSession'
import { getChefLocation, getOrderLocation, locationToText, openBaiduNavigation, setOrderLocation } from '@/utils/location'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const order = ref(null)
const chefId = ref(null)
const locationDialogVisible = ref(false)
const selectedLocation = ref(null)
const routeDialogVisible = ref(false)
const routeStartLocation = ref({ lng: 0, lat: 0, address: '' })
const routeEndLocation = ref({ lng: 0, lat: 0, address: '' })
const routeDialogTitle = ref('路线规划')
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectRemark = ref('')

const prepList = computed(() => (order.value ? buildPrepList(order.value) : []))
const detailLocationInitial = computed(() => {
  if (!order.value) {
    return { lng: 0, lat: 0, address: '' }
  }
  return {
    ...(selectedLocation.value || { lng: 0, lat: 0 }),
    address: (selectedLocation.value && selectedLocation.value.address) || order.value.address || ''
  }
})

async function loadDetail() {
  loading.value = true
  try {
    const pool = await getChefOrderPool(chefId.value)
    const allOrders = normalizeOrders([...pool.newOrders, ...pool.servingOrders, ...pool.historyOrders])
    const targetId = Number(route.params.id)
    order.value = allOrders.find((item) => Number(item.id) === targetId) || null
    selectedLocation.value = order.value ? getOrderLocation(order.value.id) : null
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载订单详情失败')
  } finally {
    loading.value = false
  }
}

function callCustomer() {
  if (!order.value?.customerPhone) {
    ElMessage.warning('客户手机号为空')
    return
  }
  window.location.href = `tel:${order.value.customerPhone}`
}

function openNavigation() {
  if (!order.value) return

  if (selectedLocation.value && Number(selectedLocation.value.lng) !== 0 && Number(selectedLocation.value.lat) !== 0) {
    const startLocation = getChefLocation()
    const hasStartPoint = startLocation && Number(startLocation.lng) !== 0 && Number(startLocation.lat) !== 0

    if (hasStartPoint) {
      routeStartLocation.value = {
        lng: Number(startLocation.lng),
        lat: Number(startLocation.lat),
        address: startLocation.address || '我的位置'
      }
      routeEndLocation.value = {
        lng: Number(selectedLocation.value.lng),
        lat: Number(selectedLocation.value.lat),
        address: selectedLocation.value.address || order.value.address || ''
      }
      routeDialogTitle.value = `路线规划（${order.value.orderNo || '当前订单'}）`
      routeDialogVisible.value = true
      return
    }
  }

  try {
    const result = openBaiduNavigation({
      location: selectedLocation.value,
      address: order.value?.address || '',
      title: `订单 ${order.value?.orderNo || ''}`
    })
    if (result?.mode !== 'route') {
      ElMessage.info('起点位置未找到，已打开目的地位置页面')
    }
  } catch (error) {
    ElMessage.warning('地址缺失，请先选取地图定位点')
  }
}

function openLocationPicker() {
  if (!order.value) return
  locationDialogVisible.value = true
}

function handleLocationConfirm(location) {
  if (!order.value) return
  selectedLocation.value = setOrderLocation(order.value.id, location)
  if (location.address) {
    order.value.address = location.address
  }
  ElMessage.success('订单定位已保存，待接单页面可复用')
}

async function acceptCurrent() {
  if (!order.value) return
  submitLoading.value = true
  try {
    const res = await acceptOrder(order.value.id, chefId.value)
    if (res.code !== 200) {
      ElMessage.error(res.message || '接单失败')
      return
    }
    ElMessage.success('接单成功')
    await loadDetail()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '接单失败')
  } finally {
    submitLoading.value = false
  }
}

async function changeStatus(status) {
  if (!order.value) return
  submitLoading.value = true
  try {
    const res = await updateOrderStatus(order.value.id, status)
    if (res.code !== 200) {
      ElMessage.error(res.message || '状态更新失败')
      return
    }
    ElMessage.success('状态已更新')
    await loadDetail()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '状态更新失败')
  } finally {
    submitLoading.value = false
  }
}

function openReject() {
  if (!order.value) return
  rejectReason.value = ''
  rejectRemark.value = ''
  rejectVisible.value = true
}

function confirmReject() {
  if (!order.value) return
  if (!rejectReason.value) {
    ElMessage.warning('请选择拒单原因')
    return
  }

  const rejectLogRaw = localStorage.getItem('cooker_reject_log_v1')
  const rejectLog = rejectLogRaw ? JSON.parse(rejectLogRaw) : []
  rejectLog.unshift({
    orderId: order.value.id,
    orderNo: order.value.orderNo,
    reason: rejectReason.value,
    remark: rejectRemark.value,
    time: new Date().toISOString()
  })
  localStorage.setItem('cooker_reject_log_v1', JSON.stringify(rejectLog.slice(0, 30)))

  ElMessage.success('拒单原因已保存')
  rejectVisible.value = false
  
  // 从列表中移除订单
  router.push('/cooker/todo')
}

onMounted(async () => {
  chefId.value = parseChefId()
  if (!chefId.value) {
    ElMessage.warning('请先登录')
    router.push('/cooker/login')
    return
  }
  await loadDetail()
})
</script>

<style scoped>
.page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px 20px;
  display: grid;
  gap: 14px;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.head strong {
  font-size: 18px;
  color: #253548;
}

.head p {
  margin-top: 4px;
  color: #69778d;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }

  .head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
