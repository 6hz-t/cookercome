<template>
  <div class="page">
    <el-card shadow="never" class="head-card">
      <div class="head-row">
        <div>
          <h3>消息中心</h3>
          <p>派单、客户聊天、系统通知、评价提醒统一处理</p>
        </div>
        <el-button @click="markAllAsRead">全部标为已读</el-button>
      </div>
    </el-card>

    <el-card shadow="hover">
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="`订单消息 (${unreadOrderCount})`" name="order">
          <div class="list-wrap">
            <el-empty v-if="orderMessages.length === 0" description="暂无订单消息" />
            <el-card
              v-for="item in orderMessages"
              :key="item.id"
              class="msg-card"
              shadow="never"
              :class="{ unread: !item.read }"
            >
              <div class="msg-head">
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </div>
                <div class="msg-actions">
                  <el-tag :type="item.read ? 'info' : 'danger'" size="small">{{ item.read ? '已读' : '未读' }}</el-tag>
                  <el-button size="small" link @click="openOrder(item.orderId)">查看订单</el-button>
                  <el-button size="small" link @click="markRead(item, 'order')">标记已读</el-button>
                </div>
              </div>
              <div class="time">{{ item.time }}</div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`客户聊天 (${unreadChatCount})`" name="chat">
          <div class="chat-layout">
            <aside class="thread-list">
              <div
                v-for="thread in chatThreads"
                :key="thread.id"
                class="thread-item"
                :class="{ active: thread.id === activeThreadId }"
                @click="activeThreadId = thread.id"
              >
                <strong>{{ thread.customerName }}</strong>
                <p>{{ thread.lastMessage }}</p>
                <el-badge :value="thread.unread" :hidden="thread.unread === 0" />
              </div>
            </aside>

            <section class="chat-panel" v-if="activeThread">
              <div class="chat-head">
                <strong>{{ activeThread.customerName }}</strong>
                <el-button size="small" @click="markThreadRead(activeThread.id)">设为已读</el-button>
              </div>

              <div class="chat-body">
                <div
                  v-for="message in activeThread.messages"
                  :key="message.id"
                  class="bubble"
                  :class="message.from === 'chef' ? 'self' : 'other'"
                >
                  <template v-if="message.type === 'image'">
                    <img :src="message.content" alt="chat-img" />
                  </template>
                  <template v-else>
                    {{ message.content }}
                  </template>
                </div>
              </div>

              <div class="chat-input">
                <el-input v-model="chatInput" type="textarea" :rows="2" placeholder="输入消息，支持文本/图片" />
                <div class="chat-tools">
                  <el-upload :auto-upload="false" :show-file-list="false" :on-change="handleImageChange">
                    <el-button>发送图片</el-button>
                  </el-upload>
                  <el-button @click="sendVoiceHint">发送语音</el-button>
                  <el-button type="primary" @click="sendText">发送</el-button>
                </div>
              </div>
            </section>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`系统通知 (${unreadSystemCount})`" name="system">
          <div class="list-wrap">
            <el-empty v-if="systemMessages.length === 0" description="暂无系统通知" />
            <el-card v-for="item in systemMessages" :key="item.id" class="msg-card" shadow="never" :class="{ unread: !item.read }">
              <div class="msg-head">
                <div>
                  <strong>{{ item.title }}</strong>
                  <p>{{ item.content }}</p>
                </div>
                <el-button size="small" link @click="markRead(item, 'system')">标记已读</el-button>
              </div>
              <div class="time">{{ item.time }}</div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`评价提醒 (${unreadReviewCount})`" name="review">
          <div class="list-wrap">
            <el-empty v-if="reviewMessages.length === 0" description="暂无评价提醒" />
            <el-card v-for="item in reviewMessages" :key="item.id" class="msg-card" shadow="never" :class="{ unread: !item.read }">
              <div class="review-head">
                <div>
                  <strong>{{ item.customerName }}</strong>
                  <p>{{ item.comment }}</p>
                  <el-rate :model-value="item.rating" disabled size="small" />
                </div>
                <el-button size="small" link @click="markRead(item, 'review')">标记已读</el-button>
              </div>
              <el-input v-model="item.reply" placeholder="回复客户评价" maxlength="120" show-word-limit />
              <div class="review-action">
                <el-button type="primary" size="small" @click="submitReply(item)">发送回复</el-button>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getChefOrderPool } from '@/api/cooker'
