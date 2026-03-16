<template>
  <div class="chefInfo-audit">
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
            :class="{ active: activeMenu === 'chefInfo' }"
            @click="navigateTo('/chefInfo-audit')"
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
        <div class="breadcrumb">系统设置 &gt; 厨师审核</div>
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
              <div class="card-title">待审核厨师</div>
              <div class="card-value">{{ pendingChefs }}</div>
              <div class="card-tip" style="color: #ff6b35;">⚠️ 需尽快处理</div>
            </div>
            <div class="card-icon" style="background-color: #fff2e8;">
              <el-icon size="24" color="#ff6b35;"><Shop /></el-icon>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 厨师审核列表 -->
      <div class="audit-section">
        <div class="section-header">
          <h3>待审核厨师</h3>
          <div class="filter-and-search">
            <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value=""></el-option>
              <el-option label="待审核" value="pending"></el-option>
              <el-option label="已通过" value="approved"></el-option>
              <el-option label="已拒绝" value="rejected"></el-option>
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索厨师姓名/ID"
              prefix-icon="Search"
              style="width: 300px;"
            ></el-input>
          </div>
        </div>
        
        <el-table border :data="filteredChefList" style="width: 100%;">
          <el-table-column prop="id" label="厨师ID" width="100" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="手机号" width="150" />
          <el-table-column prop="applyTime" label="申请时间" width="180" />
          <el-table-column prop="auditStatus" label="审核状态" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusType(scope.row.auditStatus)"
                disable-transitions
              >
                {{ getStatusText(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="rejectReason" label="拒绝原因" v-if="showRejectReasonColumn" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button 
                size="mini" 
                type="success" 
                @click="approveChef(scope.row)"
                :disabled="scope.row.auditStatus !== 'pending'"
              >
                通过
              </el-button>
              <el-button 
                size="mini" 
                type="danger" 
                @click="rejectChef(scope.row)"
                :disabled="scope.row.auditStatus !== 'pending'"
              >
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
const activeMenu = ref('chefInfo')

// 当前日期
const currentDate = ref('2026年03月06日')

// 待审核厨师数量
const pendingChefs = ref(45)

// 筛选条件
const filterStatus = ref('')
const searchKeyword = ref('')

// 厨师列表数据
const chefList = ref([
  { id: 20001, name: '张大厨', phone: '13800138001', applyTime: '2026-03-01 10:30:25', auditStatus: 'pending', rejectReason: '' },
  { id: 20002, name: '李师傅', phone: '13900139002', applyTime: '2026-03-02 14:22:10', auditStatus: 'pending', rejectReason: '' },
  { id: 20003, name: '王厨师', phone: '13700137003', applyTime: '2026-02-28 09:15:33', auditStatus: 'approved', rejectReason: '' },
  { id: 20004, name: '赵大师', phone: '13600136004', applyTime: '2026-03-03 16:45:12', auditStatus: 'rejected', rejectReason: '资质证明不全' },
  { id: 20005, name: '陈大厨', phone: '13500135005', applyTime: '2026-03-04 11:20:45', auditStatus: 'pending', rejectReason: '' },
  { id: 20006, name: '刘师傅', phone: '13400134006', applyTime: '2026-03-05 08:30:20', auditStatus: 'approved', rejectReason: '' },
])

// 计算属性：过滤后的厨师列表
const filteredChefList = computed(() => {
  let result = chefList.value
  
  // 按状态筛选
  if (filterStatus.value) {
    result = result.filter(chefInfo => chefInfo.auditStatus === filterStatus.value)
  }
  
  // 按姓名或ID搜索
  if (searchKeyword.value) {
    result = result.filter(chefInfo =>
      chefInfo.name.includes(searchKeyword.value) ||
      chefInfo.id.toString().includes(searchKeyword.value)
    )
  }
  
  return result
})

// 计算属性：是否显示拒绝原因列
const showRejectReasonColumn = computed(() => {
  return filterStatus.value === 'rejected' || 
         (filterStatus.value === '' && 
          chefList.value.some(chefInfo => chefInfo.auditStatus === 'rejected'))
})

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'pending': return '待审核'
    case 'approved': return '已通过'
    case 'rejected': return '已拒绝'
    default: return '未知'
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch(status) {
    case 'pending': return 'warning'
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    default: return 'info'
  }
}

// 审核通过
const approveChef = async (chefInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过「${chefInfo.name}」的厨师申请吗？`,
      '审核通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    // 更新厨师审核状态
    chefInfo.auditStatus = 'approved'
    pendingChefs.value -= 1
    
    ElMessage.success(`已通过「${chefInfo.name}」的厨师申请`)
    
    // 记录操作日志
    console.log('Approve chefInfo:', {
      chefId: chefInfo.id,
      chefName: chefInfo.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'approve'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Approve error:', error)
    }
  }
}

// 审核拒绝
const rejectChef = async (chefInfo) => {
  let reason = ''
  
  try {
    const result = await ElMessageBox.prompt(
      '请输入拒绝原因',
      '审核拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value) return '拒绝原因不能为空'
          return true
        },
        type: 'warning'
      }
    )
    
    reason = result.value
    
    // 更新厨师审核状态
    chefInfo.auditStatus = 'rejected'
    chefInfo.rejectReason = reason
    pendingChefs.value -= 1
    
    ElMessage.warning(`已拒绝「${chefInfo.name}」的厨师申请，原因：${reason}`)
    
    // 记录操作日志
    console.log('Reject chefInfo:', {
      chefId: chefInfo.id,
      chefName: chefInfo.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'reject',
      reason: reason
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Reject error:', error)
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
.chefInfo-audit {
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

/* 审核列表 */
.audit-section {
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

.filter-and-search {
  display: flex;
  gap: 10px;
}
</style>