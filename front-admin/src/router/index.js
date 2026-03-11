import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ChefAudit from '../views/ChefAudit.vue'
import BasicDataManagement from '../views/BasicDataManagement.vue'
import OrderManagement from '../views/OrderManagement.vue'
import DataStatistics from '../views/DataStatistics.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/login' // 将首页重定向到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
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

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router