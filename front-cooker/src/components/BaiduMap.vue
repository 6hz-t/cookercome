<template>
  <!-- 地图容器，必须设置宽高 -->
  <div ref="mapContainer" class="baidu-map"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import loadBMap from '@/utils/loadBMap';

// 地图容器和实例
const mapContainer = ref(null);
let map = null;

// 初始化地图
const initMap = async () => {
  try {
    // 加载百度地图 API
    const BMap = await loadBMap();
    
    console.log('BMap 对象:', BMap);
    console.log('window.BMapGL:', window.BMapGL);
    console.log('window.BMap:', window.BMap);

    // 创建地图实例（中心点：北京，缩放级别：12）
    // 优先使用 BMapGL，如果不存在则使用 BMap
    const MapClass = window.BMapGL?.Map || window.BMap?.Map;
    const PointClass = window.BMapGL?.Point || window.BMap?.Point;
    
    if (!MapClass || !PointClass) {
      throw new Error('百度地图 API 未正确加载');
    }

    map = new MapClass(mapContainer.value);
    const point = new PointClass(116.404, 39.915);

    // 初始化地图，设置中心点坐标和地图级别
    map.centerAndZoom(point, 12);

    // 开启鼠标滚轮缩放
    map.enableScrollWheelZoom(true);

    // 可选：添加控件（缩放控件、比例尺）
    if (window.BMapGL) {
      map.addControl(new window.BMapGL.NavigationControl());
      map.addControl(new window.BMapGL.ScaleControl());
      
      // 可选：添加标记点
      const marker = new window.BMapGL.Marker(point);
      map.addOverlay(marker);
      
      // 可选：标记点添加点击事件
      marker.addEventListener('click', () => {
        alert('你点击了标记点！');
      });
    } else {
      map.addControl(new window.BMap.NavigationControl());
      map.addControl(new window.BMap.ScaleControl());
      
      const marker = new window.BMap.Marker(point);
      map.addOverlay(marker);
      
      marker.addEventListener('click', () => {
        alert('你点击了标记点！');
      });
    }

  } catch (error) {
    console.error('地图初始化失败：', error);
  }
};

// 生命周期：挂载后初始化
onMounted(() => {
  initMap();
});

// 生命周期：卸载前销毁地图
onUnmounted(() => {
  if (map) {
    map.destroy();
    map = null;
  }
});
</script>

<style scoped>
.baidu-map {

  width: 400px;
  height: 400px; /* 必须设置高度，否则地图无法显示 */
}
</style>