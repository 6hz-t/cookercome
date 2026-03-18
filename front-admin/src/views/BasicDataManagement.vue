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
          <el-avatar :size="32" :src="adminInfo.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'"></el-avatar>
          <div class="user-text">
            <div class="user-name">{{ adminInfo.realName || '管理员' }}</div>
            <div class="user-id">ID: {{ adminInfo.userId || '-' }}</div>
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
                <el-input
                  v-model="cuisineSearch"
                  placeholder="搜索菜系名称"
                  class="search-input"
                  @keyup.enter="loadCuisineList"
                >
                  <template #prefix>
                    <el-icon size="16"><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="addCuisine">新增菜系</el-button>
              </div>
            </div>

            <el-table
              border
              :data="cuisineList"
              style="width: 100%;"
              v-loading="cuisineLoading"
              empty-text="暂无菜系数据，请点击「新增菜系」添加"
            >
              <el-table-column prop="id" label="菜系 ID" min-width="80" align="center" />
              <el-table-column prop="cuisineName" label="名称" min-width="120" align="center" />
              <el-table-column prop="description" label="描述" align="center" min-width="200" />
              <el-table-column prop="sort" label="排序" min-width="80" align="center">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.sort"
                    :min="0"
                    :max="999"
                    size="small"
                    controls-position="right"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="80" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.status === 1 ? 'success' : 'danger'"
                    disable-transitions
                  >
                    {{ scope.row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="200" align="center">
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
                      :type="scope.row.status === 1 ? 'warning' : 'success'"
                      @click="toggleCuisineStatus(scope.row)"
                    >
                      {{ scope.row.status === 1 ? '禁用' : '启用' }}
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

            <!-- 分页组件 -->
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="cuisinePage"
                v-model:page-size="cuisinePageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="cuisineTotal"
                @size-change="handleCuisineSizeChange"
                @current-change="handleCuisineCurrentChange"
              />
            </div>
          </el-tab-pane>

          <!-- 菜品管理 -->
          <el-tab-pane label="菜品管理" name="dish">
            <div class="section-header">
              <h3>菜品管理</h3>
              <div class="actions">
                <el-select
                  v-model="dishCuisineFilter"
                  placeholder="菜系筛选"
                  clearable
                  style="width: 120px; margin-right: 10px;"
                  @change="loadDishList"
                >
                  <el-option label="全部" value=""></el-option>
                  <el-option
                    v-for="item in cuisineList"
                    :key="item.id"
                    :label="item.cuisineName"
                    :value="item.id"
                  />
                </el-select>
                <el-input
                  v-model="dishSearch"
                  placeholder="搜索菜品名称"
                  class="search-input"
                  @keyup.enter="loadDishList"
                >
                  <template #prefix>
                    <el-icon size="16"><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="addDish">新增菜品</el-button>
              </div>
            </div>

            <el-table
              border
              :data="dishList"
              style="width: 100%;"
              v-loading="dishLoading"
              empty-text="暂无菜品数据，请点击「新增菜品」添加"
            >
              <el-table-column prop="id" label="菜品 ID" min-width="80" align="center" />
              <el-table-column prop="dishName" label="菜品名称" min-width="150" align="center" />
              <el-table-column prop="cuisineId" label="菜系" min-width="100" align="center">
                <template #default="scope">
                  {{ getCuisineName(scope.row.cuisineId) }}
                </template>
              </el-table-column>
              <el-table-column prop="dishType" label="类型" min-width="100" align="center">
                <template #default="scope">
                  {{ scope.row.dishType || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="price" label="价格" min-width="100" align="center">
                <template #default="scope">
                  ¥{{ (scope.row.price || 0).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="isFeatured" label="特色菜" min-width="80" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.isFeatured === 1 ? 'success' : 'info'"
                    disable-transitions
                    size="small"
                  >
                    {{ scope.row.isFeatured === 1 ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" align="center" min-width="200" show-overflow-tooltip />
              <el-table-column label="操作" min-width="180" align="center">
                <template #default="scope">
                  <div class="operation-cell">
                    <el-button
                      size="mini"
                      type="primary"
                      @click="editDish(scope.row)"
                    >
                      编辑
                    </el-button>
                    <el-button
                      size="mini"
                      type="danger"
                      @click="deleteDish(scope.row)"
                    >
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页组件 -->
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="dishPage"
                v-model:page-size="dishPageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="dishTotal"
                @size-change="handleDishSizeChange"
                @current-change="handleDishCurrentChange"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// 引入 Element Plus 图标
import {
  User, UserFilled, Grid, ShoppingCart, TrendCharts,
  Bell, Search
} from '@element-plus/icons-vue'
// 引入 Element Plus 提示框
import { ElMessageBox, ElMessage } from 'element-plus'
// 引入菜系管理接口
import {
  getCuisineList as getCuisineListApi,
  addCuisine as addCuisineApi,
  updateCuisine as updateCuisineApi,
  deleteCuisine as deleteCuisineApi
} from '@/api/cuisine'
// 引入菜品管理接口
import {
  getDishList as getDishListApi,
  addDish as addDishApi,
  updateDish as updateDishApi,
  deleteDish as deleteDishApi
} from '@/api/dish'
// 引入管理员信息 composable
import { useAdminInfo } from '@/composables/useAdminInfo'

// 创建路由器实例
const router = useRouter()

// 管理员信息
const { adminInfo } = useAdminInfo()

// 当前激活菜单
const activeMenu = ref('data')

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

// 当前激活的 tab
const activeTab = ref('cuisine')

// 动态面包屑文本
const breadcrumbText = computed(() => {
  const base = '系统设置 > 基础数据管理'
  return activeTab.value === 'cuisine' ? `${base} > 菜系管理` : `${base} > 菜品管理`
})

// 菜系相关
const cuisineLoading = ref(false)
const cuisineList = ref([])
const cuisinePage = ref(1)
const cuisinePageSize = ref(10)
const cuisineTotal = ref(0)
const cuisineSearch = ref('')

// 加载菜系列表
const loadCuisineList = () => {
  cuisineLoading.value = true
  getCuisineListApi({
    page: cuisinePage.value,
    size: cuisinePageSize.value,
    keyword: cuisineSearch.value
  }).then(res => {
    if (res) {
      cuisineList.value = res.records || []
      cuisineTotal.value = res.total || 0
    }
  }).catch(() => {
    cuisineList.value = []
    cuisineTotal.value = 0
  }).finally(() => {
    cuisineLoading.value = false
  })
}

// 处理菜系每页条数变化
const handleCuisineSizeChange = (val) => {
  cuisinePageSize.value = val
  cuisinePage.value = 1
  loadCuisineList()
}

// 处理菜系页码变化
const handleCuisineCurrentChange = (val) => {
  cuisinePage.value = val
  loadCuisineList()
}

// 根据菜系 ID 获取名称
const getCuisineName = (cuisineId) => {
  const cuisine = cuisineList.value.find(c => c.id === cuisineId)
  return cuisine ? cuisine.cuisineName : '-'
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

    const { value: description } = await ElMessageBox.prompt(
      '请输入菜系描述',
      '新增菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入描述（可选）',
        type: 'success'
      }
    ).catch(() => ({ value: '' }))

    // 计算最大排序值
    const maxSort = cuisineList.value.length > 0
      ? Math.max(...cuisineList.value.map(c => c.sort || 0))
      : 0

    await addCuisineApi({
      cuisineName: result.value,
      description: description || '',
      sort: maxSort + 1,
      status: 1
    })

    ElMessage.success(`菜系「${result.value}」已添加`)
    loadCuisineList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Add cuisine error:', error)
    }
  }
}

// 编辑菜系
const editCuisine = async (cuisine) => {
  try {
    const { value: cuisineName } = await ElMessageBox.prompt(
      '请输入菜系的新名称',
      '编辑菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: cuisine.cuisineName,
        inputPattern: /\S+/,
        inputErrorMessage: '菜系名称不能为空',
        type: 'primary'
      }
    )

    const { value: description } = await ElMessageBox.prompt(
      '请输入菜系描述',
      '编辑菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: cuisine.description || '',
        type: 'primary'
      }
    )

    await updateCuisineApi({
      id: cuisine.id,
      cuisineName,
      description,
      sort: cuisine.sort,
      status: cuisine.status
    })

    ElMessage.success(`菜系「${cuisine.cuisineName}」已更新`)
    loadCuisineList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Edit cuisine error:', error)
    }
  }
}

// 切换菜系状态
const toggleCuisineStatus = async (cuisine) => {
  try {
    const action = cuisine.status === 1 ? '禁用' : '启用'
    const actionType = cuisine.status === 1 ? 'warning' : 'success'
    const newStatus = cuisine.status === 1 ? 0 : 1

    await ElMessageBox.confirm(
      `确定要${action}「${cuisine.cuisineName}」吗？`,
      `${action}菜系`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: actionType
      }
    )

    await updateCuisineApi({
      id: cuisine.id,
      cuisineName: cuisine.cuisineName,
      description: cuisine.description,
      sort: cuisine.sort,
      status: newStatus
    })

    ElMessage({
      type: actionType,
      message: `菜系「${cuisine.cuisineName}」已${action}`
    })

    loadCuisineList()
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
      `确定要删除「${cuisine.cuisineName}」吗？此操作不可恢复！`,
      '删除菜系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )

    await deleteCuisineApi(cuisine.id)

    ElMessage({
      type: 'success',
      message: `菜系「${cuisine.cuisineName}」已删除`
    })

    loadCuisineList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete cuisine error:', error)
    }
  }
}

