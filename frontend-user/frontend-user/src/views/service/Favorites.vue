<template>
  <div class="favorites">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Star /></el-icon>
        我的收藏
      </h2>
      <p class="panel-desc">已收藏的厨师，快速预约</p>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="tech-loader">
        <div class="loader-orb"></div>
        <div class="loader-orb loader-orb-2"></div>
        <div class="loader-orb loader-orb-3"></div>
      </div>
      <p class="loading-text">正在加载收藏夹...</p>
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="favorites.length === 0" class="empty-state">
      <div class="empty-icon">
        <el-icon><Star /></el-icon>
      </div>
      <p class="empty-text">暂无收藏</p>
      <p class="empty-hint">快去发现喜欢的厨师吧</p>
      <el-button type="primary" @click="goToBooking">
        去浏览厨师
      </el-button>
    </div>
    
    <!-- 收藏列表 -->
    <div v-else class="favorites-grid">
      <div 
        v-for="(chef, index) in favorites" 
        :key="chef.chefId" 
        class="favorite-card"
        :style="{ animationDelay: `${index * 0.1}s` }"
      >
        <!-- 厨师头像 -->
        <div class="chef-avatar-wrapper">
          <el-avatar 
            :src="chef.avatarUrl || defaultAvatar" 
            :size="80" 
            shape="circle"
            class="chef-avatar"
          />
          <div class="favorite-time">
            <el-icon><Clock /></el-icon>
            <span>{{ formatFavoriteTime(chef.favoriteTime) }}</span>
          </div>
        </div>
        
        <!-- 厨师信息 -->
        <div class="chef-info">
          <div class="chef-name-row">
            <h4 class="chef-name">{{ chef.realName }}</h4>
            <span class="gender-icon" v-if="chef.gender === 1">
              <el-icon><Male /></el-icon>
            </span>
            <span class="gender-icon" v-else-if="chef.gender === 2">
              <el-icon><Female /></el-icon>
            </span>
          </div>
          
          <div class="chef-level">
            <el-tag size="small" effect="dark" type="warning">
              {{ getLevelText(chef.chefLevel) }}
            </el-tag>
          </div>
          
          <div class="chef-stats">
            <div class="stat-item">
              <el-icon><Finished /></el-icon>
              <span>{{ chef.completedOrders }}订单</span>
            </div>
            <div class="stat-item">
              <el-icon><Timer /></el-icon>
              <span>{{ chef.experienceYears }}年</span>
            </div>
          </div>
          
          <p class="chef-intro">{{ chef.introduction || '暂无简介' }}</p>
          
          <div class="chef-price">
            <span class="price-label">起步价：</span>
            <span class="price-value">¥{{ chef.minPrice }}</span>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="card-actions">
          <el-button 
            type="primary" 
            size="default"
            class="action-btn book-btn"
            @click="handleRebook(chef)"
          >
            <el-icon><Calendar /></el-icon>
            再次预约
          </el-button>
          <el-button 
            size="default"
            class="action-btn remove-btn"
            @click="handleRemoveFavorite(chef.chefId, chef.realName)"
            :loading="removingId === chef.chefId"
          >
            <el-icon><Delete /></el-icon>
            取消收藏
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 再次预约对话框 -->
    <el-dialog 
      v-model="bookingDialogVisible" 
      title="预约厨师"
      width="700px"
      :close-on-click-modal="false"
      class="booking-dialog"
      append-to-body
    >
      <div class="dialog-content">
        <!-- 厨师信息摘要 -->
        <div class="chef-summary">
          <el-avatar :size="60" :src="selectedChef?.avatarUrl || defaultAvatar" shape="circle" />
          <div class="chef-info">
            <h3>{{ selectedChef?.realName }}</h3>
            <p>{{ getLevelText(selectedChef?.chefLevel) }} | {{ selectedChef?.experienceYears }}年经验</p>
            <p class="min-price">最低服务费：¥{{ selectedChef?.minPrice }}</p>
          </div>
        </div>

        <!-- 订单表单 -->
        <el-form :model="orderForm" ref="orderFormRef" label-width="100px" size="large">
          <!-- 选择地址 -->
          <el-form-item label="服务地址" required>
            <el-select 
              v-model="orderForm.addressId" 
              placeholder="请选择服务地址" 
              style="width: 100%"
              @change="handleAddressChange"
            >
              <el-option 
                v-for="addr in addressList" 
                :key="addr.id" 
                :label="formatAddress(addr)" 
                :value="addr.id"
              >
                <div class="address-option">
                  <span>{{ formatAddress(addr) }}</span>
                  <el-tag v-if="addr.isDefault === 1" size="small" type="success">默认</el-tag>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 预约日期 -->
          <el-form-item label="预约日期" required>
            <el-date-picker
              v-model="orderForm.reserveDate"
              type="date"
              placeholder="选择预约日期"
              :disabled-date="disabledDate"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <!-- 预约时间 -->
          <el-form-item label="预约时间" required>
            <el-select 
              v-model="orderForm.reserveTime" 
              placeholder="选择预约时间段" 
              style="width: 100%"
            >
              <el-option label="09:00-11:00" value="09:00-11:00" />
              <el-option label="11:00-13:00" value="11:00-13:00" />
              <el-option label="14:00-16:00" value="14:00-16:00" />
              <el-option label="17:00-19:00" value="17:00-19:00" />
              <el-option label="19:00-21:00" value="19:00-21:00" />
            </el-select>
          </el-form-item>

          <!-- 菜品要求 -->
          <el-form-item label="菜品要求">
            <el-input
              v-model="orderForm.dishRequirements"
              type="textarea"
              :rows="3"
              placeholder="请输入菜品要求，如：少辣、不放香菜等"
            />
          </el-form-item>

          <!-- 订单金额 -->
          <el-form-item label="订单金额" required>
            <el-input-number
              v-model="orderForm.totalAmount"
              :min="selectedChef?.minPrice || 0"
              :precision="2"
              :step="10"
              style="width: 100%"
              placeholder="请输入订单金额"
            />
            <div class="form-tip">订单金额不能低于厨师最低服务费 ¥{{ selectedChef?.minPrice }}</div>
          </el-form-item>

          <!-- 备注 -->
          <el-form-item label="备注">
            <el-input
              v-model="orderForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注信息（选填）"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="bookingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmBooking" :loading="submitting">
            确认下单
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Star, Clock, Male, Female, Finished, Timer, Calendar, Delete 
} from '@element-plus/icons-vue'
import { getFavoriteList, removeFavorite } from '@/api/favorite'
import { getUserAddresses, createOrder } from '@/api/chef'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const emit = defineEmits(['navigate-to'])
const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 收藏列表数据
const favorites = ref([])
const loading = ref(false)
const removingId = ref(null)

