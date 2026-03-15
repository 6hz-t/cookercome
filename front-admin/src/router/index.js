// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
// 导入登录页面和主页面
import Login from '@/views/Login.vue'
import CookerHome from '@/views/cookerviews/CookerHome.vue' 
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
    redirect: (to) => {
      // 检查是否有token，决定重定向到哪里
      const token = localStorage.getItem('admin-token');
      if (token) {
        // 已登录，跳转到account页面
        return '/account';
      } else {
        // 未登录，重定向到登录页
        return '/login';
      }
    }
  },
  {
    path: '/login', // 登录页路由
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard', // 主页路由
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/account', // 账号管控页面（在Dashboard中实现）
    name: 'Account',
    component: Dashboard,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/chef-audit',
    name: 'ChefAudit',
    component: ChefAudit,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/data-management',
    name: 'DataManagement',
    component: BasicDataManagement,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/order-control',
    name: 'OrderControl',
    component: OrderManagement,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: DataStatistics,
    meta: { requiresAuth: true } // 需要登录才能访问
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否有token
    const token = localStorage.getItem('admin-token');
    if (token) {
      // 有token，允许访问
      next();
    } else {
      // 没有token，重定向到登录页
      next('/login');
    }
  } else {
    // 不需要认证的路由，直接访问
    next();
  }
});

export default router