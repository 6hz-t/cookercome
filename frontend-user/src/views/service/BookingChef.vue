<template>
  <div class="booking-chef">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Calendar /></el-icon>
        预约厨师
      </h2>
      <p class="panel-desc">专业厨师上门服务，为您定制专属美食</p>
    </div>
    
    <!-- 搜索和排序栏 -->
    <div class="search-sort-bar">
      <div class="search-section">
        <el-input
          v-model="searchName"
          placeholder="输入厨师姓名搜索"
          prefix-icon="Search"
          clearable
          @input="handleSearchInput"
          class="search-input"
        />
      </div>
      
      <div class="sort-section">
        <span class="sort-label">排序：</span>
        <el-radio-group v-model="sortBy" @change="handleSortChange" class="sort-radio">
          <el-radio label="default">默认</el-radio>
          <el-radio label="level_desc">等级从高到低</el-radio>
          <el-radio label="price_asc">价格从低到高</el-radio>
          <el-radio label="orders_desc">订单数从高到低</el-radio>
        </el-radio-group>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="tech-loader">
        <div class="loader-orb"></div>
        <div class="loader-orb loader-orb-2"></div>
        <div class="loader-orb loader-orb-3"></div>
        <p class="loading-text">正在加载厨师信息...</p>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="chefList.length === 0" class="empty-container">
      <el-empty description="暂无符合条件的厨师" />
    </div>
    
    <!-- 厨师卡片列表 -->
    <div v-else class="chef-cards">
      <div v-for="chef in chefList" :key="chef.id" class="chef-card">
        <!-- 左侧：基本信息 -->
        <div class="chef-left">
          <div class="chef-avatar-section">
            <el-avatar :size="80" :src="chef.avatarUrl || defaultAvatar" shape="circle" class="chef-avatar" />
            <div class="chef-level-badge" :class="getLevelClass(chef.chefLevel)">
              {{ getLevelText(chef.chefLevel) }}
            </div>
          </div>
          <div class="chef-basic">
            <h3 class="chef-name">
              {{ chef.realName }}
              <span class="gender-icon" :class="chef.gender === 1 ? 'male' : 'female'" :title="chef.gender === 1 ? '男' : '女'">
                <el-icon v-if="chef.gender === 1"><Male /></el-icon>
                <el-icon v-else><Female /></el-icon>
              </span>
            </h3>
            <p class="chef-experience">
              <el-icon><Timer /></el-icon>
              {{ chef.experienceYears || 0 }}年经验
            </p>
          </div>
        </div>
        
        <!-- 中间：详细信息 -->
        <div class="chef-middle">
          <div class="chef-intro">
            <el-icon><Document /></el-icon>
            <span>{{ chef.introduction || '暂无简介' }}</span>
          </div>
          <div class="chef-stats-grid">
            <div class="stat-item">
              <div class="stat-icon completed">
                <el-icon><Finished /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ chef.completedOrders || 0 }}</div>
                <div class="stat-label">完成订单</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon price">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value highlight">¥{{ chef.minPrice || 0 }}</div>
                <div class="stat-label">起步价</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧：操作按钮 -->
        <div class="chef-right">
          <el-button 
            type="primary" 
            size="large"
            class="book-btn"
            @click="handleBookChef(chef)"
          >
            立即预约
          </el-button>
          <div class="chef-contact">
            <el-icon><Phone /></el-icon>
            <span>{{ formatPhone(chef.phone) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 预约订单对话框 -->
    <el-dialog 
      v-model="bookingDialogVisible" 
      title="预约厨师订单"
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
import { ref, onMounted, computed } from 'vue'
import { Calendar, Male, Female, Timer, Document, Finished, Money, Phone, Search } from '@element-plus/icons-vue'
import { getChefList, getUserAddresses, createOrder } from '@/api/chef'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['book-chef'])

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

// 响应式数据
const chefList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchName = ref('')
const sortBy = ref('default')
let searchTimer = null

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

// 获取等级样式类
const getLevelClass = (level) => {
  const classMap = {
    1: 'level-primary',
    2: 'level-intermediate',
    3: 'level-senior',
    4: 'level-expert',
    5: 'level-master'
  }
  return classMap[level] || ''
}

// 格式化手机号
const formatPhone = (phone) => {
  if (!phone) return '暂未填写'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 加载厨师列表
const loadChefList = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sortBy: sortBy.value === 'default' ? undefined : sortBy.value,
      name: searchName.value || undefined
    }
    
    const res = await getChefList(params)
    if (res.code === 200 && res.data) {
      chefList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载厨师列表失败:', error)
    ElMessage.error('加载厨师列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索输入（防抖 1 秒）
const handleSearchInput = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  searchTimer = setTimeout(() => {
    currentPage.value = 1 // 重置到第一页
    loadChefList()
  }, 1000)
}

// 处理排序变化
const handleSortChange = () => {
  currentPage.value = 1 // 重置到第一页
  loadChefList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadChefList()
}

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  loadChefList()
}

// 组件挂载时加载数据
onMounted(() => {
  loadChefList()
})

// ==================== 预约相关方法 ====================

// 处理预约按钮点击（从父组件接收事件）
const handleBookChef = (chef) => {
  selectedChef.value = chef
  bookingDialogVisible.value = true
  // 初始化表单
  orderForm.value = {
    chefId: chef.id,
    addressId: 0,
    reserveDate: '',
    reserveTime: '',
    dishRequirements: '',
    totalAmount: chef.minPrice || 0,
    remark: ''
  }
  // 加载用户地址
  loadUserAddresses()
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
  if (!orderForm.value.totalAmount || orderForm.value.totalAmount <= 0) {
    ElMessage.warning('请输入订单金额')
    return
  }
  if (selectedChef.value && orderForm.value.totalAmount < selectedChef.value.minPrice) {
    ElMessage.warning(`订单金额不能低于厨师最低服务费 ¥${selectedChef.value.minPrice}`)
    return
  }

  try {
    submitting.value = true
    const params = {
      chefId: orderForm.value.chefId,
      addressId: orderForm.value.addressId,
      reserveDate: orderForm.value.reserveDate,
      reserveTime: orderForm.value.reserveTime,
      dishRequirements: orderForm.value.dishRequirements,
      totalAmount: orderForm.value.totalAmount,
      remark: orderForm.value.remark
    }
    
    const res = await createOrder(params)
    if (res.code === 200) {
      ElMessage.success(`订单创建成功！订单号：${res.data}`)
      bookingDialogVisible.value = false
      // 通知父组件订单创建成功
      emit('order-created', res.data)
    } else {
      ElMessage.error(res.message || '订单创建失败')
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error(error.message || '订单创建失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.booking-chef {
  min-height: 600px;
}

.panel-header {
  text-align: center;
  margin-bottom: 40px;
}

.panel-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 10px;
}

.panel-title .el-icon {
  color: #667eea;
  font-size: 32px;
}

.panel-desc {
  font-size: 16px;
  color: var(--text-secondary);
}

/* 搜索排序栏 */
.search-sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8));
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
  backdrop-filter: blur(10px);
}

