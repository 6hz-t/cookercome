<template>
  <div class="cooker-home">
    <el-container>
      <el-header class="top-header">
        <div class="brand">厨师端</div>

        <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" @select="handleSelect">
          <el-menu-item index="desk">工作台</el-menu-item>
          
          
          <el-menu-item index="todo">
            <el-badge :value="pendingCount" :hidden="pendingCount === 0" :max="99">待接单</el-badge>
          </el-menu-item>
          
          <el-menu-item index="message">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">消息中心</el-badge>
          </el-menu-item>
          <el-menu-item index="orders">历史订单</el-menu-item>
        </el-menu>

        <div class="right-tools">
          <el-tag :type="isOffline ? 'danger' : 'success'" effect="dark">{{ isOffline ? '离线' : '在线' }}</el-tag>
          <el-dropdown @command="handleUserMenuCommand">
            <el-button circle>
              <el-icon><User /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <el-alert
          v-if="isOffline"
          type="warning"
          title="网络连接已断开，已进入离线模式，部分操作将在网络恢复后可用"
          show-icon
          :closable="false"
          class="offline-alert"
        />
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getNewOrders } from '@/api/cooker'
import { getUnreadMessageCount, parseChefId } from '@/utils/cookerSession'

const router = useRouter()
const route = useRoute()

const activeIndex = ref('todo')
const pendingCount = ref(0)
const unreadCount = ref(0)
const isOffline = ref(typeof navigator !== 'undefined' ? !navigator.onLine : false)

let timer = null

const routeMap = {
  todo: '/cooker/todo',
  desk: '/cooker/desk',
  message: '/cooker/message',
  orders: '/cooker/orders'
}

function syncActiveFromPath(path) {
  const entry = Object.entries(routeMap).find(([, value]) => path.startsWith(value))
  activeIndex.value = entry ? entry[0] : 'desk'
}

async function loadPendingCount() {
  const chefId = parseChefId()
  if (!chefId) {
    pendingCount.value = 0
    return
  }
  try {
    const res = await getNewOrders(chefId)
    pendingCount.value = Array.isArray(res?.data) ? res.data.length : 0
  } catch (error) {
    pendingCount.value = 0
  }
}

function loadUnreadCount() {
  unreadCount.value = getUnreadMessageCount()
}

function handleSelect(key) {
  const path = routeMap[key]
  if (path) {
    router.push(path)
  }
}

function goProfile() {
  router.push('/cooker/profile')
}

function logout() {
  localStorage.removeItem('accesstoken')
  localStorage.removeItem('userId')
  localStorage.removeItem('userRole')
  localStorage.removeItem('cooker_online_state')
  ElMessage.success('已退出登录')
  router.replace('/cooker/login')
}

function handleUserMenuCommand(command) {
  if (command === 'profile') {
    goProfile()
    return
  }
  if (command === 'logout') {
    logout()
  }
}

function handleMessageEvent(event) {
  const detailCount = Number(event?.detail?.unreadCount)
  unreadCount.value = Number.isFinite(detailCount) ? detailCount : getUnreadMessageCount()
}

function handleNetworkChange() {
  isOffline.value = !navigator.onLine
}

onMounted(async () => {
  if (route.path === '/cooker' || route.path === '/cooker/') {
    router.replace('/cooker/desk')
  }

  syncActiveFromPath(route.path)
  loadUnreadCount()
  await loadPendingCount()

  timer = window.setInterval(loadPendingCount, 45000)
  window.addEventListener('cooker-message-updated', handleMessageEvent)
  window.addEventListener('online', handleNetworkChange)
  window.addEventListener('offline', handleNetworkChange)
})

onBeforeUnmount(() => {
  if (timer) {
    window.clearInterval(timer)
    timer = null
  }
  window.removeEventListener('cooker-message-updated', handleMessageEvent)
  window.removeEventListener('online', handleNetworkChange)
  window.removeEventListener('offline', handleNetworkChange)
})

watch(
  () => route.path,
  (path) => {
    syncActiveFromPath(path)
  }
)
</script>

<style scoped>
.top-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  background: linear-gradient(90deg, #ffffff 0%, #f4f8fc 100%);
  border-bottom: 1px solid #e5eaf2;
}

.brand {
  font-size: 18px;
  font-weight: 700;
  color: #243447;
  white-space: nowrap;
}

.nav-menu {
  border-bottom: none;
  min-width: 0;
}

.right-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.main-content {
  min-height: calc(100vh - 60px);
  padding-top: 16px;
  background: #f5f7fb;
}

.offline-alert {
  margin-bottom: 16px;
}

@media (max-width: 992px) {
  .top-header {
    grid-template-columns: 1fr auto;
    grid-template-areas:
      'brand right'
      'menu menu';
    height: auto;
    padding-top: 10px;
  }

  .brand {
    grid-area: brand;
  }

  .right-tools {
    grid-area: right;
    justify-content: flex-end;
  }

  .nav-menu {
    grid-area: menu;
  }
}
</style>
