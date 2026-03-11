/**
 * 动态加载百度地图 API
 * @returns {Promise} 返回一个 Promise，在地图 API 加载完成时 resolve
 */
export function loadBMapGL() {
  return new Promise((resolve, reject) => {
    // 如果已经加载，直接返回
    if (window.BMapGL) {
      resolve(window.BMapGL);
      return;
    }

    // 创建 script 标签
    const script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://api.map.baidu.com/api?type=webgl&v=1.0&ak=VhLF9CxroEWj5WcVVAWkZSC9HxOVgoJk';
    
    // 加载成功回调
    script.onload = () => {
      console.log('百度地图 API 加载成功');
      resolve(window.BMapGL);
    };
    
    // 加载失败回调
    script.onerror = () => {
      console.error('百度地图 API 加载失败');
      reject(new Error('百度地图 API 加载失败'));
    };
    
    // 将 script 添加到页面
    document.head.appendChild(script);
  });
}
