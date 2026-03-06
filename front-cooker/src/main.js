import './assets/main.css'
// 1. 引入 Element Plus 核心库和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 2. 挂载 Element Plus 到 Vue 实例
const app = createApp(App)
// 新增：全局注册所有图标（一次注册，所有组件可直接用）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
app.use(ElementPlus) // 全局注册 Element Plus 所有组件
app.mount('#app')