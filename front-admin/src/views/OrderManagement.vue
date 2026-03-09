<template>
  <div class="order-management">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <el-icon size="24" color="#1989fa"><UserFilled /></el-icon>
        <span class="sidebar-title">管理后台</span>
      </div>
      <div class="sidebar-menu">
        <div class="menu-group">
          <div class="group-title">核心管理</div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'account' }"
            @click="navigateTo('/dashboard')"
          >
            <el-icon><User /></el-icon>
            <span>账号管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'chef' }"
            @click="navigateTo('/chef-audit')"
          >
            <el-icon><UserFilled /></el-icon>
            <span>厨师审核</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'data' }"
            @click="navigateTo('/data-management')"
          >
            <el-icon><Grid /></el-icon>
            <span>基础数据管理</span>
          </div>
        </div>
        <div class="menu-group">
          <div class="group-title">业务运营</div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'order' }"
            @click="navigateTo('/order-control')"
          >
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'stats' }"
            @click="navigateTo('/statistics')"
          >
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </div>
        </div>
      </div>
      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="32" src="https://img.yzcdn.cn/vant/cat.jpeg"></el-avatar>
          <div class="user-text">
            <div class="user-name">超级管理员</div>
            <div class="user-id">ID: 10001</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="content">
      <!-- 顶部信息栏 -->
      <div class="top-info">
        <div class="breadcrumb">业务运营 &gt; 订单管控</div>
        <div class="top-right">
          <div class="date-info">当前日期 {{ currentDate }}</div>
          <el-icon size="20" class="bell-icon"><Bell /></el-icon>
          <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">安全退出</el-button>
        </div>
      </div>

      <!-- 订单管控主体内容 -->
      <div class="order-management-section">
        <div class="section-header">
          <h3>订单管控</h3>
          <div class="filters">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索订单ID/用户ID/菜品名称"
              prefix-icon="Search"
              style="width: 250px; margin-right: 10px;"
            ></el-input>
            <el-select
              v-model="filterStatus"
              placeholder="订单状态"
              clearable
              style="width: 120px; margin-right: 10px;"
            >
              <el-option
                v-for="status in statusOptions"
                :key="status.value"
                :label="status.label"
                :value="status.value"
              />
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 300px; margin-right: 10px;"
            />
            <el-button type="primary" @click="applyFilters">筛选</el-button>
          </div>
        </div>

        <!-- 核心修改：所有列都设置表头和数据左对齐 -->
        <el-table border :data="displayedOrders" style="width: 100%;" class="order-table" >
          <el-table-column prop="id" label="订单ID" width="120" align="left" header-align="left" />
          <el-table-column prop="userId" label="用户ID" width="120" align="left" header-align="left" />
          <el-table-column prop="dishName" label="菜品名称" width="150" align="left" header-align="left" />
          <el-table-column prop="orderTime" label="下单时间" width="180" align="left" header-align="left" />
          <el-table-column prop="status" label="订单状态" width="120" align="left" header-align="left">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.status)"
                disable-transitions
              >
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="实付金额" width="100" align="left" header-align="left">
            <template #default="scope">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="left" header-align="left">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'abnormal'"
                size="mini" 
                type="danger" 
                @click="forceCancelOrder(scope.row)"
                style="margin-right: 5px;"
              >
                强制取消
              </el-button>
              <el-button 
                v-else
                size="mini" 
                type="info" 
                disabled
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredOrders.length"
          style="margin-top: 20px; text-align: right;"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
// 引入Element Plus图标
import { 
  User, UserFilled, Grid, ShoppingCart, TrendCharts, 
  Bell, InfoFilled, Shop, Search 
} from '@element-plus/icons-vue'
// 引入Element Plus提示框
import { ElMessageBox, ElMessage, ElInput } from 'element-plus'

// 创建路由器实例
const router = useRouter()

// 当前激活菜单
const activeMenu = ref('order')

// 当前日期
const currentDate = ref('2026年03月07日')

// 搜索和筛选参数
const searchKeyword = ref('')
const filterStatus = ref('')
const dateRange = ref([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)

// 订单状态选项
const statusOptions = [
  { value: 'pending', label: '待支付' },
  { value: 'completed', label: '已完成' },
  { value: 'cancelled', label: '已取消' },
  { value: 'abnormal', label: '异常订单' }
]

// 模拟订单数据
const orders = ref([
  { id: 50001, userId: 1001, dishName: '宫保鸡丁', orderTime: '2026-03-01 12:30:15', status: 'completed', amount: 28.50 },
  { id: 50002, userId: 1002, dishName: '麻婆豆腐', orderTime: '2026-03-01 13:45:20', status: 'pending', amount: 18.00 },
  { id: 50003, userId: 1003, dishName: '红烧肉', orderTime: '2026-03-02 11:20:10', status: 'abnormal', amount: 35.00 },
  { id: 50004, userId: 1004, dishName: '鱼香肉丝', orderTime: '2026-03-02 18:15:30', status: 'completed', amount: 22.80 },
  { id: 50005, userId: 1005, dishName: '糖醋里脊', orderTime: '2026-03-03 19:40:25', status: 'cancelled', amount: 32.00 },
  { id: 50006, userId: 1006, dishName: '回锅肉', orderTime: '2026-03-03 20:10:45', status: 'completed', amount: 30.50 },
  { id: 50007, userId: 1007, dishName: '酸辣土豆丝', orderTime: '2026-03-04 12:15:10', status: 'abnormal', amount: 15.00 },
  { id: 50008, userId: 1008, dishName: '蒜蓉菠菜', orderTime: '2026-03-04 13:30:20', status: 'completed', amount: 12.80 },
  { id: 50009, userId: 1009, dishName: '水煮牛肉', orderTime: '2026-03-05 18:45:35', status: 'pending', amount: 42.00 },
  { id: 50010, userId: 1010, dishName: '干煸豆角', orderTime: '2026-03-05 19:20:15', status: 'completed', amount: 25.60 },
  { id: 50011, userId: 1011, dishName: '白切鸡', orderTime: '2026-03-06 11:30:40', status: 'abnormal', amount: 38.00 },
  { id: 50012, userId: 1012, dishName: '清蒸鲈鱼', orderTime: '2026-03-06 17:55:20', status: 'completed', amount: 58.00 },
])

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case 'pending': return 'warning'
    case 'completed': return 'success'
    case 'cancelled': return 'info'
    case 'abnormal': return 'danger'
    default: return 'info'
  }
}