// 预约相关
const bookingDialogVisible = ref(false)
const selectedChef = ref(null)
const addressList = ref([])
const orderFormRef = ref(null)
const submitting = ref(false)

// 订单表单默认值
const orderForm = ref({
  chefId: 0,
  addressId: 0,
  reserveDate: '',
  reserveTime: '',
  dishRequirements: '',
  totalAmount: 0,
  remark: ''
})

// 获取等级文字
const getLevelText = (level) => {
  const levelMap = {
    1: '初级厨师',
    2: '中级厨师',
    3: '高级厨师',
    4: '资深厨师',
    5: '特级厨师'
  }
  return levelMap[level] || '未定级'
}

// 格式化收藏时间
const formatFavoriteTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  // 小于 1 分钟
  if (diff < 60000) return '刚刚'
  // 小于 1 小时
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  // 小于 24 小时
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  // 小于 7 天
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  // 超过 7 天显示具体日期
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 加载收藏列表
const loadFavorites = async () => {
  try {
    loading.value = true
    const res = await getFavoriteList()
    if (res.code === 200) {
      favorites.value = res.data || []
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const handleRemoveFavorite = async (chefId, chefName) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消收藏厨师「${chefName}」吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    removingId.value = chefId
    await removeFavorite(chefId)
    
    ElMessage.success('取消收藏成功')
    // 重新加载列表
    await loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      // 不再显示错误消息，全局拦截器已经处理
    }
  } finally {
    removingId.value = null
  }
}

