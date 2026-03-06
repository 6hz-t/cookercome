// src/utils/loadBMap.js
// 百度地图已在 index.html 中全局引入，此函数仅用于检查是否可用
export default function loadBMap() {
  return new Promise((resolve, reject) => {
    // 检查是否已加载
    if (window.BMapGL && window.BMapGL.Map) {
      console.log('百度地图 BMapGL 可用');
      resolve(window.BMapGL);
      return;
    }
    if (window.BMap && window.BMap.Map) {
      console.log('百度地图 BMap 可用');
      resolve(window.BMap);
      return;
    }
    
    // 如果还没加载，等待一小段时间再检查
    const checkInterval = setInterval(() => {
      if (window.BMapGL && window.BMapGL.Map) {
        clearInterval(checkInterval);
        console.log('百度地图 BMapGL 加载完成');
        resolve(window.BMapGL);
      } else if (window.BMap && window.BMap.Map) {
        clearInterval(checkInterval);
        console.log('百度地图 BMap 加载完成');
        resolve(window.BMap);
      }
    }, 100);
    
    // 超时处理
    setTimeout(() => {
      clearInterval(checkInterval);
      reject(new Error('百度地图未加载，请检查 index.html 中的脚本引入'));
    }, 5000);
  });
}