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

        <!-- 订单列表 -->
        <div class="orders-area" v-if="orders.length > 0">
            <el-card v-for="order in orders" :key="order.orderid" class="order-card" shadow="hover">
                <div class="order-header">
                    <div class="order-info-left">
                        <span class="order-no">订单编号：{{ order.orderNo }}</span>
                        <span class="order-time">{{ formatTime(order.createTime) }}</span>
                    </div>
                    <el-tag :type="getStatusType(order.status)" size="large">
                        {{ getStatusName(order.status) }}
                    </el-tag>
                </div>

                <div class="order-body">
                    <div class="info-row">
                        <span class="info-label">客户姓名：</span>
                        <span class="info-value">{{ order.username }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">联系电话：</span>
                        <span class="info-value">{{ order.userphone }}</span>
                        <el-button link type="primary" @click="handleCall(order.userphone)">
                            <el-icon>
                                <Phone />
                            </el-icon>
                        </el-button>
                    </div>
                    <div class="info-row">
                        <span class="info-label">服务时间：</span>
                        <span class="info-value">{{ order.servetime }}</span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">服务地址：</span>
                        <span class="info-value">{{ order.serveaddress }}</span>
                    </div>
                    <div class="info-row" v-if="order.requirement">
                        <span class="info-label">客户要求：</span>
                        <span class="info-value requirement">{{ order.requirement }}</span>
                    </div>
                </div>

                <div class="order-footer">
                    <div class="order-total">
                        <span>订单金额：</span>
                        <span class="total-price">¥{{ order.totalprice }}</span>
                    </div>
                    <div class="order-actions">
                        <el-button v-if="order.status === 'accepted'" type="primary" @click="handleStartService(order)">
                            <el-icon>
                                <VideoPlay />
                            </el-icon>
                            开始服务
                        </el-button>
                        <el-button v-if="order.status === 'serving'" type="success" @click="handleCompleteService(order)">
                            <el-icon>
                                <CircleCheck />
                            </el-icon>
                            完成服务
                        </el-button>
                        <el-button @click="handleViewDetail(order)">
                            <el-icon>
                                <Document />
                            </el-icon>
                            查看详情
                        </el-button>
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 空状态 -->
        <el-empty v-else description="暂无待服务订单" />

        <!-- 订单详情对话框 -->
        <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
            <div v-if="currentOrder" class="order-detail">
                <el-descriptions title="订单信息" :column="2" border>
                    <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
                    <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
                    <el-descriptions-item label="客户姓名">{{ currentOrder.username }}</el-descriptions-item>
                    <el-descriptions-item label="联系电话">{{ currentOrder.userphone }}</el-descriptions-item>
                    <el-descriptions-item label="服务时间" :span="2">{{ currentOrder.servetime }}</el-descriptions-item>
                    <el-descriptions-item label="服务地址" :span="2">{{ currentOrder.serveaddress }}</el-descriptions-item>
                    <el-descriptions-item label="客户要求" :span="2">{{ currentOrder.requirement || '无' }}</el-descriptions-item>
                </el-descriptions>

                <div class="order-total-detail">
                    <span>订单金额：</span>
                    <span class="total-price">¥{{ currentOrder.totalprice }}</span>
                </div>

                <div class="dialog-actions">
                    <el-button v-if="currentOrder.status === 'accepted'" type="primary" @click="handleStartService(currentOrder)">
                        开始服务
                    </el-button>
                    <el-button v-if="currentOrder.status === 'serving'" type="success" @click="handleCompleteService(currentOrder)">
                        完成服务
                    </el-button>
                    <el-button @click="detailDialogVisible = false">关闭</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {
    Clock, Timer, Calendar, Money, Phone, Document, VideoPlay, CircleCheck
} from '@element-plus/icons-vue'
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
}
</script>

<style scoped>
.container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
}

h3 {
    margin: 20px 0;
    text-align: center;
    color: var(--color-text-primary);
    font-size: 20px;
    font-weight: 600;
}

/* 统计卡片 */
.stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;
}

.stat-card {
    border-radius: 8px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
}

.stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
}

.stat-icon.pending {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.servicing {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.today {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-icon.revenue {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
    flex: 1;
}

.stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
}

/* 订单列表 */
.orders-area {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.order-card {
    border-radius: 8px;
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
}

.order-info-left {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.order-no {
    font-size: 14px;
    color: #606266;
}

.order-time {
    font-size: 12px;
    color: #909399;
}

.order-body {
    margin-bottom: 16px;
}

.info-row {
    display: flex;
    align-items: flex-start;
    margin-bottom: 8px;
    font-size: 14px;
}

.info-row:last-child {
    margin-bottom: 0;
}

.info-label {
    color: #909399;
    min-width: 80px;
}

.info-value {
    color: #606266;
    flex: 1;
}

.info-value.requirement {
    background-color: #f5f7fa;
    padding: 8px 12px;
    border-radius: 4px;
    display: block;
}

.order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
}

.order-total {
    font-size: 14px;
    color: #606266;
}

.total-price {
    font-size: 20px;
    color: #f56c6c;
    font-weight: bold;
    margin-left: 8px;
}

.order-actions {
    display: flex;
    gap: 8px;
}

/* 订单详情 */
.order-total-detail {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
    font-size: 16px;
}

.order-total-detail .total-price {
    font-size: 24px;
    color: #f56c6c;
    font-weight: bold;
    margin-left: 16px;
}

.dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
}
</style>
