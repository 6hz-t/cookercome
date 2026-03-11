<template>
  <div class="basic-data-management">
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
        <!-- 动态面包屑 -->
        <div class="breadcrumb">{{ breadcrumbText }}</div>
        <div class="top-right">
          <div class="date-info">当前日期 {{ currentDate }}</div>
          <!-- 修复图标属性传递 -->
          <el-icon class="bell-icon">
            <Bell size="20" />
          </el-icon>
          <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">安全退出</el-button>
        </div>
      </div>

      <!-- 数据管理 tabs -->
      <div class="data-management-section">
        <el-tabs v-model="activeTab" type="card">
          <!-- 菜系管理 -->
          <el-tab-pane label="菜系管理" name="cuisine">
            <div class="section-header">
              <h3>菜系管理</h3>
              <div class="actions">
                <!-- 修复搜索框前缀图标 & 样式类封装 -->
                <el-input
                  v-model="cuisineSearch"
                  placeholder="搜索菜系名称"
                  class="search-input"
                >
                  <template #prefix>
                    <el-icon size="16"><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="addCuisine">新增菜系</el-button>
                <el-button type="success" @click="saveSortOrder">保存排序</el-button>
              </div>
            </div>
            
            <!-- 修复表格列宽度 + 空数据占位 -->
            <el-table 
              border 
              :data="filteredCuisines" 
              style="width: 100%;"
              empty-text="暂无菜系数据，请点击「新增菜系」添加"
            >
              <el-table-column prop="id" label="菜系ID" min-width="100" align="center" />
              <el-table-column prop="name" label="名称" min-width="150" align="center" />
              <el-table-column prop="sortOrder" label="排序值" min-width="100" align="center">
                <template #default="scope">
                  <el-input-number 
                    v-model="scope.row.sortOrder" 
                    :min="1" 
                    :max="999" 
                    size="small"
                    @change="updateSortOrder(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="100" align="center">
                <template #default="scope">
                  <el-tag 
                    :type="scope.row.status === 'enabled' ? 'success' : 'danger'"
                    disable-transitions
                  >
                    {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <!-- 修复操作列宽度 + 防止按钮换行 -->
              <el-table-column label="操作" min-width="220" align="center">
                <template #default="scope">
                  <div class="operation-cell">
                    <el-button 
                      size="mini" 
                      type="primary" 
                      @click="editCuisine(scope.row)"
                    >
                      编辑
                    </el-button>
                    <el-button 
                      size="mini" 
                      :type="scope.row.status === 'enabled' ? 'warning' : 'success'" 
                      @click="toggleCuisineStatus(scope.row)"
                    >
                      {{ scope.row.status === 'enabled' ? '禁用' : '启用' }}
                    </el-button>
                    <el-button 
                      size="mini" 
                      type="danger" 
                      @click="deleteCuisine(scope.row)"
                    >
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 角色字典管理 -->
          <el-tab-pane label="角色字典管理" name="role">
            <div class="section-header">
              <h3>角色字典管理</h3>
              <div class="actions">
                <!-- 修复搜索框前缀图标 & 样式类封装 -->
                <el-input
                  v-model="roleSearch"
                  placeholder="搜索角色名称"
                  class="search-input"
                >
                  <template #prefix>
                    <el-icon size="16"><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="addRole">新增角色</el-button>
              </div>
            </div>
            
            <!-- 修复表格列宽度 + 空数据占位 -->
            <el-table 
              border 
              :data="filteredRoles" 
              style="width: 100%;"
              empty-text="暂无角色数据，请点击「新增角色」添加"
            >
              <el-table-column prop="id" label="角色ID" min-width="100" align="center" />
              <el-table-column prop="name" label="名称" min-width="150" align="center" />
              <el-table-column prop="description" label="描述" align="center" />
              <!-- 修复操作列宽度 -->
              <el-table-column label="操作" min-width="180" align="center">
                <template #default="scope">
                  <div class="operation-cell">
                    <el-button 
                      size="mini" 
                      type="primary" 
                      @click="editRole(scope.row)"
                    >
                      编辑
                    </el-button>
                    <el-button 
                      size="mini" 
                      type="danger" 
                      @click="deleteRole(scope.row)"
                    >
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
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
const activeMenu = ref('data')

// 当前日期
const currentDate = ref('2026年03月06日')

// 当前激活的tab
const activeTab = ref('cuisine')

// 动态面包屑文本
const breadcrumbText = computed(() => {
  const base = '系统设置 > 基础数据管理'
  return activeTab.value === 'cuisine' ? `${base} > 菜系管理` : `${base} > 角色字典管理`
})

// 搜索关键字
const cuisineSearch = ref('')
const roleSearch = ref('')

// 模拟菜系数据
const cuisines = ref([
  { id: 30001, name: '川菜', sortOrder: 1, status: 'enabled', description: '四川风味菜系' },
  { id: 30002, name: '粤菜', sortOrder: 2, status: 'enabled', description: '广东风味菜系' },
  { id: 30003, name: '湘菜', sortOrder: 3, status: 'disabled', description: '湖南风味菜系' },
  { id: 30004, name: '鲁菜', sortOrder: 4, status: 'enabled', description: '山东风味菜系' },
  { id: 30005, name: '苏菜', sortOrder: 5, status: 'enabled', description: '江苏风味菜系' },
])

// 模拟角色数据
const roles = ref([
  { id: 1, name: '超级管理员', description: '拥有系统最高权限' },
  { id: 2, name: '管理员', description: '拥有大部分管理权限' },
  { id: 3, name: '普通用户', description: '一般用户权限' },
  { id: 4, name: '审核员', description: '负责审核工作' },
])

// 计算属性：过滤后的菜系列表
const filteredCuisines = computed(() => {
  if (!cuisineSearch.value) {
    // 按排序值从小到大排序
    return [...cuisines.value].sort((a, b) => a.sortOrder - b.sortOrder);
  }
  return cuisines.value
    .filter(cuisine => 
      cuisine.name.includes(cuisineSearch.value)
    )
    .sort((a, b) => a.sortOrder - b.sortOrder);
})

// 计算属性：过滤后的角色列表
const filteredRoles = computed(() => {
  if (!roleSearch.value) return roles.value
  return roles.value.filter(role => 
    role.name.includes(roleSearch.value)
  )
})

// 保存排序
const saveSortOrder = () => {
  // 按当前排序整理数据
  const sortedCuisines = [...cuisines.value].sort((a, b) => a.sortOrder - b.sortOrder);
  
  // 输出排序结果到控制台
  console.log('Save sort order:', sortedCuisines.map(cuisine => ({
    id: cuisine.id,
    name: cuisine.name,
    sortOrder: cuisine.sortOrder
  })));
  
  ElMessage.success('菜系排序已保存');
  
  // 记录操作日志
  console.log('Save cuisine sort order:', {
    operator: '超级管理员',
    timestamp: new Date().toLocaleString(),
    action: 'save_sort_order',
    sortedCuisines: sortedCuisines.map(cuisine => ({
      id: cuisine.id,
      name: cuisine.name,
      sortOrder: cuisine.sortOrder
    }))
  });
}

// 更新菜系排序
const updateSortOrder = (cuisine) => {
  console.log('Update sort order:', cuisine)
  ElMessage.success(`菜系「${cuisine.name}」排序已更新为 ${cuisine.sortOrder}`)
  
  // 记录操作日志
  console.log('Update cuisine sort order:', {
    cuisineId: cuisine.id,
    cuisineName: cuisine.name,
    newSortOrder: cuisine.sortOrder,
    operator: '超级管理员',
    timestamp: new Date().toLocaleString(),
    action: 'update_sort_order'
  })
}

// 新增菜系
const addCuisine = async () => {
  try {
    const result = await ElMessageBox.prompt(
      '请输入新菜系的名称',
      '新增菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '菜系名称不能为空',
        type: 'success'
      }
    )
    
    const newCuisine = {
      id: cuisines.value.length + 30000,
      name: result.value,
      sortOrder: Math.max(...cuisines.value.map(c => c.sortOrder)) + 1,
      status: 'enabled',
      description: '新添加的菜系'
    }
    
    cuisines.value.push(newCuisine)
    
    ElMessage.success(`菜系「${result.value}」已添加`)
    
    // 记录操作日志
    console.log('Add cuisine:', {
      cuisineId: newCuisine.id,
      cuisineName: newCuisine.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'add_cuisine'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Add cuisine error:', error)
    }
  }
}

// 编辑菜系
const editCuisine = async (cuisine) => {
  try {
    const result = await ElMessageBox.prompt(
      '请输入菜系的新名称',
      '编辑菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: cuisine.name,
        inputPattern: /\S+/,
        inputErrorMessage: '菜系名称不能为空',
        type: 'primary'
      }
    )
    
    const oldName = cuisine.name
    cuisine.name = result.value
    
    ElMessage.success(`菜系「${oldName}」已更新为「${result.value}」`)
    
    // 记录操作日志
    console.log('Edit cuisine:', {
      cuisineId: cuisine.id,
      oldName: oldName,
      newName: result.value,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'edit_cuisine'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Edit cuisine error:', error)
    }
  }
}

// 切换菜系状态
const toggleCuisineStatus = async (cuisine) => {
  try {
    const action = cuisine.status === 'enabled' ? '禁用' : '启用'
    const actionType = cuisine.status === 'enabled' ? 'warning' : 'success'
    
    await ElMessageBox.confirm(
      `确定要${action}「${cuisine.name}」吗？`,
      `${action}菜系`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: actionType
      }
    )
    
    cuisine.status = cuisine.status === 'enabled' ? 'disabled' : 'enabled'
    
    ElMessage({
      type: actionType,
      message: `菜系「${cuisine.name}」已${action}`
    })
    
    // 记录操作日志
    console.log('Toggle cuisine status:', {
      cuisineId: cuisine.id,
      cuisineName: cuisine.name,
      newStatus: cuisine.status,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: `toggle_cuisine_${cuisine.status}`
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Toggle cuisine status error:', error)
    }
  }
}

// 删除菜系
const deleteCuisine = async (cuisine) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${cuisine.name}」吗？此操作不可恢复！`,
      '删除菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )
    
    const index = cuisines.value.findIndex(item => item.id === cuisine.id)
    if (index !== -1) {
      cuisines.value.splice(index, 1)
    }
    
    ElMessage({
      type: 'success',
      message: `菜系「${cuisine.name}」已删除`
    })
    
    // 记录操作日志
    console.log('Delete cuisine:', {
      cuisineId: cuisine.id,
      cuisineName: cuisine.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'delete_cuisine'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete cuisine error:', error)
    }
  }
}

// 新增角色
const addRole = async () => {
  try {
    const { value: name } = await ElMessageBox.prompt(
      '请输入角色名称',
      '新增角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '角色名称不能为空',
        type: 'success'
      }
    )
    
    const { value: description } = await ElMessageBox.prompt(
      '请输入角色描述',
      '新增角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '角色描述不能为空',
        type: 'success'
      }
    )
    
    const newRole = {
      id: roles.value.length + 1,
      name: name,
      description: description
    }
    
    roles.value.push(newRole)
    
    ElMessage.success(`角色「${name}」已添加`)
    
    // 记录操作日志
    console.log('Add role:', {
      roleId: newRole.id,
      roleName: newRole.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'add_role'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Add role error:', error)
    }
  }
}

// 编辑角色
const editRole = async (role) => {
  try {
    const { value: name } = await ElMessageBox.prompt(
      '请输入角色的新名称',
      '编辑角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: role.name,
        inputPattern: /\S+/,
        inputErrorMessage: '角色名称不能为空',
        type: 'primary'
      }
    )
    
    const { value: description } = await ElMessageBox.prompt(
      '请输入角色的新描述',
      '编辑角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: role.description,
        inputPattern: /\S+/,
        inputErrorMessage: '角色描述不能为空',
        type: 'primary'
      }
    )
    
    const oldName = role.name
    role.name = name
    role.description = description
    
    ElMessage.success(`角色「${oldName}」已更新`)
    
    // 记录操作日志
    console.log('Edit role:', {
      roleId: role.id,
      oldName: oldName,
      newName: name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'edit_role'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Edit role error:', error)
    }
  }
}

// 删除角色
const deleteRole = async (role) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${role.name}」吗？此操作不可恢复！`,
      '删除角色',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )
    
    const index = roles.value.findIndex(item => item.id === role.id)
    if (index !== -1) {
      roles.value.splice(index, 1)
    }
    
    ElMessage({
      type: 'success',
      message: `角色「${role.name}」已删除`
    })
    
    // 记录操作日志
    console.log('Delete role:', {
      roleId: role.id,
      roleName: role.name,
      operator: '超级管理员',
      timestamp: new Date().toLocaleString(),
      action: 'delete_role'
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete role error:', error)
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
.basic-data-management {
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

/* 数据管理 */
.data-management-section {
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

.actions {
  display: flex;
  gap: 10px;
}

/* 搜索框样式封装 + 响应式 */
.search-input {
  width: 200px;
  margin-right: 10px;
}

@media (max-width: 1200px) {
  .search-input {
    width: 160px;
  }
}

.el-table {
  margin-top: 10px;
}

/* 样式穿透修复 - 表格内容居中 */
:deep(.el-table th.el-table__cell), 
:deep(.el-table td.el-table__cell) {
  text-align: center !important;
  padding: 12px 0 !important;
}

:deep(.el-table .cell) {
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  padding: 0 10px;
}

/* 操作列按钮组居中 + 防止换行 */
.operation-cell {
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  gap: 8px;
  width: 100%;
  white-space: nowrap;
}

/* 输入框居中 */
:deep(.el-table .el-input-number) {
  display: inline-flex !important;
  justify-content: center !important;
}

/* 标签居中 */
:deep(.el-table .el-tag) {
  display: inline-flex !important;
  justify-content: center !important;
  align-items: center !important;
}

/* 按钮居中 */
:deep(.el-table .el-button) {
  margin: 2px;
}
</style>