import { customerRating, formatDateTime, normalizeOrders } from '@/utils/cookerOrder'
import { parseChefId, setUnreadMessageCount } from '@/utils/cookerSession'

const router = useRouter()

const activeTab = ref('order')
const chefId = ref(null)

const orderMessages = ref([])
const systemMessages = ref([])
const reviewMessages = ref([])
const chatThreads = ref([])

const activeThreadId = ref('')
const chatInput = ref('')

const unreadOrderCount = computed(() => orderMessages.value.filter((item) => !item.read).length)
const unreadSystemCount = computed(() => systemMessages.value.filter((item) => !item.read).length)
const unreadReviewCount = computed(() => reviewMessages.value.filter((item) => !item.read).length)
const unreadChatCount = computed(() => chatThreads.value.reduce((sum, item) => sum + item.unread, 0))

const activeThread = computed(() => chatThreads.value.find((item) => item.id === activeThreadId.value))

function syncUnreadCount() {
  const total = unreadOrderCount.value + unreadSystemCount.value + unreadReviewCount.value + unreadChatCount.value
  setUnreadMessageCount(total)
}

function openOrder(orderId) {
  if (!orderId) return
  router.push(`/cooker/order/${orderId}`)
}

function markRead(item, type) {
  if (!item || item.read) return
  item.read = true
  if (type === 'chat') {
    const thread = chatThreads.value.find((entry) => entry.id === item.threadId)
    if (thread && thread.unread > 0) {
      thread.unread -= 1
    }
  }
  syncUnreadCount()
}

function markThreadRead(threadId) {
  const thread = chatThreads.value.find((item) => item.id === threadId)
  if (!thread) return
  thread.unread = 0
  syncUnreadCount()
}

function markAllAsRead() {
  orderMessages.value.forEach((item) => {
    item.read = true
  })
  systemMessages.value.forEach((item) => {
    item.read = true
  })
  reviewMessages.value.forEach((item) => {
    item.read = true
  })
  chatThreads.value.forEach((item) => {
    item.unread = 0
  })
  syncUnreadCount()
  ElMessage.success('全部消息已设为已读')
}

function sendText() {
  if (!activeThread.value) return
  const text = chatInput.value.trim()
  if (!text) return

  activeThread.value.messages.push({
    id: `self-${Date.now()}`,
    from: 'chef',
    type: 'text',
    content: text
  })
  activeThread.value.lastMessage = text
  chatInput.value = ''
}

function sendVoiceHint() {
  if (!activeThread.value) return
  activeThread.value.messages.push({
    id: `voice-${Date.now()}`,
    from: 'chef',
    type: 'text',
    content: '【语音】已收到，我马上安排。'
  })
  activeThread.value.lastMessage = '【语音】已收到，我马上安排。'
}

function handleImageChange(file) {
  if (!activeThread.value || !file?.raw) return
  const url = URL.createObjectURL(file.raw)
  activeThread.value.messages.push({
    id: `img-${Date.now()}`,
    from: 'chef',
    type: 'image',
    content: url
  })
  activeThread.value.lastMessage = '[图片]'
}

