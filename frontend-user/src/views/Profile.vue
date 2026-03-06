<template>
  <div class="profile">
    <!-- 顶部用户信息卡片 -->
    <div class="profile-header scroll-animate" data-direction="bottom">
      <div class="user-info-card">
        <div class="avatar-section">
          <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          <el-button type="primary" size="small" circle class="edit-avatar-btn">
            <el-icon><Camera /></el-icon>
          </el-button>
        </div>
        <div class="user-details">
          <h2 class="username">张三</h2>
          <p class="user-phone">138****1234</p>
          <div class="user-level">
            <el-tag type="warning" size="large">黄金会员</el-tag>
            <span class="level-points">积分：2580</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button @click="$router.push('/login')">退出登录</el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="profile-content">
      <!-- 统计卡片 -->
      <div class="stats-row scroll-animate" data-direction="bottom">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-value">12</div>
          <div class="stat-label">总订单</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Finished /></el-icon>
          </div>
          <div class="stat-value">8</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-value">2</div>
          <div class="stat-label">进行中</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-value">4.9</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="function-section scroll-animate" data-direction="bottom">
        <h3 class="section-title">我的服务</h3>
        <div class="function-grid">
          <div class="function-item" @click="$router.push('/order')">
            <div class="function-icon">
              <el-icon><Document /></el-icon>
            </div>
            <span class="function-label">我的订单</span>
          </div>
          <div class="function-item">
            <div class="function-icon">
              <el-icon><Location /></el-icon>
            </div>
            <span class="function-label">地址管理</span>
          </div>
          <div class="function-item">
            <div class="function-icon">
              <el-icon><Favorite /></el-icon>
            </div>
            <span class="function-label">我的收藏</span>
          </div>
          <div class="function-item">
            <div class="function-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <span class="function-label">评价记录</span>
          </div>
          <div class="function-item">
            <div class="function-icon">
              <el-icon><Ticket /></el-icon>
            </div>
            <span class="function-label">优惠券</span>
          </div>
          <div class="function-item">
            <div class="function-icon">
              <el-icon><Service /></el-icon>
            </div>
            <span class="function-label">客服中心</span>
          </div>
        </div>
      </div>

      <!-- 地址管理（带地图） -->
      <div class="address-section scroll-animate" data-direction="bottom">
        <div class="section-header">
          <h3 class="section-title">我的地址</h3>
          <el-button type="primary" size="small" @click="showAddAddress = true">
            <el-icon><Plus /></el-icon>新增地址
          </el-button>
        </div>
        
        <div class="address-list">
          <div 
            v-for="(addr, index) in addresses" 
            :key="index"
            class="address-item"
            :class="{ active: addr.isDefault }"
            @click="selectAddress(addr)"
          >
            <div class="address-header">
              <div class="address-info">
                <span class="contact-name">{{ addr.name }}</span>
                <span class="contact-phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault" size="small" type="success">默认</el-tag>
              </div>
              <div class="address-actions">
                <el-button size="small" @click.stop="editAddress(addr)">编辑</el-button>
                <el-button size="small" type="danger" @click.stop="deleteAddress(index)">删除</el-button>
              </div>
            </div>
            <p class="address-detail">
              <el-icon><Location /></el-icon>
              {{ addr.fullAddress }}
            </p>
            
            <!-- 地图显示 -->
            <div class="map-container" ref="mapContainers"></div>
          </div>
        </div>
      </div>

      <!-- 预约情况 -->
      <div class="booking-section scroll-animate" data-direction="bottom">
        <h3 class="section-title">预约记录</h3>
        
        <el-tabs v-model="activeTab" class="booking-tabs">
          <el-tab-pane label="进行中" name="ongoing">
            <div class="booking-list">
              <div 
                v-for="order in ongoingOrders" 
                :key="order.id"
                class="booking-card"
                @click="$router.push(`/order/${order.id}`)"
              >
                <div class="booking-header">
                  <div class="chef-info">
                    <el-avatar :size="50" :src="order.chefAvatar" />
                    <div class="chef-details">
                      <h4>{{ order.chefName }}</h4>
                      <p>{{ order.serviceType }}</p>
                    </div>
                  </div>
                  <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
                </div>
                
                <div class="booking-body">
                  <div class="booking-row">
                    <el-icon><Calendar /></el-icon>
                    <span>服务时间：{{ order.serviceDate }} {{ order.serviceTime }}</span>
                  </div>
                  <div class="booking-row">
                    <el-icon><Location /></el-icon>
                    <span>服务地址：{{ order.address }}</span>
                  </div>
                  <div class="booking-row">
                    <el-icon><User /></el-icon>
                    <span>用餐人数：{{ order.guestCount }}人</span>
                  </div>
                  <div class="booking-row">
                    <el-icon><Money /></el-icon>
                    <span>订单金额：¥{{ order.totalAmount }}</span>
                  </div>
                </div>
                
                <div class="booking-footer" v-if="order.status === 'confirmed'">
                  <el-button size="small" @click.stop="cancelOrder(order.id)">取消订单</el-button>
                  <el-button size="small" type="primary" @click.stop="contactChef(order)">联系厨师</el-button>
                </div>
              </div>
              
              <el-empty v-if="ongoingOrders.length === 0" description="暂无进行中的订单" />
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="历史订单" name="history">
            <div class="booking-list">
              <div 
                v-for="order in historyOrders" 
                :key="order.id"
                class="booking-card history"
                @click="$router.push(`/order/${order.id}`)"
              >
                <div class="booking-header">
                  <div class="chef-info">
                    <el-avatar :size="50" :src="order.chefAvatar" />
                    <div class="chef-details">
                      <h4>{{ order.chefName }}</h4>
                      <p>{{ order.serviceType }}</p>
                    </div>
                  </div>
                  <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
                </div>
                
                <div class="booking-body">
                  <div class="booking-row">
                    <el-icon><Calendar /></el-icon>
                    <span>服务时间：{{ order.serviceDate }} {{ order.serviceTime }}</span>
                  </div>
                  <div class="booking-row">
                    <el-icon><Location /></el-icon>
                    <span>服务地址：{{ order.address }}</span>
                  </div>
                  <div class="booking-row">
                    <el-icon><Money /></el-icon>
                    <span>订单金额：¥{{ order.totalAmount }}</span>
                  </div>
                </div>
                
                <div class="booking-footer">
                  <el-button size="small" @click.stop="reviewOrder(order)">评价</el-button>
                  <el-button size="small" type="primary" @click.stop="bookAgain(order)">再次预约</el-button>
                </div>
              </div>
              
              <el-empty v-if="historyOrders.length === 0" description="暂无历史订单" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 应用功能介绍 -->
      <div class="app-features-section scroll-animate" data-direction="bottom">
        <h3 class="section-title">应用功能</h3>
        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><ChefHat /></el-icon>
            </div>
            <div class="feature-content">
              <h4>专业厨师团队</h4>
              <p>所有厨师均经过严格认证，持证上岗，为您提供专业烹饪服务</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="feature-content">
              <h4>准时上门服务</h4>
              <p>智能匹配最近厨师，最短 30 分钟上门，守时守信绝不拖延</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="feature-content">
              <h4>食材代采服务</h4>
              <p>提供新鲜食材代购，保证品质，价格透明，可开具发票</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Shield /></el-icon>
            </div>
            <div class="feature-content">
              <h4>安全保障</h4>
              <p>全程保险，食品安全责任险，让您的用餐更加安心</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Service /></el-icon>
            </div>
            <div class="feature-content">
              <h4>贴心客服</h4>
              <p>一对一专属客服，7×12 小时在线响应，随时为您解决问题</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="feature-content">
              <h4>透明消费</h4>
              <p>明码标价，无隐形消费，支持多种支付方式，担保交易更安全</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加地址对话框 -->
    <el-dialog v-model="showAddAddress" title="添加地址" width="500px">
      <el-form :model="newAddress" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="newAddress.name" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="newAddress.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="所在地区">
          <el-input v-model="newAddress.district" placeholder="请选择所在地区" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input 
            v-model="newAddress.detail" 
            type="textarea" 
            :rows="3"
            placeholder="请输入详细地址，如街道、门牌号等" 
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="newAddress.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAddress = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { 
  Camera, Calendar, Finished, Clock, Star, 
  Document, Location, Favorite, ChatDotRound, 
  Ticket, Service, Plus, ChefHat, Timer, 
  ShoppingCart, Shield, Wallet, Money, User
} from '@element-plus/icons-vue'

