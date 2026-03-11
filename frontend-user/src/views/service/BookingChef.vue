<template>
  <div class="booking-chef">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Calendar /></el-icon>
        预约厨师
      </h2>
      <p class="panel-desc">专业厨师上门服务，为您定制专属美食</p>
    </div>
    
    <div class="chef-cards">
      <div v-for="chef in chefList" :key="chef.id" class="chef-card-item">
        <div class="chef-card-img">
          <img :src="chef.avatar" :alt="chef.name" />
          <div class="chef-level-badge" :class="chef.levelClass">{{ chef.level }}</div>
        </div>
        <div class="chef-card-info">
          <h3>{{ chef.name }}</h3>
          <p class="chef-title">{{ chef.title }}</p>
          <div class="chef-skills">
            <el-tag v-for="skill in chef.skills" :key="skill" size="small" effect="plain">{{ skill }}</el-tag>
          </div>
          <div class="chef-stats-row">
            <span><el-icon><Star /></el-icon> {{ chef.rating }}分</span>
            <span><el-icon><ShoppingCart /></el-icon> {{ chef.orders }}单</span>
            <span>¥{{ chef.price }}起</span>
          </div>
          <el-button type="primary" block @click="$emit('book-chef', chef)">立即预约</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Calendar, Star, ShoppingCart } from '@element-plus/icons-vue'

const emit = defineEmits(['book-chef'])

const chefList = ref([
  {
    id: 1,
    name: '王师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '特级厨师 | 20 年经验',
    level: '金牌',
    levelClass: 'gold',
    skills: ['川菜', '粤菜', '海鲜'],
    rating: 4.9,
    orders: 2580,
    price: 398
  },
  {
    id: 2,
    name: '李师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '高级营养师 | 18 年经验',
    level: '钻石',
    levelClass: 'diamond',
    skills: ['养生菜', '药膳', '轻食'],
    rating: 4.8,
    orders: 1860,
    price: 458
  },
  {
    id: 3,
    name: '张师傅',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    title: '创新菜大师 | 15 年经验',
    level: '银牌',
    levelClass: 'silver',
    skills: ['融合菜', '创意菜', '西餐'],
    rating: 4.7,
    orders: 1520,
    price: 368
  }
])
</script>

<style scoped>
.booking-chef {
  min-height: 600px;
}

.panel-header {
  text-align: center;
  margin-bottom: 40px;
}

.panel-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 10px;
}

.panel-title .el-icon {
  color: #667eea;
  font-size: 32px;
}

.panel-desc {
  font-size: 16px;
  color: var(--text-secondary);
}

.chef-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}

.chef-card-item {
  background: linear-gradient(135deg, #3d3d3d 0%, #2d2d2d 100%);
  border-radius: 16px;
  overflow: hidden;
  transition: var(--transition);
  cursor: pointer;
}

.chef-card-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.2);
}

.chef-card-img {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.chef-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: var(--transition);
}

.chef-card-item:hover .chef-card-img img {
  transform: scale(1.1);
}

.chef-level-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.chef-level-badge.gold { background: linear-gradient(135deg, #ffd700, #ffed4e); }
.chef-level-badge.diamond { background: linear-gradient(135deg, #b9f2ff, #00d4ff); color: #333; }
.chef-level-badge.silver { background: linear-gradient(135deg, #c0c0c0, #e8e8e8); color: #333; }

.chef-card-info {
  padding: 20px;
}

.chef-card-info h3 {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.chef-title {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 15px 0;
}

.chef-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.chef-stats-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-top: 1px solid var(--border-color);
  margin-bottom: 15px;
  font-size: 14px;
  color: var(--text-secondary);
}

.chef-stats-row span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.chef-stats-row .el-icon {
  color: #faad14;
}
</style>