// 菜品相关
const dishLoading = ref(false)
const dishList = ref([])
const dishPage = ref(1)
const dishPageSize = ref(10)
const dishTotal = ref(0)
const dishSearch = ref('')
const dishCuisineFilter = ref('')

// 加载菜品列表
const loadDishList = () => {
  dishLoading.value = true
  getDishListApi({
    page: dishPage.value,
    size: dishPageSize.value,
    keyword: dishSearch.value,
    cuisineId: dishCuisineFilter.value === '' ? null : dishCuisineFilter.value
  }).then(res => {
    console.log('菜品列表响应:', res)
    if (res) {
      // MyBatis-Plus Page 对象序列化后是 records 字段
      dishList.value = res.records || res.list || []
      dishTotal.value = res.total || 0
      console.log('菜品列表:', dishList.value, '总数:', dishTotal.value)
    } else {
      console.warn('菜品列表响应为空')
      dishList.value = []
      dishTotal.value = 0
    }
  }).catch(err => {
    console.error('加载菜品列表失败:', err)
    dishList.value = []
    dishTotal.value = 0
  }).finally(() => {
    dishLoading.value = false
  })
}

// 处理菜品每页条数变化
const handleDishSizeChange = (val) => {
  dishPageSize.value = val
  dishPage.value = 1
  loadDishList()
}