// 模拟数据
const addresses = ref([
  {
    name: '张三',
    phone: '138****1234',
    isDefault: true,
    fullAddress: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    latitude: 39.9042,
    longitude: 116.4074
  },
  {
    name: '李四',
    phone: '139****5678',
    isDefault: false,
    fullAddress: '上海市浦东新区陆家嘴环路 1000 号 B 座 2002 室',
    latitude: 31.2304,
    longitude: 121.4737
  }
])

const ongoingOrders = ref([
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
    totalAmount: 598
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
    totalAmount: 888
  }
])

const historyOrders = ref([
  {
    id: 3,
    chefName: '张师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '家常宴席',
    status: 'completed',
    serviceDate: '2026-02-28',
    serviceTime: '18:00-20:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    totalAmount: 468
  },
  {
    id: 4,
    chefName: '刘师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '火锅烧烤',
    status: 'completed',
    serviceDate: '2026-02-14',
    serviceTime: '17:00-19:00',
    address: '北京市朝阳区建国路 93 号万达广场 A 座 1001 室',
    totalAmount: 398
  }
])

const activeTab = ref('ongoing')
const showAddAddress = ref(false)
const newAddress = ref({
  name: '',
  phone: '',
  district: '',
  detail: '',
  isDefault: false
})

const mapContainers = ref([])

