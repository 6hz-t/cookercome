// src/main.js
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
// 引入路由
import router from './router/index.js'

const app = createApp(App)
app.use(ElementPlus)
app.use(router) // 挂载路由
app.mount('#app')