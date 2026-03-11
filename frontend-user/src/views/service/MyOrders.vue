<template>
  <div class="my-orders">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Document /></el-icon>
        我的订单
      </h2>
      <p class="panel-desc">查看和管理您的所有订单</p>
    </div>
    
    <el-tabs v-model="orderTab" class="order-tabs">
      <el-tab-pane name="all">
        <template #label>
          <span><el-icon><Menu /></el-icon> 全部</span>
        </template>
      </el-tab-pane>
      <el-tab-pane name="pending">
        <template #label>
          <span><el-icon><Clock /></el-icon> 待使用</span>
        </template>
      </el-tab-pane>
      <el-tab-pane name="completed">
        <template #label>
          <span><el-icon><CircleCheck /></el-icon> 已完成</span>
        </template>
      </el-tab-pane>
      <el-tab-pane name="cancelled">
        <template #label>
          <span><el-icon><Close /></el-icon> 已取消</span>
        </template>
      </el-tab-pane>
    </el-tabs>
    
    <div class="order-list">
      <div v-for="order in allOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.no }}</span>
          <el-tag :type="getOrderStatusType(order.status)" size="small">{{ getOrderStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-content">
          <el-avatar :size="60" :src="order.chefAvatar" />
          <div class="order-info">
            <h4>{{ order.chefName }}</h4>
            <p>{{ order.serviceType }}</p>
            <p class="order-time"><el-icon><Calendar /></el-icon> {{ order.serviceDate }} {{ order.serviceTime }}</p>
          </div>
          <div class="order-amount">¥{{ order.totalAmount }}</div>
        </div>
        <div class="order-actions">
          <el-button size="small" @click="$emit('view-detail', order)">查看详情</el-button>
          <el-button v-if="order.status === 'completed'" size="small" type="primary" @click="$emit('review-order', order)">评价</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Document, Menu, Clock, CircleCheck, Close, Calendar } from '@element-plus/icons-vue'

const emit = defineEmits(['view-detail', 'review-order'])

const orderTab = ref('all')

const allOrders = ref([
  {
    id: 1,
    no: 'DD20260308001',
    chefName: '王师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '川菜定制',
    status: 'pending',
    serviceDate: '2026-03-08',
    serviceTime: '18:00-20:00',
    totalAmount: 598
  },
  {
    id: 2,
    no: 'DD20260305002',
    chefName: '李师傅',
    chefAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    serviceType: '粤菜海鲜',
    status: 'completed',
    serviceDate: '2026-03-05',
    serviceTime: '12:00-14:00',
    totalAmount: 888
  }
])

const getOrderStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getOrderStatusText = (status) => {
  const textMap = {
    'pending': '待使用',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}
</script>

<style scoped>
.my-orders {
  min-height: 600px;
}

.order-tabs {
  margin-bottom: 25px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: var(--card-bg);
  border: 2px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
  transition: var(--transition);
}

.order-card:hover {
  border-color: #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color);
}

.order-no {
  font-size: 14px;
  color: var(--text-secondary);
}

.order-content {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.order-info h4 {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.order-info p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 5px 0;
}

.order-time {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-muted) !important;
}

.order-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-left: auto;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid var(--border-color);
}
</style>