function submitReply(item) {
  if (!item.reply?.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  item.read = true
  ElMessage.success('评价回复成功')
  syncUnreadCount()
}

function buildChatThreads(orders) {
  return orders.slice(0, 5).map((order, index) => {
    const customerName = order.customerName || `客户${index + 1}`
    return {
      id: `thread-${order.id}`,
      orderId: order.id,
      customerName,
      unread: index === 0 ? 2 : 1,
      lastMessage: '师傅您好，能否提前半小时到达？',
      messages: [
        {
          id: `cus-${order.id}-1`,
          from: 'customer',
          type: 'text',
          content: '您好，想确认一下今天的菜品安排。'
        },
        {
          id: `chef-${order.id}-1`,
          from: 'chef',
          type: 'text',
          content: '没问题，我已备好食材，稍后到达。'
        }
      ]
    }
  })
}

async function loadMessages() {
  try {
    const pool = await getChefOrderPool(chefId.value)
    const orders = normalizeOrders([...pool.newOrders, ...pool.servingOrders, ...pool.historyOrders])

    orderMessages.value = orders.slice(0, 8).map((order, index) => ({
      id: `order-${order.id}`,
      orderId: order.id,
      title: index % 2 === 0 ? '新派单通知' : '订单状态更新',
      content: `${order.orderType} | ${order.customerName || '匿名客户'} | ${order.address || '地址待确认'}`,
      time: formatDateTime(order.createTime || order.reserveDate),
      read: index > 1
    }))

    systemMessages.value = [
      {
        id: 'sys-1',
        title: '活动公告',
        content: '周末私厨节活动已开启，接单达标可获得额外激励。',
        time: formatDateTime(new Date()),
        read: false
      },
      {
        id: 'sys-2',
        title: '提现提醒',
        content: '本周结算将在周日 21:00 截止，请及时核对收益明细。',
        time: formatDateTime(new Date(Date.now() - 3600 * 1000 * 18)),
        read: true
      },
      {
        id: 'sys-3',
        title: '资质审核结果',
        content: '您的健康证复审已通过，接单权限维持正常。',
        time: formatDateTime(new Date(Date.now() - 3600 * 1000 * 32)),
        read: false
      }
    ]

    reviewMessages.value = orders.slice(0, 5).map((order, index) => ({
      id: `review-${order.id}`,
      orderId: order.id,
      customerName: order.customerName || `客户${index + 1}`,
      rating: Math.round(customerRating(order)),
      comment: index % 2 === 0 ? '服务很专业，沟通顺畅，菜品口味很赞。' : '准时上门，细节处理到位，会继续预约。',
      read: index > 1,
      reply: ''
    }))

    chatThreads.value = buildChatThreads(orders)
    activeThreadId.value = chatThreads.value[0]?.id || ''

    syncUnreadCount()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载消息中心失败')
  }
}

watch(
  () => activeThreadId.value,
  (threadId) => {
    if (!threadId) return
    markThreadRead(threadId)
  }
)

onMounted(async () => {
  chefId.value = parseChefId()
  if (!chefId.value) {
    ElMessage.warning('请先登录厨师账号')
    router.push('/cooker/login')
    return
  }

  await loadMessages()
})
</script>

<style scoped>
.page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 20px 20px;
  display: grid;
  gap: 14px;
}

.head-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

.head-row h3 {
  font-size: 20px;
  color: #27384d;
}

.head-row p {
  margin-top: 4px;
  color: #677489;
}

.list-wrap {
  display: grid;
  gap: 10px;
}

.msg-card {
  border: 1px solid #e8edf5;
}

.msg-card.unread {
  border-left: 4px solid #f56c6c;
}

.msg-head,
.review-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.msg-head p,
.review-head p {
  margin-top: 4px;
  color: #5f6b7f;
  line-height: 1.5;
}

.msg-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time {
  margin-top: 8px;
  color: #8a95a8;
  font-size: 12px;
}

.chat-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 520px;
  border: 1px solid #e8edf5;
  border-radius: 12px;
  overflow: hidden;
}

.thread-list {
  border-right: 1px solid #e8edf5;
  background: #f7f9fc;
  padding: 8px;
  display: grid;
  gap: 8px;
  align-content: flex-start;
}

.thread-item {
  border-radius: 10px;
  padding: 10px;
  background: #fff;
  cursor: pointer;
  border: 1px solid #e8edf5;
}

.thread-item.active {
  border-color: #3a7afe;
  box-shadow: 0 0 0 2px rgba(58, 122, 254, 0.12);
}

.thread-item p {
  margin-top: 6px;
  color: #677489;
  font-size: 12px;
}

.chat-panel {
  display: grid;
  grid-template-rows: auto 1fr auto;
}

.chat-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-bottom: 1px solid #e8edf5;
}

.chat-body {
  padding: 12px;
  background: #f5f7fb;
  overflow: auto;
  display: grid;
  gap: 10px;
  align-content: flex-start;
}

.bubble {
  max-width: 72%;
  padding: 10px;
  border-radius: 10px;
  line-height: 1.5;
}

.bubble.other {
  background: #fff;
  border: 1px solid #e4eaf4;
}

.bubble.self {
  justify-self: end;
  background: #3a7afe;
  color: #fff;
}

.bubble img {
  max-width: 200px;
  border-radius: 8px;
}

.chat-input {
  border-top: 1px solid #e8edf5;
  padding: 10px;
}

.chat-tools {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.review-action {
  margin-top: 8px;
  text-align: right;
}

@media (max-width: 992px) {
  .page {
    padding: 0 12px 14px;
  }

  .chat-layout {
    grid-template-columns: 1fr;
  }

  .thread-list {
    border-right: none;
    border-bottom: 1px solid #e8edf5;
  }
}
</style>
