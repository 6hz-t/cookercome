<template>
  <div class="favorites">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Collection /></el-icon>
        我的收藏
      </h2>
      <p class="panel-desc">收藏的厨师和菜品</p>
    </div>
    
    <div class="favorites-grid">
      <div v-for="item in favorites" :key="item.id" class="favorite-card">
        <el-image :src="item.image" fit="cover" class="favorite-img" />
        <div class="favorite-info">
          <h4>{{ item.name }}</h4>
          <p class="favorite-desc">{{ item.description }}</p>
          <div class="favorite-footer">
            <span class="price">¥{{ item.price }}</span>
            <el-button size="small" type="primary" circle @click="$emit('remove-favorite', item.id)">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Collection, Close } from '@element-plus/icons-vue'

const emit = defineEmits(['remove-favorite'])

const favorites = ref([
  {
    id: 1,
    name: '水煮鱼',
    description: '麻辣鲜香，口感滑嫩',
    image: 'https://via.placeholder.com/300x200/ff6b6b/ffffff?text=Food',
    price: 128
  },
  {
    id: 2,
    name: '清蒸石斑鱼',
    description: '清淡鲜美，营养丰富',
    image: 'https://via.placeholder.com/300x200/4ecdc4/ffffff?text=Seafood',
    price: 268
  },
  {
    id: 3,
    name: '糖醋排骨',
    description: '酸甜可口，外酥里嫩',
    image: 'https://via.placeholder.com/300x200/ffeaa7/ffffff?text=Pork',
    price: 88
  }
])
</script>

<style scoped>
.favorites {
  min-height: 600px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
}

.favorite-card {
  background: var(--card-bg);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: var(--transition);
}

.favorite-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-hover);
}

.favorite-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.favorite-info {
  padding: 15px;
}

.favorite-info h4 {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.favorite-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.favorite-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