.search-section {
  flex: 1;
  max-width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(26, 31, 46, 0.8);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: none;
}

.search-input :deep(.el-input__inner) {
  color: var(--text-primary);
}

.sort-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 14px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.sort-radio {
  display: flex;
  gap: 16px;
}

.sort-radio :deep(.el-radio) {
  color: var(--text-secondary);
  font-size: 14px;
}

.sort-radio :deep(.el-radio.is-checked) {
  color: #667eea;
}

.sort-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

/* 加载状态 */
.loading-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 科技感加载动画 */
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
    opacity: 0.5;
  }
  75% {
    transform: translate(20px, 30px) scale(1.05);
    opacity: 0.7;
  }
}

.loading-text {
  margin-top: 60px;
  font-size: 16px;
  color: var(--text-secondary);
  text-align: center;
  letter-spacing: 1px;
  animation: textPulse 2s ease-in-out infinite;
}

@keyframes textPulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
}

/* 空状态 */
.empty-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.pagination :deep(.el-pagination) {
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8));
  padding: 15px 20px;
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
}

.pagination :deep(.el-pager li) {
  background: rgba(26, 31, 46, 0.8);
  color: var(--text-secondary);
  border-radius: 6px;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  margin: 0 4px;
}

.pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
}

.pagination :deep(.el-pager li:not(.is-active):hover) {
  background: rgba(102, 126, 234, 0.2);
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  background: rgba(26, 31, 46, 0.8);
  color: var(--text-secondary);
  border-radius: 6px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.pagination :deep(.btn-prev:hover),
.pagination :deep(.btn-next:hover) {
  background: rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.pagination :deep(.el-select .el-input__wrapper) {
  background: rgba(26, 31, 46, 0.8);
  box-shadow: none;
}

.pagination :deep(.el-select .el-input__inner) {
  color: var(--text-primary);
}

.chef-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chef-card {
  display: grid;
  grid-template-columns: 280px 1fr auto;
  gap: 24px;
  background: linear-gradient(135deg, #1a1f2e 0%, #242b3d 50%, #2d3450 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.3),
    0 0 40px rgba(102, 126, 234, 0.1),
    inset 0 0 60px rgba(102, 126, 234, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(102, 126, 234, 0.2);
  position: relative;
  overflow: hidden;
}

.chef-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -50%;
  width: 200%;
  height: 3px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(102, 126, 234, 0.3),
    rgba(118, 75, 162, 0.6),
    rgba(102, 126, 234, 0.3),
    transparent
  );
  animation: shimmer 4s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateX(100%);
    opacity: 0;
  }
}

.chef-card:hover {
  transform: translateY(-4px);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.4),
    0 0 60px rgba(102, 126, 234, 0.2),
    inset 0 0 80px rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.4);
}

