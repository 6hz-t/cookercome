<template>
  <div class="my-orders">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Document /></el-icon>
        我的订单
      </h2>
      <p class="panel-desc">查看和管理您的所有订单</p>
    </div>
    
    <!-- 订单分类标签 -->
    <div class="order-tabs">
      <el-radio-group v-model="activeCategory" size="large" @change="handleCategoryChange">
        <el-radio-button label="all">
          <el-icon><Menu /></el-icon> 全部
        </el-radio-button>
        <el-radio-button label="pending">
          <el-icon><Clock /></el-icon> 预约中
        </el-radio-button>
        <el-radio-button label="payment">
          <el-icon><Money /></el-icon> 待支付
        </el-radio-button>
        <el-radio-button label="fulfillment">
          <el-icon><Finished /></el-icon> 待履约
        </el-radio-button>
        <el-radio-button label="refunding">
          <el-icon><RefreshLeft /></el-icon> 退款中
        </el-radio-button>
        <el-radio-button label="history">
          <el-icon><Timer /></el-icon> 历史订单
        </el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="tech-loader">
        <div class="loader-orb"></div>
        <div class="loader-orb loader-orb-2"></div>
        <div class="loader-orb loader-orb-3"></div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="empty-icon">
        <el-icon :size="80" color="#667eea"><Document /></el-icon>
      </div>
      <div class="empty-text">
        <h3>暂无订单</h3>
        <p>当前分类下还没有订单哦~</p>
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="getOrderStatusType(order.status)" size="small">
            {{ getOrderStatusText(order.status) }}
          </el-tag>
        </div>
        
        <div class="order-content">
          <el-avatar :size="70" :src="order.chefAvatar || defaultAvatar" shape="circle" />
          <div class="order-info">
            <h4>{{ order.chefName || '未知厨师' }}</h4>
            <p class="chef-level" v-if="order.chefLevel">{{ getChefLevelText(order.chefLevel) }}</p>
            <p class="reserve-time">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(order.reserveDate) }} {{ order.reserveTime }}
            </p>
            <p v-if="order.dishRequirements" class="dish-req">
              <el-icon><Food /></el-icon> {{ order.dishRequirements }}
            </p>
          </div>
          <div class="order-amount">
            <span class="amount-label">订单金额</span>
            <span class="amount-value">¥{{ order.totalAmount }}</span>
          </div>
        </div>
        
        <div class="order-footer">
          <span class="create-time">创建时间：{{ formatDateTime(order.createTime) }}</span>
          <div class="order-actions">
            <!-- 预约中订单（status=0）：显示取消订单 -->
            <el-button 
              v-if="order.status === 0" 
              size="small" 
              type="danger" 
              plain
              @click="cancelOrder(order)">
              <el-icon><Close /></el-icon> 取消订单
            </el-button>
            
            <!-- 待支付订单（status=1）：显示取消和支付 -->
            <template v-if="order.status === 1">
              <el-button 
                size="small" 
                type="danger" 
                plain
                @click="cancelOrder(order)">
                <el-icon><Close /></el-icon> 取消订单
              </el-button>
              <el-button 
                size="small" 
                type="success" 
                @click="payOrder(order)">
                <el-icon><Money /></el-icon> 立即支付
              </el-button>
            </template>
            
            <!-- 待履约订单（status=2）：显示申请退款 -->
            <el-button 
              v-if="order.status === 2" 
              size="small" 
              type="warning" 
              plain
              @click="showRefundDialog(order)">
              <el-icon><RefreshLeft /></el-icon> 申请退款
            </el-button>
            
            <!-- 服务完成订单（status=3）：显示收藏按钮或已收藏标签 -->
            <template v-if="order.status === 3">
              <!-- 已收藏：显示标签 -->
              <el-tag 
                v-if="order.isFavorited" 
                size="small" 
                type="primary"
                effect="dark"
                round
                class="favorited-tag">
                <el-icon><Star /></el-icon>
                <span style="margin-left: 4px;">已收藏</span>
              </el-tag>
              <!-- 未收藏：显示收藏按钮 -->
              <el-button 
                v-else
                size="small" 
                type="primary" 
                plain
                @click="showFavoriteConfirm(order)"
                :loading="favoriting && currentFavoritingOrderId === order.id">
                <el-icon><Star /></el-icon> 收藏厨师
              </el-button>
            </template>
            
            <!-- 退款中订单（status=5）：显示退款进度提示 -->
            <el-tag 
              v-if="order.status === 5" 
              size="small" 
              type="warning"
              effect="dark">
              退款审核中
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 退款对话框（自定义） -->
    <transition name="dialog-fade">
      <div v-if="refundDialogVisible" class="custom-dialog-overlay" @click="closeRefundDialog">
        <div class="custom-dialog refund-custom-dialog" @click.stop>
          <div class="dialog-header refund-header">
            <div class="header-content">
              <div class="icon-wrapper-small">
                <el-icon><RefreshLeft /></el-icon>
              </div>
              <div class="title-text">
                <h3 class="dialog-title">申请退款</h3>
                <p class="dialog-subtitle">填写退款原因，我们将尽快处理</p>
              </div>
            </div>
            <button class="dialog-close" @click="closeRefundDialog">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          
          <div class="dialog-body">
            <!-- 订单信息卡片 -->
            <div class="order-info-card-compact">
              <div class="info-grid-compact">
                <div class="info-row-compact">
                  <span class="info-label-compact">
                    <el-icon><Menu /></el-icon>
                    订单编号
                  </span>
                  <span class="info-value-compact order-no">{{ currentOrder.orderNo }}</span>
                </div>
                <div class="info-row-compact highlight">
                  <span class="info-label-compact">
                    <el-icon><Money /></el-icon>
                    订单金额
                  </span>
                  <span class="info-value-compact amount">¥{{ currentOrder.totalAmount }}</span>
                </div>
                <div class="info-row-compact">
                  <span class="info-label-compact">
                    <el-icon><Food /></el-icon>
                    厨师姓名
                  </span>
                  <span class="info-value-compact">{{ currentOrder.chefName || '未知厨师' }}</span>
                </div>
                <div class="info-row-compact">
                  <span class="info-label-compact">
                    <el-icon><Calendar /></el-icon>
                    预约时间
                  </span>
                  <span class="info-value-compact">
                    {{ formatDate(currentOrder.reserveDate) }} {{ currentOrder.reserveTime }}
                  </span>
                </div>
              </div>
            </div>
            
            <!-- 退款原因输入 -->
            <div class="reason-section-compact">
              <label class="reason-label-compact">
                <div class="label-left-compact">
                  <el-icon><Edit /></el-icon>
                  <span>退款原因</span>
                </div>
                <span class="required-mark-compact">*</span>
              </label>
              <div class="textarea-wrapper-compact">
                <textarea
                  v-model="refundForm.reason"
                  class="reason-textarea-compact"
                  placeholder="请描述退款原因，如：时间安排冲突、不需要服务等..."
                  rows="4"
                ></textarea>
                <div class="char-count-compact">
                  {{ refundForm.reason.length }}/200
                </div>
              </div>
            </div>
            
            <!-- 温馨提示 -->
            <div class="tips-box-compact">
              <div class="tips-icon-compact">
                <el-icon><WarningFilled /></el-icon>
              </div>
              <div class="tips-content-compact">
                <p class="tips-title-compact">温馨提示</p>
                <ul class="tips-list-compact">
                  <li>退款申请提交后，客服将在 24 小时内审核</li>
                  <li>审核通过后，款项将原路返回至支付账户</li>
                </ul>
              </div>
            </div>
          </div>
          
          <div class="dialog-footer">
            <button class="btn btn-cancel" @click="closeRefundDialog">
              <el-icon><Close /></el-icon>
              取消
            </button>
            <button class="btn btn-submit" @click="submitRefund" :disabled="submitting || !refundForm.reason.trim()">
              <el-icon v-if="!submitting"><CircleCheck /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
              {{ submitting ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 收藏确认对话框（自定义） -->
    <transition name="dialog-fade">
      <div v-if="favoriteDialogVisible" class="custom-dialog-overlay" @click="closeFavoriteDialog">
        <div class="custom-dialog favorite-custom-dialog" @click.stop>
          <div class="dialog-header">
            <h3 class="dialog-title">
              <el-icon><Star /></el-icon>
              收藏厨师
            </h3>
            <button class="dialog-close" @click="closeFavoriteDialog">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          <div class="dialog-body">
            <div class="favorite-chef-card">
              <img 
                :src="currentOrder.chefAvatar || defaultAvatar" 
                :alt="currentOrder.chefName"
                class="chef-avatar-img"
              />
              <div class="chef-info-content">
                <h4 class="chef-name">{{ currentOrder.chefName }}</h4>
                <p class="chef-level-text" v-if="currentOrder.chefLevel">
                  {{ getChefLevelText(currentOrder.chefLevel) }}
                </p>
              </div>
            </div>
            <div class="favorite-message-box">
              <el-icon class="message-icon"><InfoFilled /></el-icon>
              <p>确定要收藏这位厨师吗？收藏后可以在个人中心快速查看。</p>
            </div>
          </div>
          <div class="dialog-footer">
            <button class="btn btn-cancel" @click="closeFavoriteDialog">取消</button>
            <button class="btn btn-confirm" @click="confirmFavorite" :disabled="favoriting">
              <el-icon v-if="!favoriting"><Star /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
              {{ favoriting ? '收藏中...' : '确定收藏' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Menu, Clock, Money, Finished, Timer, Calendar, Food, Close, RefreshLeft, Star, InfoFilled, Loading, Edit, WarningFilled, CircleCheck } from '@element-plus/icons-vue'
import { getUserOrders, actionOrder } from '@/api/chef'
import { addFavorite } from '@/api/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 当前选中的分类
const activeCategory = ref('all')
const orders = ref([])
const loading = ref(false)

// 退款相关
const refundDialogVisible = ref(false)
const submitting = ref(false)
const currentOrder = ref({})
const refundForm = ref({
  orderId: 0,
  reason: ''
})

// 收藏相关
const favoriteDialogVisible = ref(false)
const favoriting = ref(false)
const currentFavoritingOrderId = ref(null)

// 获取厨师等级文字
const getChefLevelText = (level) => {
  const levelMap = {
    1: '初级厨师',
    2: '中级厨师',
    3: '高级厨师',
    4: '资深厨师',
    5: '特级厨师'
  }
  return levelMap[level] || ''
}

// 获取订单状态类型
const getOrderStatusType = (status) => {
  const typeMap = {
    0: 'info',      // 订单提交
    1: 'warning',   // 待支付
    2: 'success',   // 已支付
    3: 'success',   // 服务完成
    4: 'danger',    // 订单取消
    5: 'warning',   // 退款中
    6: 'info'       // 已退款
  }
  return typeMap[status] || 'info'
}

// 获取订单状态文字
const getOrderStatusText = (status) => {
  const textMap = {
    0: '订单提交',
    1: '待支付',
    2: '已支付',
    3: '服务完成',
    4: '订单取消',
    5: '退款中',
    6: '已退款'
  }
  return textMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 加载订单数据
const loadOrders = async (category) => {
  try {
    loading.value = true
    const res = await getUserOrders(category)
    if (res.code === 200 && res.data) {
      orders.value = res.data
    } else {
      orders.value = []
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    orders.value = []
  } finally {
    loading.value = false
  }
}

// 处理分类变化
const handleCategoryChange = () => {
  loadOrders(activeCategory.value)
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 "${order.orderNo}" 吗？`,
      '取消订单确认',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )
    
    const res = await actionOrder({
      orderId: order.id,
      actionType: 'cancel',
      reason: '用户主动取消'
    })
    
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      // 重新加载订单列表
      loadOrders(activeCategory.value)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 支付订单
const payOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认支付订单 "${order.orderNo}" 吗？\n支付金额：¥${order.totalAmount}\n（支付后将获得 ${order.totalAmount} 积分）`,
      '支付确认',
      {
        confirmButtonText: '立即支付',
        cancelButtonText: '再想想',
        type: 'success'
      }
    )
    
    const res = await actionOrder({
      orderId: order.id,
      actionType: 'pay'
    })
    
    if (res.code === 200) {
      ElMessage.success(`订单已支付成功！获得 ${order.totalAmount} 积分`)
      // 重新加载订单列表
      loadOrders(activeCategory.value)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付订单失败:', error)
      ElMessage.error('支付订单失败')
    }
  }
}

// 显示退款对话框
const showRefundDialog = (order) => {
  currentOrder.value = order
  refundForm.value = {
    orderId: order.id,
    reason: ''
  }
  refundDialogVisible.value = true
}

// 关闭退款对话框
const closeRefundDialog = () => {
  if (!submitting.value) {
    refundDialogVisible.value = false
  }
}

// 提交退款申请
const submitRefund = async () => {
  if (!refundForm.value.reason.trim()) {
    ElMessage.warning('请输入退款原因')
    return
  }
  
  try {
    submitting.value = true
    const res = await actionOrder({
      orderId: refundForm.value.orderId,
      actionType: 'refund',
      reason: refundForm.value.reason
    })
    
    if (res.code === 200) {
      ElMessage.success('退款申请已提交，等待审核')
      refundDialogVisible.value = false
      // 重新加载订单列表
      loadOrders(activeCategory.value)
    }
  } catch (error) {
    console.error('提交退款失败:', error)
    ElMessage.error(error.message || '提交退款失败')
  } finally {
    submitting.value = false
  }
}

// 显示收藏确认对话框
const showFavoriteConfirm = (order) => {
  currentOrder.value = order
  currentFavoritingOrderId.value = order.id
  favoriteDialogVisible.value = true
}

// 关闭收藏对话框
const closeFavoriteDialog = () => {
  if (!favoriting.value) {
    favoriteDialogVisible.value = false
  }
}

// 确认收藏
const confirmFavorite = async () => {
  try {
    favoriting.value = true
    await addFavorite(currentOrder.value.chefId)
    
    ElMessage.success('收藏成功')
    favoriteDialogVisible.value = false
    // 可选：重新加载订单列表（如果需要更新收藏状态）
    // loadOrders(activeCategory.value)
  } catch (error) {
    console.error('收藏失败:', error)
    // 不再显示错误消息，全局拦截器已经处理
    // ElMessage.error(error.message || '收藏失败')
  } finally {
    favoriting.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadOrders('all')
})
</script>

<style scoped>
.my-orders {
  min-height: 600px;
}

/* 面板头部 */
.panel-header {
  margin-bottom: 30px;
  text-align: center;
}

.panel-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.panel-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 订单分类标签 */
.order-tabs {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
}

.order-tabs :deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.order-tabs :deep(.el-radio-button__inner) {
  background: rgba(36, 43, 61, 0.6);
  border-color: rgba(102, 126, 234, 0.3);
  color: var(--text-secondary);
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.order-tabs :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: #667eea;
  color: #fff;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.order-tabs :deep(.el-radio-button:focus-visible) .el-radio-button__inner {
  outline: none;
}

/* 加载状态 */
.loading-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tech-loader {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.loader-orb {
  position: absolute;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  opacity: 0.6;
  filter: blur(20px);
  animation: orbFloat 3s ease-in-out infinite;
}

.loader-orb-2 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  animation-delay: 1s;
}

.loader-orb-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  animation-delay: 2s;
}

@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.4;
  }
  25% {
    transform: translate(30px, -30px) scale(1.1);
    opacity: 0.6;
  }
  50% {
    transform: translate(-20px, 20px) scale(0.9);
    opacity: 0.3;
  }
  75% {
    transform: translate(20px, 30px) scale(1.05);
    opacity: 0.5;
  }
}