// 获取状态标签
const getStatusLabel = (status) => {
  switch(status) {
    case 'pending': return '待支付'
    case 'completed': return '已完成'
    case 'cancelled': return '已取消'
    case 'abnormal': return '异常'
    default: return '未知'
  }
}

// 计算属性：根据筛选条件过滤订单
const filteredOrders = computed(() => {
  let result = orders.value
  
  // 按关键词搜索（订单ID、用户ID、菜品名称）
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order => 
      String(order.id).includes(keyword) ||
      String(order.userId).includes(keyword) ||
      order.dishName.toLowerCase().includes(keyword)
    )
  }
  
  // 按状态筛选
  if (filterStatus.value) {
    result = result.filter(order => order.status === filterStatus.value)
  }
  
  // 按时间范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    result = result.filter(order => {
      const orderDate = new Date(order.orderTime)
      return orderDate >= startDate && orderDate <= endDate
    })
  }
  
  return result
})

// 计算属性：当前页显示的订单
const displayedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

// 应用筛选条件
const applyFilters = () => {
  currentPage.value = 1 // 重置到第一页
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

// 当前页改变
const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 强制取消订单
const forceCancelOrder = async (order) => {
  try {
    // 模拟权限校验：仅 role=2 的管理员可操作
    const currentUserRole = 2 // 假设当前用户是管理员
    if (currentUserRole !== 2) {
      ElMessage.error('权限不足，仅管理员可执行此操作')
      return
    }
    
    await ElMessageBox.confirm(
      `确定要强制取消订单「${order.id}」吗？此操作将处理退款¥${order.amount.toFixed(2)}，且不可恢复！`,
      '强制取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 更新订单状态
    const targetOrder = orders.value.find(item => item.id === order.id)
    if (targetOrder) {
      targetOrder.status = 'cancelled'
    }
    
    ElMessage({
      type: 'success',
      message: `订单「${order.id}」已取消，退款¥${order.amount.toFixed(2)}`
    })
    
    // 记录操作日志
    console.log('Force cancel order:', {
      orderId: order.id,
      userId: order.userId,
      refundAmount: order.amount,
      operator: '超级管理员',
      operatorId: 10001,
      timestamp: new Date().toLocaleString(),
      action: 'force_cancel_order'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Force cancel order error:', error)
    }
  }
}

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}

// 处理安全退出
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // 清空本地存储中的登录相关数据
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    
    // 跳转到登录页面并防止通过浏览器返回按钮返回
    router.replace('/login')
    
    ElMessage({
      type: 'success',
      message: '退出登录成功！'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Logout error:', error)
    } else {
      ElMessage({
        type: 'info',
        message: '已取消退出登录'
      })
    }
  }
}
</script>

<style scoped>
.order-management {
  display: flex;
  height: 100vh;
  background-color: #f7f8fa;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  background-color: #fff;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.sidebar-title {
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
}

.sidebar-menu {
  flex: 1;
  padding: 10px 0;
}

.menu-group {
  margin-bottom: 20px;
}

.group-title {
  padding: 8px 20px;
  font-size: 12px;
  color: #969799;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item:hover {
  background-color: #f7f8fa;
}

.menu-item.active {
  background-color: #e8f3ff;
  color: #1989fa;
  border-right: 3px solid #1989fa;
}

.menu-item .el-icon {
  margin-right: 10px;
}

.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-text {
  margin-left: 10px;
}

.user-name {
  font-size: 14px;
  font-weight: bold;
}

.user-id {
  font-size: 12px;
  color: #969799;
}

/* 右侧内容区 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.top-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.breadcrumb {
  font-size: 14px;
  color: #969799;
}

.top-right {
  display: flex;
  align-items: center;
}

.date-info {
  margin-right: 15px;
  font-size: 14px;
}

.bell-icon {
  margin-right: 15px;
  cursor: pointer;
}

/* 订单管控 */
.order-management-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
}

.filters {
  display: flex;
  gap: 10px;
}

.el-table {
  margin-top: 10px;
}

.order-table {
  --el-table-header-text-color: #303133;
  --el-table-row-hover-bg-color: #f5f7fa;
}

.order-table th,
.order-table td {
  text-align: left ;
}

.order-table th.el-table__cell,
.order-table td.el-table__cell {
  padding: 12px 8px; /* 调整内边距，让内容更贴近左侧 */
  border-bottom: 1px solid #ebeef5;
  text-align: left  /* 兜底确保左对齐，防止样式覆盖 */
}

/* 优化操作按钮间距，避免挤压 */
.order-table .el-button {
  margin: 0 2px;
}
</style>