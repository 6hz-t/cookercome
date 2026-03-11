<template>
  <div class="activities">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Ticket /></el-icon>
        优惠活动
      </h2>
      <p class="panel-desc">精彩活动不容错过</p>
    </div>
    
    <div class="activity-list">
      <div v-for="activity in activities" :key="activity.id" class="activity-card">
        <div class="activity-banner" :style="{ backgroundImage: `url(${activity.image})` }">
          <div class="activity-tag" :class="activity.tagType">{{ activity.tag }}</div>
        </div>
        <div class="activity-content">
          <h3>{{ activity.title }}</h3>
          <p class="activity-desc">{{ activity.description }}</p>
          <div class="activity-meta">
            <span><el-icon><Clock /></el-icon> {{ activity.endDate }}</span>
            <span class="activity-progress">
              <el-progress :percentage="activity.progress" :stroke-width="6" />
            </span>
          </div>
          <el-button :type="activity.btnType" block @click="$emit('join-activity', activity)">{{ activity.btnText }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Ticket, Clock } from '@element-plus/icons-vue'

const emit = defineEmits(['join-activity'])

const activities = ref([
  {
    id: 1,
    title: '新用户专享优惠',
    description: '首次下单立减 100 元，享受专业厨师上门服务',
    image: 'https://via.placeholder.com/400x200/667eea/ffffff?text=New+User',
    tag: '热门',
    tagType: 'hot',
    endDate: '2026-12-31',
    progress: 75,
    btnType: 'primary',
    btnText: '立即参与'
  },
  {
    id: 2,
    title: '春季养生套餐',
    description: '专业营养师搭配，为您定制健康养生菜单',
    image: 'https://via.placeholder.com/400x200/43e97b/ffffff?text=Spring+Health',
    tag: '新品',
    tagType: 'new',
    endDate: '2026-04-30',
    progress: 45,
    btnType: 'success',
    btnText: '查看详情'
  },
  {
    id: 3,
    title: '家庭聚餐特惠',
    description: '满 500 减 80，适合家庭聚会、生日宴等场合',
    image: 'https://via.placeholder.com/400x200/fa709a/ffffff?text=Family+Meal',
    tag: '限时',
    tagType: 'limited',
    endDate: '2026-03-31',
    progress: 90,
    btnType: 'warning',
    btnText: '马上抢购'
  }
])
</script>

<style scoped>
.activities {
  min-height: 600px;
}

.activity-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
}

.activity-card {
  background: var(--card-bg);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: var(--transition);
}

.activity-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
}

.activity-banner {
  height: 200px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.activity-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.activity-tag.hot { background: linear-gradient(135deg, #ff416c, #ff4b2b); }
.activity-tag.new { background: linear-gradient(135deg, #56ab2f, #a8e063); }
.activity-tag.limited { background: linear-gradient(135deg, #f093fb, #f5576c); }

.activity-content {
  padding: 20px;
}

.activity-content h3 {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 10px 0;
}

.activity-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.activity-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 13px;
  color: var(--text-muted);
}

.activity-progress {
  width: 120px;
}
</style>