/* 左侧区域 */
.chef-left {
  display: flex;
  gap: 16px;
  align-items: center;
}

.chef-avatar-section {
  position: relative;
  flex-shrink: 0;
}

.chef-avatar {
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chef-level-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: bold;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  white-space: nowrap;
}

.chef-level-badge.level-primary {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.chef-level-badge.level-intermediate {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.chef-level-badge.level-senior {
  background: linear-gradient(135deg, #faad14, #ffc53d);
}

.chef-level-badge.level-expert {
  background: linear-gradient(135deg, #eb2f96, #ff4d9a);
}

.chef-level-badge.level-master {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #333;
}

.chef-basic {
  flex: 1;
}

.chef-name {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.gender-icon {
  font-size: 16px;
  opacity: 0.6;
}

/* 男性图标 - 蓝色 */
.gender-icon.male .el-icon {
  color: #1890ff;
}

/* 女性图标 - 粉色 */
.gender-icon.female .el-icon {
  color: #eb2f96;
}

.chef-experience {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.chef-experience .el-icon {
  color: #faad14;
}

/* 中间区域 */
.chef-middle {
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
}

.chef-intro {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-height: 60px;
  overflow: hidden;
}

.chef-intro .el-icon {
  color: #667eea;
  flex-shrink: 0;
  margin-top: 2px;
}

.chef-stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8));
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
  backdrop-filter: blur(10px);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.stat-icon.completed {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.stat-icon.price {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-value.highlight {
  color: #f5576c;
  font-size: 22px;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* 右侧区域 */
.chef-right {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  min-width: 140px;
}

.book-btn {
  width: 120px;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.chef-contact {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.chef-contact .el-icon {
  color: #52c41a;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chef-card {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .chef-left {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .chef-middle {
    flex-direction: row;
    align-items: center;
  }
  
  .chef-intro {
    flex: 1;
  }
  
  .chef-stats-grid {
    min-width: 200px;
  }
  
  .chef-right {
    flex-direction: row;
    justify-content: flex-start;
  }
  
  .book-btn {
    width: auto;
    flex: 1;
  }
}

@media (max-width: 768px) {
  .chef-card {
    padding: 16px;
  }
  
  .chef-name {
    font-size: 18px;
  }
  
  .stat-item {
    padding: 8px;
  }
  
  .stat-icon {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }
  
  .stat-value {
    font-size: 16px;
  }
}

/* 预约对话框样式 - 使用更强选择器 */
.booking-dialog.el-dialog {
  background: linear-gradient(135deg, #1a1f2e 0%, #242b3d 50%, #2d3450 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  border-radius: 16px !important;
}

.booking-dialog .el-dialog__header {
  padding: 20px 24px !important;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2) !important;
  background: transparent !important;
}

.booking-dialog .el-dialog__title {
  color: #fff !important;
  font-size: 20px !important;
  font-weight: 600 !important;
}

.booking-dialog .el-dialog__body {
  padding: 24px !important;
  max-height: 70vh !important;
  overflow-y: auto !important;
  background: transparent !important;
}

.booking-dialog .el-dialog__footer {
  padding: 16px 24px !important;
  border-top: 1px solid rgba(102, 126, 234, 0.2) !important;
  background: transparent !important;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chef-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8));
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
}

.chef-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: var(--text-primary);
}

.chef-info p {
  margin: 4px 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.min-price {
  color: #f5576c !important;
  font-weight: 600;
  font-size: 15px !important;
}

.address-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 6px;
}

.booking-dialog :deep(.el-date-picker) {
  width: 100%;
}

.booking-dialog :deep(.el-date-picker__editor-wrap) {
  width: 100%;
}
</style>

<style>
/* 全局样式 - 用于覆盖 Element Plus Dialog 组件样式 */
.booking-dialog.el-dialog {
  background: linear-gradient(135deg, #242b3d 0%, #2d3548 50%, #3a4158 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.4) !important;
  border-radius: 16px !important;
  max-width: 700px !important;
  margin: 10vh auto 0 !important;
}

/* 固定对话框位置，居中显示 */
.booking-dialog .el-dialog__header {
  padding: 15px 20px !important;
  border-bottom: 1px solid rgba(102, 126, 234, 0.3) !important;
  background: transparent !important;
}

.booking-dialog .el-dialog__title {
  color: #f0f2f5 !important;
  font-size: 18px !important;
  font-weight: 600 !important;
}

.booking-dialog .el-dialog__body {
  padding: 20px !important;
  max-height: calc(90vh - 150px) !important;
  overflow-y: auto !important;
  overflow-x: hidden !important;
  background: transparent !important;
}

.booking-dialog .el-dialog__footer {
  padding: 15px 20px !important;
  border-top: 1px solid rgba(102, 126, 234, 0.3) !important;
  background: transparent !important;
}

/* 内容区域紧凑化 */
.booking-dialog .dialog-content {
  display: flex !important;
  flex-direction: column !important;
  gap: 15px !important;
}

/* 厨师信息摘要紧凑化 */
.booking-dialog .chef-summary {
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;
  padding: 12px !important;
  background: linear-gradient(135deg, rgba(36, 43, 61, 0.9), rgba(45, 52, 80, 0.8)) !important;
  border-radius: 10px !important;
  border: 1px solid rgba(102, 126, 234, 0.2) !important;
}

.booking-dialog .chef-info h3 {
  margin: 0 0 5px 0 !important;
  font-size: 16px !important;
  color: #f0f2f5 !important;
}

.booking-dialog .chef-info p {
  margin: 3px 0 !important;
  font-size: 13px !important;
  color: #c0c4cc !important;
}

.booking-dialog .min-price {
  color: #f5576c !important;
  font-weight: 600 !important;
  font-size: 14px !important;
}

/* 表单紧凑化 */
.booking-dialog .el-form-item {
  margin-bottom: 12px !important;
}

.booking-dialog .el-form-item__label {
  color: #f0f2f5 !important;
  font-size: 13px !important;
  line-height: 1.5 !important;
}

/* 输入框样式 */
.booking-dialog .el-input__wrapper,
.booking-dialog .el-textarea__inner {
  background: rgba(36, 43, 61, 0.9) !important;
  border-color: rgba(102, 126, 234, 0.4) !important;
  box-shadow: none !important;
  border-radius: 6px !important;
}

.booking-dialog .el-input__inner,
.booking-dialog .el-textarea__inner {
  color: #f0f2f5 !important;
  font-size: 14px !important;
  padding: 8px 12px !important;
}

/* 特别处理：地址选择器和日期选择器的输入框 */
.booking-dialog .el-select .el-select__wrapper,
.booking-dialog .el-date-picker .el-input__wrapper {
  background: rgba(36, 43, 61, 0.9) !important;
  border-color: rgba(102, 126, 234, 0.4) !important;
  box-shadow: none !important;
}

.booking-dialog .el-select .el-input__inner,
.booking-dialog .el-date-picker .el-input__inner {
  color: #f0f2f5 !important;
}

/* 下拉箭头图标颜色 */
.booking-dialog .el-select .el-icon {
  color: #c0c4cc !important;
}

.booking-dialog .el-input-number__decrease,
.booking-dialog .el-input-number__increase {
  background: rgba(36, 43, 61, 0.9) !important;
  border-color: rgba(102, 126, 234, 0.4) !important;
  color: #c0c4cc !important;
}

/* 下拉框样式 */
.booking-dialog .el-select-dropdown__item {
  color: #c0c4cc !important;
  font-size: 14px !important;
}

.booking-dialog .el-select-dropdown__item.is-selected {
  color: #667eea !important;
}

.booking-dialog .el-select .el-input__wrapper {
  cursor: pointer !important;
}

/* 按钮样式 */
.booking-dialog .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  padding: 8px 24px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  border-radius: 6px !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.booking-dialog .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6) !important;
}

.booking-dialog .el-button--default {
  background: rgba(58, 65, 88, 0.8) !important;
  border: 1px solid rgba(102, 126, 234, 0.4) !important;
  color: #c0c4cc !important;
  padding: 8px 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  border-radius: 6px !important;
}

.booking-dialog .el-button--default:hover {
  background: rgba(102, 126, 234, 0.2) !important;
  border-color: #667eea !important;
  color: #fff !important;
}

/* 日期选择器 */
.booking-dialog .el-date-picker {
  width: 100% !important;
}

.booking-dialog .el-date-picker__editor-wrap {
  width: 100% !important;
}

.booking-dialog .el-date-editor {
  width: 100% !important;
}

/* 地址选项 */
.booking-dialog .address-option {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  width: 100% !important;
}

/* 提示文字 */
.booking-dialog .form-tip {
  font-size: 12px !important;
  color: #909399 !important;
  margin-top: 4px !important;
}

/* 滚动条美化 */
.booking-dialog .el-dialog__body::-webkit-scrollbar {
  width: 6px !important;
}

.booking-dialog .el-dialog__body::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.5) !important;
  border-radius: 3px !important;
}

.booking-dialog .el-dialog__body::-webkit-scrollbar-track {
  background: rgba(36, 43, 61, 0.3) !important;
}
</style>
