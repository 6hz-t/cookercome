// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// 导入登录页面和主页面
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ChefAudit from '../views/ChefAudit.vue'
import BasicDataManagement from '../views/BasicDataManagement.vue'
import OrderManagement from '../views/OrderManagement.vue'
import DataStatistics from '../views/DataStatistics.vue'

// 路由规则
const routes = [
  {
    path: '/', // 默认访问根路径
    name: 'Home',
    redirect: '/login' // 直接重定向到登录页
  },
  {
    path: '/login', // 登录页路由
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard', // 主页路由
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/chef-audit',
    name: 'ChefAudit',
    component: ChefAudit
  },
  {
    path: '/data-management',
    name: 'DataManagement',
    component: BasicDataManagement
  },
  {
    path: '/order-control',
    name: 'OrderControl',
    component: OrderManagement
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: DataStatistics
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router