/* 空状态 */
.empty-state {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.empty-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  display: flex;
  align-items: center;
  justify-content: center;
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-text h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.empty-text p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 订单卡片 */
.order-card {
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8));
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
}

.order-card:hover {
  border-color: rgba(102, 126, 234, 0.5);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  margin-bottom: 20px;
}

.order-no {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 500;
}

.order-content {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.order-info {
  flex: 1;
  min-width: 0;
}

.order-info h4 {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.chef-level {
  font-size: 13px;
  color: #667eea;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.reserve-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 8px 0;
}

.dish-req {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-muted);
  margin: 0;
}

.order-amount {
  text-align: right;
  padding-left: 20px;
}

.amount-label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.amount-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #f56c6c, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
}

.create-time {
  font-size: 12px;
  color: var(--text-muted);
}

.order-actions {
  display: flex;
  gap: 10px;
}

.order-actions :deep(.el-button) {
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 6px;
}

/* 已收藏标签样式 */
.order-actions .el-tag--primary {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2)) !important;
  border-color: rgba(102, 126, 234, 0.5) !important;
  color: #667eea !important;
  font-weight: 600;
  padding: 6px 16px !important;
  border-radius: 20px !important;
  animation: favoritedPulse 2s ease-in-out infinite;
}

/* 已收藏标签内容布局 */
.favorited-tag {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.favorited-tag .el-icon {
  display: inline-flex;
  align-items: center;
}

@keyframes favoritedPulse {
  0%, 100% {
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  }
  50% {
    box-shadow: 0 2px 16px rgba(102, 126, 234, 0.6);
  }
}


/* 退款对话框样式 */
.refund-dialog :deep(.el-dialog) {
  background: linear-gradient(135deg, #242b3d 0%, #2d3548 50%, #3a4158 100%);
  border: 1px solid rgba(102, 126, 234, 0.4);
  border-radius: 16px;
}

.refund-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(102, 126, 234, 0.3);
  padding: 20px;
}

.refund-dialog :deep(.el-dialog__title) {
  color: #f0f2f5;
  font-size: 18px;
  font-weight: 600;
}

.refund-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.refund-dialog :deep(.el-form-item__label) {
  color: #f0f2f5 !important;
}

.refund-dialog :deep(.el-input__wrapper) {
  background: rgba(36, 43, 61, 0.9);
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: none;
  transition: all 0.3s ease;
}

.refund-dialog :deep(.el-input__wrapper:hover) {
  border-color: rgba(102, 126, 234, 0.6);
}

.refund-dialog :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.refund-dialog :deep(.el-input__inner),
.refund-dialog :deep(.el-textarea__inner) {
  color: #f0f2f5;
}

.dialog-amount {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

/* 按钮加载文字样式 */
.btn-loading-text {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

/* 全局 MessageBox 样式覆盖 */
:global(.el-message-box) {
  background: linear-gradient(135deg, #242b3d 0%, #2d3448 50%, #3a4158 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.4) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4) !important;
}

:global(.el-message-box__header) {
  border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
  padding-bottom: 15px !important;
}

:global(.el-message-box__title) {
  color: #f0f2f5 !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

:global(.el-message-box__content) {
  color: #c9d1d9 !important;
  font-size: 15px !important;
  line-height: 1.6 !important;
}

:global(.el-message-box__btns) {
  border-top: 1px solid rgba(102, 126, 234, 0.3) !important;
  padding-top: 15px !important;
}

:global(.el-message-box__cancel-btn) {
  color: #c9d1d9 !important;
  border-color: rgba(102, 126, 234, 0.4) !important;
  background: rgba(36, 43, 61, 0.6) !important;
}

:global(.el-message-box__cancel-btn:hover) {
  background: rgba(102, 126, 234, 0.2) !important;
  border-color: rgba(102, 126, 234, 0.6) !important;
}

:global(.el-message-box__confirm-btn) {
  background: linear-gradient(135deg, #f59e42 0%, #f56c6c 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 500 !important;
}

:global(.el-message-box__confirm-btn:hover) {
  background: linear-gradient(135deg, #f56c6c 0%, #f59e42 100%) !important;
  box-shadow: 0 4px 15px rgba(245, 158, 66, 0.4) !important;
}

/* 退款对话框 footer 中的按钮样式 */
.refund-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid rgba(102, 126, 234, 0.3);
}

.refund-dialog :deep(.el-button--warning) {
  background: linear-gradient(135deg, #f59e42 0%, #f56c6c 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
}

.refund-dialog :deep(.el-button--warning:hover) {
  background: linear-gradient(135deg, #f56c6c 0%, #f59e42 100%);
  box-shadow: 0 4px 15px rgba(245, 158, 66, 0.4);
  transform: translateY(-2px);
}

.refund-dialog :deep(.el-button:not(.el-button--warning)) {
  color: #c9d1d9;
  border-color: rgba(102, 126, 234, 0.4);
  background: rgba(36, 43, 61, 0.6);
}

.refund-dialog :deep(.el-button:not(.el-button--warning):hover) {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.6);
}

/* 收藏对话框样式 */
.favorite-dialog :deep(.el-dialog) {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border: 1px solid rgba(102, 126, 234, 0.3);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);
}

.favorite-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
  border-bottom: 1px solid rgba(102, 126, 234, 0.3);
  padding: 20px;
}

.favorite-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.favorite-dialog :deep(.el-dialog__body) {
  padding: 25px 20px;
}

.favorite-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.favorite-chef-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(36, 43, 61, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.favorite-chef-info h4 {
  margin: 0 0 5px 0;
  color: #fff;
  font-size: 16px;
}

.chef-details .chef-level {
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.favorite-message {
  margin: 0;
  padding: 15px;
  background: rgba(102, 126, 234, 0.1);
  border-left: 3px solid #667eea;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  line-height: 1.6;
}

.favorite-dialog :deep(.el-dialog__footer) {
  padding: 15px 20px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
}

/* ==================== 自定义对话框通用样式 ==================== */
.custom-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: overlayFadeIn 0.3s ease;
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.custom-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.3),
              0 0 40px rgba(102, 126, 234, 0.1);
  min-width: 450px;
  max-width: 90vw;
  overflow: hidden;
  animation: dialogSlideIn 0.3s ease;
}

@keyframes dialogSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.dialog-header {
  padding: 24px 30px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  border-bottom: 1px solid rgba(102, 126, 234, 0.3);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
}

.dialog-close {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 18px;
}

.dialog-close:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  transform: rotate(90deg);
}

.dialog-body {
  padding: 30px;
}

.dialog-footer {
  padding: 20px 30px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: rgba(0, 0, 0, 0.2);
}

.btn {
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-cancel:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.btn-confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-confirm:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.btn-confirm:active:not(:disabled) {
  transform: translateY(0);
}

/* ==================== 收藏对话框特定样式 ==================== */
.favorite-custom-dialog {
  animation: dialogSlideIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.favorite-chef-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: rgba(36, 43, 61, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.25);
  margin-bottom: 20px;
}

.chef-avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(102, 126, 234, 0.5);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.chef-info-content {
  flex: 1;
}

.chef-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.chef-level-text {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3));
  padding: 4px 12px;
  border-radius: 20px;
  display: inline-block;
}

.favorite-message-box {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 18px;
  background: rgba(102, 126, 234, 0.1);
  border-left: 4px solid #667eea;
  border-radius: 10px;
}

.message-icon {
  font-size: 22px;
  color: #667eea;
  flex-shrink: 0;
  margin-top: 2px;
}

.favorite-message-box p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
}

/* ==================== 过渡动画 ==================== */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: all 0.3s ease;
}

.dialog-fade-enter-from .custom-dialog,
.dialog-fade-leave-to .custom-dialog {
  opacity: 0;
  transform: translateY(-30px) scale(0.95);
}

.dialog-fade-enter-from .custom-dialog-overlay,
.dialog-fade-leave-to .custom-dialog-overlay {
  opacity: 0;
}

/* ==================== 退款对话框特定样式 ==================== */
.refund-custom-dialog {
  min-width: 560px;
  max-width: 95vw;
  animation: dialogSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 头部样式 - 超紧凑版 */
.refund-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 14px 18px;
}

.refund-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.refund-header::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -5%;
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.icon-wrapper-small {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.icon-wrapper-small .el-icon {
  font-size: 22px;
  color: #fff;
}

.title-text {
  flex: 1;
}

.dialog-title {
  margin: 0 0 3px 0;
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}

.dialog-subtitle {
  margin: 0;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 400;
}

.dialog-close {
  width: 32px;
  height: 32px;
  border-radius: 7px;
  border: none;
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 17px;
  position: relative;
  z-index: 1;
}

.dialog-close:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: rotate(90deg) scale(1.1);
}

/* 订单信息卡片 - 超紧凑版 */
.order-info-card-compact {
  margin-bottom: 14px;
  background: rgba(36, 43, 61, 0.4);
  border-radius: 10px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  overflow: hidden;
}

.info-grid-compact {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1px;
  background: rgba(102, 126, 234, 0.1);
}

.info-row-compact {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 14px;
  background: rgba(36, 43, 61, 0.6);
  transition: all 0.3s ease;
}

.info-row-compact:hover {
  background: rgba(36, 43, 61, 0.8);
}

.info-row-compact.highlight {
  background: rgba(102, 126, 234, 0.1);
}

.info-label-compact {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
}

.info-label-compact .el-icon {
  font-size: 13px;
  color: #667eea;
}

.info-value-compact {
  font-size: 13px;
  color: #fff;
  font-weight: 500;
}

.info-value-compact.order-no {
  font-family: 'Courier New', monospace;
  color: #667eea;
  font-size: 12px;
  font-weight: 600;
}

.info-value-compact.amount {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

/* 退款原因输入框 - 超紧凑版 */
.reason-section-compact {
  margin-bottom: 14px;
}

.reason-label-compact {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.label-left-compact {
  display: flex;
  align-items: center;
  gap: 5px;
}

.label-left-compact .el-icon {
  color: #667eea;
  font-size: 15px;
}

.required-mark-compact {
  color: #ff6b6b;
  font-size: 15px;
  font-weight: 700;
}

.textarea-wrapper-compact {
  position: relative;
}

.reason-textarea-compact {
  width: 100%;
  padding: 10px 12px;
  background: rgba(36, 43, 61, 0.6);
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 8px;
  color: #fff;
  font-size: 12px;
  line-height: 1.5;
  resize: vertical;
  transition: all 0.3s ease;
  font-family: inherit;
  min-height: 85px;
}

.reason-textarea-compact:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: rgba(36, 43, 61, 0.8);
}

.reason-textarea-compact::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.char-count-compact {
  position: absolute;
  right: 10px;
  bottom: 10px;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
}

/* 温馨提示 - 超紧凑版 */
.tips-box-compact {
  display: flex;
  gap: 10px;
  padding: 12px;
  background: rgba(255, 193, 7, 0.08);
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 8px;
  border-left: 3px solid #ffc107;
}

.tips-icon-compact {
  flex-shrink: 0;
  width: 26px;
  height: 26px;
  border-radius: 7px;
  background: rgba(255, 193, 7, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.tips-icon-compact .el-icon {
  font-size: 16px;
  color: #ffc107;
}

.tips-content-compact {
  flex: 1;
}

.tips-title-compact {
  margin: 0 0 6px 0;
  font-size: 12px;
  font-weight: 600;
  color: #ffc107;
}

.tips-list-compact {
  margin: 0;
  padding-left: 14px;
}

.tips-list-compact li {
  margin-bottom: 3px;
  font-size: 11px;
  line-height: 1.4;
  color: rgba(255, 255, 255, 0.85);
}

.tips-list-compact li:last-child {
  margin-bottom: 0;
}

/* 提交按钮 */
.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  min-width: 120px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.6);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