// 再次预约
const handleRebook = async (chef) => {
  selectedChef.value = chef
  bookingDialogVisible.value = true
  
  // 初始化订单表单
  orderForm.value = {
    chefId: chef.chefId,
    addressId: 0,
    reserveDate: '',
    reserveTime: '',
    dishRequirements: '',
    totalAmount: chef.minPrice || 0,
    remark: ''
  }
  
  // 加载用户地址
  await loadUserAddresses()
}

// 加载用户地址列表
const loadUserAddresses = async () => {
  try {
    const res = await getUserAddresses()
    if (res.code === 200 && res.data) {
      addressList.value = res.data
      // 默认选择默认地址
      const defaultAddr = addressList.value.find(addr => addr.isDefault === 1)
      if (defaultAddr) {
        orderForm.value.addressId = defaultAddr.id
      }
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
  }
}

// 格式化地址显示
const formatAddress = (addr) => {
  return `${addr.province}${addr.city}${addr.district}${addr.detailAddress}`
}

// 禁用今天之前的日期
const disabledDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date.getTime() < today.getTime()
}

// 处理地址变化
const handleAddressChange = (addressId) => {
  console.log('选择的地址 ID:', addressId)
}

// 确认下单
const handleConfirmBooking = async () => {
  // 表单验证
  if (!orderForm.value.addressId) {
    ElMessage.warning('请选择服务地址')
    return
  }
  if (!orderForm.value.reserveDate) {
    ElMessage.warning('请选择预约日期')
    return
  }
  if (!orderForm.value.reserveTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  if (!orderForm.value.totalAmount || orderForm.value.totalAmount < (selectedChef.value?.minPrice || 0)) {
    ElMessage.warning(`订单金额不能低于厨师最低服务费 ¥${selectedChef.value?.minPrice}`)
    return
  }
  
  try {
    submitting.value = true
    await createOrder(orderForm.value)
    
    ElMessage.success('下单成功')
    bookingDialogVisible.value = false
    // 跳转到订单页面或刷新
    router.push('/service/orders')
  } catch (error) {
    console.error('下单失败:', error)
    // 不再显示错误消息，全局拦截器已经处理
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadFavorites()
})

// 跳转到预约厨师页面
const goToBooking = () => {
  emit('navigate-to', 'booking')
}
</script>

<style scoped>
/* 科技感配色方案 */
.favorites {
  --tech-bg: linear-gradient(135deg, #0c0c1e 0%, #1a1a3e 50%, #0f0f2d 100%);
  --neon-blue: #00f3ff;
  --neon-purple: #bc13fe;
  --neon-green: #0aff0a;
  --neon-pink: #ff00ff;
  --tech-gray: #2a2a4a;
  --tech-border: rgba(0, 243, 255, 0.3);
  --glass-bg: rgba(20, 20, 50, 0.6);
  --glass-border: rgba(0, 243, 255, 0.2);
  
  position: relative;
  min-height: 600px;
  padding: 30px;
}

/* 面板头部 */
.panel-header {
  margin-bottom: 30px;
  text-align: center;
}

.panel-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--neon-blue);
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  text-shadow: 0 0 20px rgba(0, 243, 255, 0.5);
}

.panel-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 20px;
}

.tech-loader {
  position: relative;
  width: 80px;
  height: 80px;
}

.loader-orb {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--neon-blue), transparent);
  box-shadow: 0 0 20px var(--neon-blue);
  animation: orbit 2s linear infinite;
}

.loader-orb-2 {
  animation-delay: -0.67s;
  background: radial-gradient(circle, var(--neon-purple), transparent);
  box-shadow: 0 0 20px var(--neon-purple);
}

.loader-orb-3 {
  animation-delay: -1.33s;
  background: radial-gradient(circle, var(--neon-pink), transparent);
  box-shadow: 0 0 20px var(--neon-pink);
}

