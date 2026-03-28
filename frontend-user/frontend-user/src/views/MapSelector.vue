<template>
  <div class="map-selector">
    <h2>地图选点</h2>
    <div v-if="loading" class="loading">地图加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { loadBMapGL } from '@/utils/bmapLoader';

const loading = ref(true);
const error = ref('');
const mapContainer = ref(null);
let map = null;

onMounted(async () => {
  try {
    // 动态加载百度地图 API
    await loadBMapGL();
    
    // 初始化地图
    map = new BMapGL.Map(mapContainer.value);
    const point = new BMapGL.Point(116.404, 39.915);
    map.centerAndZoom(point, 15);
    
    // 启用滚动和缩放
    map.enableScrollWheelZoom();
    
    loading.value = false;
  } catch (err) {
    error.value = '地图加载失败，请检查网络连接';
    loading.value = false;
    console.error(err);
  }
});
</script>

<style scoped>
.map-selector {
  padding: 20px;
}

.map-container {
  width: 100%;
  height: 500px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.loading,
.error {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error {
  color: #f56c6c;
}
</style>
