<template>
    <div class="container">
        <h3>服务订单</h3>

        <!-- 订单统计 -->
        <div class="stats-cards">
            <el-card shadow="hover" class="stat-card">
                <div class="stat-item">
                    <div class="stat-icon pending">
                        <el-icon :size="28">
                            <Clock />
                        </el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ stats.pending }}</div>
                        <div class="stat-label">待服务</div>
                    </div>
                </div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
                <div class="stat-item">
                    <div class="stat-icon servicing">
                        <el-icon :size="28">
                            <Timer />
                        </el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ stats.servicing }}</div>
                        <div class="stat-label">服务中</div>
                    </div>
                </div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
                <div class="stat-item">
                    <div class="stat-icon today">
                        <el-icon :size="28">
                            <Calendar />
                        </el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ stats.today }}</div>
                        <div class="stat-label">今日订单</div>
                    </div>
                </div>
            </el-card>
            <el-card shadow="hover" class="stat-card">
                <div class="stat-item">
                    <div class="stat-icon revenue">
                        <el-icon :size="28">
                            <Money />
                        </el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">¥{{ stats.todayRevenue.toFixed(2) }}</div>
                        <div class="stat-label">今日收入</div>
                    </div>
                </div>
            </el-card>
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
import { getServingOrders, startService, endService, updateOrderStatus, getOrders } from '@/api/cooker'

export default {
    name: 'CookerServing',
    components: {
        Clock,
        Timer,
        Calendar,
        Money,
        Phone,
        Document,
        VideoPlay,
        CircleCheck
    },
    data() {
        return {
            orders: [],
            detailDialogVisible: false,
            currentOrder: null
        }
    },
    computed: {
        stats() {
            const pendingOrders = this.orders.filter(o => o.status === 'accepted')
            const servicingOrders = this.orders.filter(o => o.status === 'serving')
            const today = new Date().toDateString()
            const todayOrders = this.orders.filter(o => new Date(o.createTime).toDateString() === today)
            const todayRevenue = todayOrders
                .filter(o => o.status === 'completed')
                .reduce((sum, o) => sum + parseFloat(o.totalprice || 0), 0)

            return {
                pending: pendingOrders.length,
                servicing: servicingOrders.length,
                today: todayOrders.length,
                todayRevenue: todayRevenue
            }
        }
    },
    methods: {
        async loadOrders() {
            try {
                // 从后端API获取订单数据
                const chefId = localStorage.getItem('userId')
                console.log('[调试] 厨师ID:', chefId)
                
                if (!chefId) {
                    ElMessage.warning('未找到厨师ID，请重新登录')
                    return
                }
                
                console.log('[调试] 开始调用获取订单API...')
                const res = await getOrders(chefId)
                console.log('[调试] API返回完整数据:', res)
                
                // 直接处理返回的数据
                if (res.records && res.records.length > 0) {
                    console.log('[调试] 订单记录:', res.records)
                    // 转换后端数据格式为前端需要的格式
                    this.orders = res.records.map(order => ({
                        orderid: order.id,
                        orderNo: order.orderNo,
                        username: order.customerId || '',
                        userphone: order.customerId || '',
                        servetime: `${order.reserveDate} ${order.reserveTime}`,
                        serveaddress: order.addressId || '',
                        requirement: order.dishRequirements || '',
                        totalprice: order.totalAmount || 0,
                        status: this.convertStatus(order.status),
                        createTime: order.createTime
                    }))
                    console.log('[调试] 转换后的订单数据:', this.orders)
                } else {
                    console.log('[调试] API返回错误:', res.message)
                    this.orders = []
                }
            } catch (error) {
                console.error('[调试] 加载订单失败:', error)
                ElMessage.error('加载订单失败')
            }
        },
        saveOrder(order) {
            // 保存订单到 localStorage
            const servingOrders = localStorage.getItem('servingOrders')
            let orders = servingOrders ? JSON.parse(servingOrders) : []
            orders.push(order)
            localStorage.setItem('servingOrders', JSON.stringify(orders))
        },
        convertStatus(status) {
            // 将后端状态转换为前端状态
            const statusMap = {
                2: 'accepted',    // 已接单
                3: 'serving',     // 服务中
                4: 'completed',   // 已完成
                5: 'cancelled'    // 已取消
            }
            return statusMap[status] || 'accepted'
        },
        getStatusType(status) {
            const typeMap = {
                accepted: 'warning',
                serving: 'primary',
                completed: 'success',
                cancelled: 'info'
            }
            return typeMap[status] || ''
        },
        getStatusName(status) {
            const nameMap = {
                accepted: '待服务',
                serving: '服务中',
                completed: '已完成',
                cancelled: '已取消'
            }
            return nameMap[status] || ''
        },
        formatTime(time) {
            if (!time) return ''
            const date = new Date(time)
            const now = new Date()
            const diff = now - date

            if (diff < 60000) return '刚刚'
            if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
            if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
            if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

            return date.toLocaleDateString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            })
        },
        handleCall(phone) {
            ElMessageBox.confirm(`拨打客户电话：${phone}？`, '提示', {
                confirmButtonText: '拨打',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                window.location.href = `tel:${phone}`
            }).catch(() => { })
        },
        async handleStartService(order) {
            try {
                await ElMessageBox.confirm(`确认订单：${order.orderNo}？`, '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'info'
                })

                // 调用更新订单状态 API，设置状态为2（服务中）
                await updateOrderStatus(order.orderid, 1)
                
                // 更新订单状态
                order.status = 'serving'
                this.updateOrders()
                
                ElMessage.success('服务已完成')
                this.detailDialogVisible = false
            } catch (error) {
               console.error('失败:', error)
            }
        },
        async handleCompleteService(order) {
            try {
                await ElMessageBox.confirm(`确认完成服务订单：${order.orderNo}？`, '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                })

                // 调用更新订单状态 API，设置状态为3
                await updateOrderStatus(order.orderid, 3)
                
                // 从列表中移除已完成的订单
                this.orders = this.orders.filter(o => o.orderid !== order.orderid)
                this.updateOrders()
                
                ElMessage.success('服务已完成')
                this.detailDialogVisible = false
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('完成服务失败:', error)
                    ElMessage.error('完成服务失败')
                }
            }
        },
        updateOrders() {
            // 更新 localStorage 中的订单列表
            localStorage.setItem('servingOrders', JSON.stringify(this.orders))
        },
        handleViewDetail(order) {
            this.currentOrder = order
            this.detailDialogVisible = true
        }
    },
    async mounted() {
        await this.loadOrders()
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