@keyframes orbit {
  0% {
    transform: rotate(0deg) translateX(40px) rotate(0deg);
  }
  100% {
    transform: rotate(360deg) translateX(40px) rotate(-360deg);
  }
}

.loading-text {
  color: var(--neon-blue);
  font-size: 16px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 15px;
}

.empty-icon {
  font-size: 80px;
  color: rgba(255, 255, 255, 0.2);
}

.empty-text {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.empty-hint {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
}

/* 收藏卡片网格 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 25px;
}

.favorite-card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
  padding: 25px;
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: slideIn 0.5s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes slideIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.favorite-card:hover {
  transform: translateY(-8px) scale(1.02);
  border-color: var(--neon-blue);
  box-shadow: 
    0 8px 32px rgba(0, 243, 255, 0.2),
    0 0 60px rgba(0, 243, 255, 0.1);
}

/* 厨师头像区域 */
.chef-avatar-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.chef-avatar {
  border: 3px solid var(--neon-blue);
  box-shadow: 0 0 30px rgba(0, 243, 255, 0.3);
}

.favorite-time {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  border: 1px solid var(--neon-blue);
  border-radius: 12px;
  padding: 4px 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--neon-blue);
  white-space: nowrap;
}

/* 厨师信息 */
.chef-info {
  margin-bottom: 20px;
}

.chef-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.chef-name {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin: 0;
  flex: 1;
}

.gender-icon {
  font-size: 18px;
  color: var(--neon-blue);
}

.gender-icon .el-icon {
  vertical-align: middle;
}

.chef-level {
  margin-bottom: 12px;
}

.chef-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.stat-item .el-icon {
  color: var(--neon-green);
}

.chef-intro {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px;
}

.chef-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--neon-pink);
  text-shadow: 0 0 10px rgba(255, 0, 255, 0.3);
}

/* 操作按钮 */
.card-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  height: 40px;
  font-size: 14px;
  border-radius: 10px;
  transition: all 0.3s;
}

.book-btn {
  background: linear-gradient(135deg, var(--neon-blue), var(--neon-purple));
  border: none;
  color: #fff;
  box-shadow: 0 4px 15px rgba(0, 243, 255, 0.3);
}

.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 243, 255, 0.5);
}

.remove-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
}

.remove-btn:hover {
  background: rgba(255, 0, 0, 0.2);
  border-color: rgba(255, 0, 0, 0.4);
  color: #ff4444;
}

/* 预约对话框 */
.booking-dialog :deep(.el-dialog) {
  background: var(--tech-bg);
  border: 1px solid var(--tech-border);
  border-radius: 16px;
}

.booking-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid var(--tech-border);
  padding: 20px 24px;
}

.booking-dialog :deep(.el-dialog__title) {
  color: var(--neon-blue);
  font-size: 20px;
  font-weight: bold;
}

.booking-dialog :deep(.el-dialog__body) {
  padding: 24px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.booking-dialog :deep(.el-dialog__footer) {
  border-top: 1px solid var(--tech-border);
  padding: 16px 24px;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 厨师信息摘要 */
.chef-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(0, 243, 255, 0.05);
  border: 1px solid var(--tech-border);
  border-radius: 12px;
}

.chef-info h3 {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 8px 0;
}

.chef-info p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.min-price {
  color: var(--neon-pink) !important;
  font-weight: bold;
  font-size: 16px !important;
}

/* 表单样式 */
.form-tip {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 4px;
}

.address-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 滚动条美化 */
.booking-dialog :deep(.el-dialog__body)::-webkit-scrollbar {
  width: 6px;
}

.booking-dialog :deep(.el-dialog__body)::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.booking-dialog :deep(.el-dialog__body)::-webkit-scrollbar-thumb {
  background: var(--neon-blue);
  border-radius: 3px;
}

.booking-dialog :deep(.el-dialog__body)::-webkit-scrollbar-thumb:hover {
  background: var(--neon-purple);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorites {
    padding: 15px;
  }
  
  .favorites-grid {
    grid-template-columns: 1fr;
  }
  
  .panel-title {
    font-size: 22px;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .chef-summary {
    flex-direction: column;
    text-align: center;
  }
}
</style>
