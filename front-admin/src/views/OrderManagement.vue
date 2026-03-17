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
              placeholder="搜索订单号/客户 ID"
              class="search-input"
              @keyup.enter="loadOrderList"
            >
              <template #prefix>
                <el-icon size="16"><Search /></el-icon>
              </template>
            </el-input>
            <el-select
              v-model="filterStatus"
              placeholder="订单状态"
              clearable
              class="status-select"
              @change="loadOrderList"
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
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              class="date-range"
              @change="loadOrderList"
            />
            <el-button type="primary" @click="loadOrderList">查询</el-button>
          </div>
        </div>

        <el-table border :data="orderList" style="width: 100%;" class="order-table" v-loading="loading">
          <el-table-column prop="id" label="订单 ID" width="100" align="center" header-align="center" />
          <el-table-column prop="orderNo" label="订单号" width="180" align="center" header-align="center" />
          <el-table-column prop="customerName" label="客户姓名" width="100" align="center" header-align="center" />
          <el-table-column prop="customerPhone" label="手机号" width="130" align="center" header-align="center" />
          <el-table-column prop="chefName" label="厨师姓名" width="100" align="center" header-align="center" />
          <el-table-column prop="appointmentTime" label="预约时间" width="170" align="center" header-align="center">
            <template #default="scope">
              {{ scope.row.appointmentTime || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="100" align="center" header-align="center">
            <template #default="scope">
              <el-tag
                :type="getStatusType(scope.row.status)"
                disable-transitions
              >
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalFee" label="订单金额" width="100" align="center" header-align="center">
            <template #default="scope">
              ¥{{ (scope.row.totalFee || 0).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" width="170" align="center" header-align="center">
            <template #default="scope">
              {{ formatDateTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" header-align="center" fixed="right">
            <template #default="scope">
              <el-button
                v-if="scope.row.status === 5 || scope.row.status === 6"
                size="mini"
                type="info"
                disabled
              >
                已取消/退款
              </el-button>
              <el-button
                v-else-if="scope.row.status === 0"
                size="mini"
                type="danger"
                @click="forceCancelOrder(scope.row)"
              >
                强制取消
              </el-button>
              <el-button
                v-else
                size="mini"
                type="primary"
                @click="viewOrderDetail(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// 引入 Element Plus 图标
import {
  User, UserFilled, Grid, ShoppingCart, TrendCharts,
  Bell, Search
} from '@element-plus/icons-vue'
// 引入 Element Plus 提示框
import { ElMessageBox, ElMessage } from 'element-plus'
// 引入订单管理接口
import { getOrderList, forceCancelOrder as forceCancelOrderApi } from '@/api/order'

// 创建路由器实例
const router = useRouter()

// 当前激活菜单
const activeMenu = ref('order')

// 当前日期（动态获取）
const currentDate = ref('')

// 格式化日期为 YYYY 年 MM 月 DD 日
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

// 初始化当前日期
currentDate.value = formatDate(new Date())

// 搜索和筛选参数
const searchKeyword = ref('')
const filterStatus = ref('')
const dateRange = ref([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载状态
const loading = ref(false)

// 订单列表
const orderList = ref([])

// 订单状态选项
const statusOptions = [
  { value: 0, label: '待支付' },
  { value: 1, label: '待接单' },
  { value: 2, label: '已接单' },
  { value: 3, label: '服务中' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' },
  { value: 6, label: '已退款' }
]

// 加载订单列表
const loadOrderList = () => {
  loading.value = true
  getOrderList({
    page: currentPage.value,
    size: pageSize.value,
    keyword: searchKeyword.value,
    status: filterStatus.value === '' ? null : filterStatus.value,
    startDate: dateRange.value ? dateRange.value[0] : null,
    endDate: dateRange.value ? dateRange.value[1] : null
  }).then(res => {
    if (res.data) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  }).catch(() => {
    orderList.value = []
    total.value = 0
  }).finally(() => {
    loading.value = false
  })
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case 0: return 'warning'
    case 1: return 'info'
    case 2: return 'primary'
    case 3: return ''
    case 4: return 'success'
    case 5: return 'info'
    case 6: return 'info'
    default: return 'info'
  }
}

// 获取状态标签
const getStatusLabel = (status) => {
  switch(status) {
    case 0: return '待支付'
    case 1: return '待接单'
    case 2: return '已接单'
    case 3: return '服务中'
    case 4: return '已完成'
    case 5: return '已取消'
    case 6: return '已退款'
    default: return '未知'
  }
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadOrderList()
}

// 当前页改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadOrderList()
}

// 强制取消订单
const forceCancelOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入取消原因',
      '强制取消订单',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入取消原因',
        inputValidator: (value) => {
          if (!value) return '取消原因不能为空'
          return true
        },
        type: 'warning'
      }
    )

    await forceCancelOrderApi(order.id, reason)

    ElMessage({
      type: 'success',
      message: `订单「${order.orderNo}」已取消，退款¥${(order.totalFee || 0).toFixed(2)}`
    })

    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Force cancel order error:', error)
    }
  }
}

// 查看订单详情
const viewOrderDetail = (order) => {
  ElMessageBox.alert(
    `
      <div style="text-align: left; line-height: 2;">
        <strong>订单号：</strong>${order.orderNo}<br/>
        <strong>订单 ID：</strong>${order.id}<br/>
        <strong>客户姓名：</strong>${order.customerName || '-'}<br/>
        <strong>客户手机：</strong>${order.customerPhone || '-'}<br/>
        <strong>厨师姓名：</strong>${order.chefName || '-'}<br/>
        <strong>预约时间：</strong>${order.appointmentTime || '-'}<br/>
        <strong>订单状态：</strong>${getStatusLabel(order.status)}<br/>
        <strong>订单金额：</strong>¥${(order.totalFee || 0).toFixed(2)}<br/>
        <strong>下单时间：</strong>${formatDateTime(order.createTime)}<br/>
      </div>
    `,
    '订单详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
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

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')

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

// 页面加载时加载订单列表
onMounted(() => {
  loadOrderList()
})
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
  align-items: center;
}

.search-input {
  width: 220px;
}

.status-select {
  width: 120px;
}

.date-range {
  width: 240px;
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
  text-align: center;
}

.order-table th.el-table__cell,
.order-table td.el-table__cell {
  padding: 12px 8px;
  border-bottom: 1px solid #ebeef5;
}

.order-table .el-button {
  margin: 0 2px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