// 状态类型映射
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

// 选择地址
const selectAddress = (addr) => {
  console.log('选择地址:', addr)
}

// 编辑地址
const editAddress = (addr) => {
  newAddress.value = { ...addr }
  showAddAddress.value = true
}

// 删除地址
const deleteAddress = (index) => {
  addresses.value.splice(index, 1)
}

// 保存地址
const saveAddress = () => {
  if (!newAddress.value.name || !newAddress.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  addresses.value.push({
    ...newAddress.value,
    fullAddress: `${newAddress.value.district}${newAddress.value.detail}`,
    latitude: 39.9042, // 模拟坐标
    longitude: 116.4074
  })
  
  showAddAddress.value = false
  newAddress.value = {
    name: '',
    phone: '',
    district: '',
    detail: '',
    isDefault: false
  }
  
  ElMessage.success('地址保存成功')
}

// 取消订单
const cancelOrder = (orderId) => {
  ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('订单已取消')
  }).catch(() => {})
}

// 联系厨师
const contactChef = (order) => {
  ElMessage.info(`正在联系${order.chefName}...`)
}

// 评价订单
const reviewOrder = (order) => {
  ElMessageBox.prompt('请为本次服务打分并写下评价', '评价', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '评价内容不能为空'
  }).then(({ value }) => {
    ElMessage.success('评价提交成功')
  }).catch(() => {})
}

// 再次预约
const bookAgain = (order) => {
  ElMessage.success('正在为您重新预约...')
}

// 初始化地图
const initMaps = async () => {
  await nextTick()
  mapContainers.value.forEach((container, index) => {
    if (container && !container.hasChildNodes()) {
      // 这里可以集成百度地图或高德地图 API
      // 目前使用模拟地图展示
      const mockMap = document.createElement('div')
      mockMap.className = 'mock-map'
      mockMap.innerHTML = `
        <div class="mock-map-bg">
          <el-icon><Location /></el-icon>
          <p>地图加载中...</p>
          <p style="font-size: 12px; color: #999;">(需配置地图 API)</p>
        </div>
      `
      container.appendChild(mockMap)
    }
  })
}

// 滚动渐入动画 - IntersectionObserver
const initScrollAnimation = () => {
  const observerOptions = {
    root: null,
    rootMargin: '-10% 0px -10% 0px',
    threshold: 0
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-active')
      }
    })
  }, observerOptions)

  document.querySelectorAll('.scroll-animate').forEach(section => {
    observer.observe(section)
  })
}

onMounted(() => {
  initMaps()
  initScrollAnimation()
})
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

/* 滚动渐入动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(80px);
  transition: all 0.6s ease-out;
}

.scroll-animate.animate-active {
  opacity: 1;
  transform: translateY(0);
}

/* 头部用户信息卡片 */
.profile-header {
  margin-bottom: 30px;
}

.user-info-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.avatar-section {
  position: relative;
  flex-shrink: 0;
}

.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  border: 3px solid white;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.user-phone {
  font-size: 16px;
  color: #666;
  margin: 0 0 15px 0;
}

.user-level {
  display: flex;
  align-items: center;
  gap: 15px;
}

.level-points {
  font-size: 14px;
  color: #999;
}

.header-actions {
  flex-shrink: 0;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 32px;
  color: #667eea;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

/* 功能区域 */
.function-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.function-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
}

.function-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.function-item:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.function-item:hover .function-icon {
  color: white;
}

.function-item:hover .function-label {
  color: white;
}

.function-icon {
  font-size: 32px;
  color: #667eea;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.function-label {
  font-size: 14px;
  color: #666;
  text-align: center;
  transition: all 0.3s ease;
}

/* 地址管理 */
.address-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.address-item {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.address-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.address-item.active {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.contact-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.contact-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-top: 10px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.map-container {
  margin-top: 15px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.mock-map {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.mock-map-bg {
  text-align: center;
  color: #667eea;
}

.mock-map-bg .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

/* 预约记录 */
.booking-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.booking-tabs {
  margin-top: 20px;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 300px;
}

.booking-card {
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.booking-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.booking-card.history {
  opacity: 0.8;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.chef-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chef-details h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 5px 0;
}

.chef-details p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.booking-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}

.booking-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #666;
}

.booking-row .el-icon {
  color: #667eea;
  font-size: 18px;
}

.booking-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

/* 应用功能介绍 */
.app-features-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.feature-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.feature-icon {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.feature-content {
  flex: 1;
}

.feature-content h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.feature-content p {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .function-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .feature-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .function-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .user-info-card {
    flex-direction: column;
    text-align: center;
  }
  
  .header-actions {
    width: 100%;
  }
}
</style>
