<template>
  <div class="schedule-section">
    <div class="section-header-tech">
      <h3 class="section-title">
        <el-icon class="title-icon"><Calendar /></el-icon>
        <span class="title-text">我的预约时间表</span>
      </h3>
    </div>

    <div class="schedule-container" v-loading="loading">
      <!-- 日期导航 -->
      <div class="date-nav">
        <el-button circle size="small" @click="prevDay" :disabled="isToday(currentDate)">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span class="current-date">{{ formatDate(currentDate) }}</span>
        <el-button circle size="small" @click="nextDay" :disabled="isLastDay">
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <!-- 时间段展示 -->
      <div class="time-slots">
        <div 
          v-for="(slot, index) in currentTimeSlots" 
          :key="index"
          class="time-slot"
          :class="getStatusClass(slot.status)"
        >
          <div class="slot-time">{{ slot.timeSlot }}</div>
          <div class="slot-status">
            <span class="status-indicator"></span>
            <span class="status-text">{{ getStatusText(slot.status) }}</span>
          </div>
          <div class="slot-chef" v-if="slot.chefName">
            <el-icon><User /></el-icon>
            {{ slot.chefName }}
          </div>
        </div>
      </div>

      <!-- 日期网格 -->
      <div class="calendar-grid">
        <div 
          v-for="(day, dayIndex) in scheduleData" 
          :key="dayIndex"
          class="calendar-day"
          :class="{ active: isSameDay(day.date, currentDate) }"
          @click="selectDate(day.date)"
        >
          <div class="day-header">
            <span class="day-name">{{ getDayName(day.date) }}</span>
            <span class="day-date">{{ getDayDate(day.date) }}</span>
          </div>
          
          <div class="day-slots">
            <div 
              v-for="(slot, slotIndex) in day.timeSlots" 
              :key="slotIndex"
              class="mini-slot"
              :class="getStatusClass(slot.status)"
              :title="`${slot.timeSlot}: ${getStatusText(slot.status)}${slot.chefName ? ' - ' + slot.chefName : ''}`"
            >
            </div>
          </div>
        </div>
      </div>

      <!-- 状态图例 -->
      <div class="status-legend">
        <div class="legend-item">
          <span class="legend-color status-0"></span>
          <span class="legend-text">空闲</span>
        </div>
        <div class="legend-item">
          <span class="legend-color status-1"></span>
          <span class="legend-text">待接单</span>
        </div>
        <div class="legend-item">
          <span class="legend-color status-2"></span>
          <span class="legend-text">待支付</span>
        </div>
        <div class="legend-item">
          <span class="legend-color status-3"></span>
          <span class="legend-text">已支付</span>
        </div>
        <div class="legend-item">
          <span class="legend-color status-4"></span>
          <span class="legend-text">服务完成</span>
        </div>
        <div class="legend-item">
          <span class="legend-color status-5"></span>
          <span class="legend-text">退款中</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Calendar, User, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getUserSchedule } from '@/api/customer'
import { ElMessage } from 'element-plus'

interface TimeSlot {
  timeSlot: string
  status: number
  orderId?: number
  chefName?: string
}

interface DaySchedule {
  date: string
  timeSlots: TimeSlot[]
}

const scheduleData = ref<DaySchedule[]>([])
const loading = ref(false)
const currentDate = ref<string>('')

// 计算属性
const currentTimeSlots = computed(() => {
  const current = scheduleData.value.find(day => isSameDay(day.date, currentDate.value))
  return current ? current.timeSlots : []
})

const isLastDay = computed(() => {
  if (!currentDate.value) return true
  const lastDay = new Date()
  lastDay.setDate(lastDay.getDate() + 14)
  return currentDate.value >= formatDate(lastDay)
})

// 初始化
onMounted(async () => {
  await loadSchedule()
})

