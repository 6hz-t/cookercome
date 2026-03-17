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
      <el-skeleton :rows="5" animated />
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
            <div class="chef-level-badge" :class="getLevelClass(chef.level)">
              {{ getLevelText(chef.level) }}
            </div>
          </div>
          <div class="chef-basic">
            <h3 class="chef-name">
              {{ chef.realName }}
              <span class="gender-icon" :title="chef.gender === 1 ? '男' : '女'">
                <el-icon v-if="chef.gender === 1"><Male /></el-icon>
                <el-icon v-else><Female /></el-icon>
              </span>
            </h3>
            <p class="chef-experience">
              <el-icon><Timer /></el-icon>
              {{ chef.workYears }}年经验
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
                <div class="stat-value">{{ chef.serviceCount || 0 }}</div>
                <div class="stat-label">完成订单</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon price">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value highlight">¥{{ chef.basePrice || 0 }}</div>
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
            @click="$emit('book-chef', chef)"
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, Male, Female, Timer, Document, Finished, Money, Phone, Search } from '@element-plus/icons-vue'
import { getChefList } from '@/api/chef'
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

.gender-icon .el-icon {
  color: #1890ff;
}

.gender-icon .el-icon[male="true"] {
  color: #1890ff;
}

.gender-icon .el-icon[female="true"] {
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
</style>
