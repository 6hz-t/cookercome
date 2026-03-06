// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// 导入登录页面和仪表盘页面
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'

// 路由规则
const routes = [
  {
    path: '/', // 默认访问根路径跳转到登录页
    redirect: '/login'
  },
  {
    path: '/login', // 登录页路由
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard', // 仪表盘页路由
    name: 'Dashboard',
    component: Dashboard
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router