// 加载时间表数据
const loadSchedule = async () => {
  loading.value = true
  try {
    const res = await getUserSchedule()
    // request 拦截器已经处理了 code !== 200 的情况，能到这里说明请求成功
    // res 就是后端返回的 data 部分
    if (res && Array.isArray(res)) {
      scheduleData.value = res.map((day: any) => ({
        date: day.date,
        timeSlots: day.timeSlots.map((slot: any) => ({
          timeSlot: slot.timeSlot,
          status: slot.status,
          orderId: slot.orderId,
          chefName: slot.chefName
        }))
      }))
      
      // 默认选择今天
      if (scheduleData.value.length > 0) {
        currentDate.value = scheduleData.value[0].date
      }
    }
  } catch (error) {
    console.error('加载时间表失败:', error)
    ElMessage.error('加载时间表失败')
  } finally {
    loading.value = false
  }
}

// 日期操作
const prevDay = () => {
  if (!currentDate.value) return
  const date = new Date(currentDate.value)
  date.setDate(date.getDate() - 1)
  currentDate.value = formatDate(date)
}

const nextDay = () => {
  if (!currentDate.value) return
  const date = new Date(currentDate.value)
  date.setDate(date.getDate() + 1)
  currentDate.value = formatDate(date)
}

const selectDate = (date: string) => {
  currentDate.value = date
}

// 工具函数
const isToday = (dateStr: string) => {
  const today = new Date().toISOString().split('T')[0]
  return dateStr === today
}

const isSameDay = (date1: string, date2: string) => {
  return date1 === date2
}

const formatDate = (date: Date | string) => {
  if (typeof date === 'string') return date
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getDayName = (dateStr: string) => {
  const date = new Date(dateStr)
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return days[date.getDay()]
}

const getDayDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}/${day}`
}

// 状态映射
const getStatusClass = (status: number) => `status-${status}`

const getStatusText = (status: number) => {
  const texts: { [key: number]: string } = {
    0: '空闲',
    1: '待接单',
    2: '待支付',
    3: '已支付',
    4: '服务完成',
    5: '退款中'
  }
  return texts[status] || '未知'
}
</script>

<style scoped>
.schedule-section {
  margin-top: 2rem;
}

.section-header-tech {
  margin-bottom: 1.5rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-primary);
  font-size: 1.25rem;
  font-weight: 600;
}

.title-icon {
  color: var(--primary-color);
  font-size: 1.5rem;
}

.schedule-container {
  background: var(--card-bg);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid rgba(100, 150, 255, 0.15);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

/* 日期导航 */
.date-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
  padding: 0.75rem 1rem;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
}

.current-date {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
}

/* 时间段展示 */
.time-slots {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.time-slot {
  padding: 1rem;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.time-slot:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.slot-time {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.slot-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--status-color, #666);
  box-shadow: 0 0 8px var(--status-color, #666);
}

.status-text {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-primary);
}

.slot-chef {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

/* 日历网格 */
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.calendar-day {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 0.75rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.calendar-day:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
}

.calendar-day.active {
  background: rgba(100, 150, 255, 0.15);
  border-color: var(--primary-color);
  box-shadow: 0 0 12px rgba(100, 150, 255, 0.3);
}

.day-header {
  text-align: center;
  margin-bottom: 0.5rem;
}

.day-name {
  display: block;
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
}

.day-date {
  display: block;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.day-slots {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.mini-slot {
  height: 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.mini-slot:hover {
  transform: scale(1.05);
}

/* 状态样式 */
.status-0 { --status-color: #6b7280; background: rgba(107, 114, 128, 0.2); }
.status-1 { --status-color: #f59e0b; background: rgba(245, 158, 11, 0.2); }
.status-2 { --status-color: #f97316; background: rgba(249, 115, 22, 0.2); }
.status-3 { --status-color: #10b981; background: rgba(16, 185, 129, 0.2); }
.status-4 { --status-color: #3b82f6; background: rgba(59, 130, 246, 0.2); }
.status-5 { --status-color: #ef4444; background: rgba(239, 68, 68, 0.2); }

/* 状态图例 */
.status-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-color.status-0 { background: #6b7280; }
.legend-color.status-1 { background: #f59e0b; }
.legend-color.status-2 { background: #f97316; }
.legend-color.status-3 { background: #10b981; }
.legend-color.status-4 { background: #3b82f6; }
.legend-color.status-5 { background: #ef4444; }

.legend-text {
  font-size: 0.8rem;
  color: var(--text-secondary);
}
</style>
