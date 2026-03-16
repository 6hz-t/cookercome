<template>
  <div class="dashboard">
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
            @click="navigateToAccount"
          >
            <el-icon><User /></el-icon>
            <span>账号管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'chef' }"
            @click="navigateToChefAudit"
          >
            <el-icon><UserFilled /></el-icon>
            <span>厨师审核</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'data' }"
            @click="navigateToDataManagement"
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
            @click="navigateToOrderControl"
          >
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管控</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'stats' }"
            @click="navigateToStatistics"
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
        <div class="breadcrumb">系统设置 &gt; 账号管控</div>
        <div class="top-right">
          <div class="date-info">当前日期 {{ currentDate }}</div>
          <el-icon size="20" class="bell-icon"><Bell /></el-icon>
          <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">安全退出</el-button>
        </div>
      </div>

      <!-- 数据概览卡片 -->
      <div class="overview-cards">
        <el-card class="card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">总用户数</div>
              <div class="card-value">{{ totalUsers }}</div>
              <div class="card-tip" style="color: #07c160;">↗ 较昨日 +12%</div>
            </div>
            <div class="card-icon" style="background-color: #e8f3ff;">
              <el-icon size="24" color="#1989fa"><User /></el-icon>
            </div>
          </div>
        </el-card>
        <el-card class="card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">待审核厨师</div>
              <div class="card-value">{{ pendingChefs }}</div>
              <div class="card-tip" style="color: #ff6b35;">⚠️ 需尽快处理</div>
            </div>
            <div class="card-icon" style="background-color: #fff2e8;">
              <el-icon size="24" color="#ff6b35;"><Shop /></el-icon>
            </div>
          </div>
        </el-card>
        <el-card class="card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">今日订单</div>
              <div class="card-value">{{ todayOrders }}</div>
              <div class="card-tip" style="color: #07c160;">↗ 较昨日 +5.4%</div>
            </div>
            <div class="card-icon" style="background-color: #e6fffa;">
              <el-icon size="24" color="#00b42a;"><ShoppingCart /></el-icon>
            </div>
          </div>
        </el-card>
        <el-card class="card">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">异常订单</div>
              <div class="card-value">{{ abnormalOrders }}</div>
              <div class="card-tip">状态稳定</div>
            </div>
            <div class="card-icon" style="background-color: #fff0f0;">
              <el-icon size="24" color="#f53f3f;"><InfoFilled /></el-icon>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 用户列表（账号管控核心功能） -->
      <div class="user-list-section">
        <div class="section-header">
          <h3>用户列表</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户名/ID"
            prefix-icon="Search"
            style="width: 300px; margin-left: 20px;"
          ></el-input>
        </div>
        <el-table border :data="filteredUserList" style="width: 100%;" v-loading="loading">
          <el-table-column prop="id" label="用户ID" />
          <el-table-column prop="name" label="用户名" />
          <el-table-column prop="role" label="角色">
            <template #default="scope">
              {{ scope.row.role === 2 ? '管理员' : '普通用户' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
                {{ scope.row.status === 'active' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="mini" type="primary" @click="toggleUserStatus(scope.row)">
                {{ scope.row.status === 'active' ? '禁用' : '启用' }}
              </el-button>
              <el-button size="mini" type="warning" @click="resetPassword(scope.row)">重置密码</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// 引入Element Plus图标
import { 
  User, UserFilled, Grid, ShoppingCart, TrendCharts, 
  Bell, InfoFilled, Shop, Search 
} from '@element-plus/icons-vue'
// 引入Element Plus提示框
import { ElMessageBox, ElMessage } from 'element-plus'
// 导入接口方法
import { getUserList, changeUserStatus, resetPassword } from '@/api/user'

// 创建路由器实例
const router = useRouter()

// 当前激活菜单
const activeMenu = ref('account')

// 当前日期
const currentDate = ref('2026年03月06日')

// 概览数据
const totalUsers = ref(12842)
const pendingChefs = ref(45)
const todayOrders = ref(856)
const abnormalOrders = ref(3)

// 搜索关键词
const searchKeyword = ref('')

// 用户列表（从后端获取）
const userList = ref([])
const loading = ref(false)

// 页面加载时获取用户列表
onMounted(() => {
  loadUserList()
})

// 加载用户列表
const loadUserList = () => {
  loading.value = true
  // 传递分页+搜索参数
  getUserList({
    keyword: searchKeyword.value,
    pageNum: 1,
    pageSize: 10
  }).then(res => {
    // 后端返回的data里是分页数据，取records（用户列表）
    userList.value = res.data.records || []
  }).finally(() => {
    loading.value = false
  })
}

// 切换用户状态（调用后端接口）
const toggleUserStatus = (row) => {
  ElMessageBox.confirm(
    `确定要${row.status === 'active' ? '禁用' : '启用'}用户「${row.name}」吗？`,
    '提示',
    { 
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 调用切换状态接口
    changeUserStatus(row.id, row.status === 'active' ? 'disabled' : 'active').then(() => {
      ElMessage.success(`用户「${row.name}」已${row.status === 'active' ? '禁用' : '启用'}！`)
      loadUserList() // 重新加载列表
    }).catch(error => {
      console.error('切换用户状态失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    })
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}

// 重置密码（调用后端接口）
const resetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置用户「${row.name}」的密码吗？重置后密码将恢复为默认值`,
    '警告',
    { 
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    resetPassword(row.id).then(() => {
      ElMessage.success(`用户「${row.name}」密码已重置！`)
    }).catch(error => {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败，请稍后重试')
    })
  }).catch(() => {
    ElMessage.info('已取消重置密码')
  })
}

// 过滤后的用户列表（保持不变）
const filteredUserList = computed(() => {
  if (!searchKeyword.value) return userList.value
  return userList.value.filter(user => 
    user.name.includes(searchKeyword.value) || 
    user.id.toString().includes(searchKeyword.value)
  )
})

// 导航到不同页面的函数
const navigateToAccount = () => {
  activeMenu.value = 'account'
  router.push('/dashboard')
}

const navigateToChefAudit = () => {
  activeMenu.value = 'chef'
  router.push('/chef-audit')
}

const navigateToDataManagement = () => {
  activeMenu.value = 'data'
  router.push('/data-management')
}

const navigateToOrderControl = () => {
  activeMenu.value = 'order'
  router.push('/order-control')
}

const navigateToStatistics = () => {
  activeMenu.value = 'stats'
  router.push('/statistics')
}

// 处理安全退出
const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
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
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消退出登录'
    })
  })
}

// 监听搜索关键词变化
watch(searchKeyword, () => {
  loadUserList()
})
</script>

<style scoped>
.dashboard {
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

/* 数据概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.card-tip {
  font-size: 12px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 用户列表 */
.user-list-section {
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
</style>