// 处理菜品页码变化
const handleDishCurrentChange = (val) => {
  dishPage.value = val
  loadDishList()
}

// 新增菜品
const addDish = async () => {
  try {
    if (cuisineList.value.length === 0) {
      ElMessage.warning('请先添加菜系')
      return
    }

    const { value: dishName } = await ElMessageBox.prompt(
      '请输入菜品名称',
      '新增菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '菜品名称不能为空',
        type: 'success'
      }
    )

    // 选择菜系（简单提示）
    const cuisineText = cuisineList.value.map(c => `${c.id}-${c.cuisineName}`).join('，')
    const { value: cuisineIdStr } = await ElMessageBox.prompt(
      `请输入菜系 ID（${cuisineText}）`,
      '新增菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入数字',
        inputValue: '1',
        type: 'success'
      }
    )

    const cuisineId = parseInt(cuisineIdStr)
    // 验证菜系 ID 是否存在
    const cuisine = cuisineList.value.find(c => c.id === cuisineId)
    if (!cuisine) {
      ElMessage.error('菜系 ID 不存在')
      return
    }

    const { value: price } = await ElMessageBox.prompt(
      '请输入菜品价格',
      '新增菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+(\.\d{1,2})?$/,
        inputErrorMessage: '请输入正确的价格格式',
        inputValue: '0',
        type: 'success'
      }
    )

    const { value: dishType } = await ElMessageBox.prompt(
      '请输入菜品类型（热菜/凉菜/汤品/主食）',
      '新增菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入菜品类型',
        inputValue: '热菜',
        type: 'success'
      }
    ).catch(() => ({ value: '热菜' }))

    const { value: description } = await ElMessageBox.prompt(
      '请输入菜品描述',
      '新增菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入描述（可选）',
        type: 'success'
      }
    ).catch(() => ({ value: '' }))

    await addDishApi({
      dishName,
      cuisineId: cuisineId,
      price: parseFloat(price),
      dishType: dishType || '热菜',
      description: description || '',
      isFeatured: 0,
      userId: 2 // 默认使用测试厨师 ID
    })

    ElMessage.success(`菜品「${dishName}」已添加`)
    loadDishList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Add dish error:', error)
    }
  }
}

// 编辑菜品
const editDish = async (dish) => {
  try {
    const { value: dishName } = await ElMessageBox.prompt(
      '请输入菜品的新名称',
      '编辑菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: dish.dishName,
        inputPattern: /\S+/,
        inputErrorMessage: '菜品名称不能为空',
        type: 'primary'
      }
    )

    const { value: price } = await ElMessageBox.prompt(
      '请输入菜品价格',
      '编辑菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: String(dish.price || 0),
        inputPattern: /^\d+(\.\d{1,2})?$/,
        inputErrorMessage: '请输入正确的价格格式',
        type: 'primary'
      }
    )

    const { value: description } = await ElMessageBox.prompt(
      '请输入菜品描述',
      '编辑菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: dish.description || '',
        type: 'primary'
      }
    )

    await updateDishApi({
      id: dish.id,
      dishName,
      cuisineId: dish.cuisineId,
      price: parseFloat(price),
      description,
      isFeatured: dish.isFeatured
    })

    ElMessage.success(`菜品「${dish.dishName}」已更新`)
    loadDishList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Edit dish error:', error)
    }
  }
}

// 删除菜品
const deleteDish = async (dish) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${dish.dishName}」吗？此操作不可恢复！`,
      '删除菜品',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'danger'
      }
    )

    await deleteDishApi(dish.id)

    ElMessage({
      type: 'success',
      message: `菜品「${dish.dishName}」已删除`
    })

    loadDishList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete dish error:', error)
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

    localStorage.removeItem('admin-token')
    localStorage.removeItem('admin-refreshToken')
    localStorage.removeItem('admin-userInfo')

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

// 页面加载时加载数据
onMounted(() => {
  loadCuisineList()
  loadDishList()
})
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

.operation-cell {
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  gap: 8px;
  width: 100%;
  white-space: nowrap;
}

:deep(.el-table .el-input-number) {
  display: inline-flex !important;
  justify-content: center !important;
}

:deep(.el-table .el-tag) {
  display: inline-flex !important;
  justify-content: center !important;
  align-items: center !important;
}

:deep(.el-table .el-button) {
  margin: 2px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
