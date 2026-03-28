import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { getAccessToken, getRefreshToken } from '@/utils/token'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home-page',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue'),
    meta: { transition: 'slide-left' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Register.vue'),
    meta: { transition: 'slide-right' }
  },
  {
    path: '/chefs',
    name: 'chef-list',
    component: () => import('@/views/ChefList.vue')
  },
  {
    path: '/chef/:id',
    name: 'chef-detail',
    component: () => import('@/views/ChefDetail.vue')
  },
  {
    path: '/order',
    name: 'order',
    component: () => import('@/views/Order.vue')
  },
  {
    path: '/service',
    name: 'service-center',
    component: () => import('@/views/ServiceCenter.vue'),
    redirect: '/service/personal',
    children: [
      {
        path: 'personal',
        name: 'service-personal',
        component: () => import('@/views/service/PersonalCenter.vue')
      },
      {
        path: 'booking',
        name: 'service-booking',
        component: () => import('@/views/service/BookingChef.vue')
      },
      {
        path: 'activity',
        name: 'service-activity',
        component: () => import('@/views/service/Activities.vue')
      },
      {
        path: 'orders',
        name: 'service-orders',
        component: () => import('@/views/service/MyOrders.vue')
      },
      {
        path: 'favorites',
        name: 'service-favorites',
        component: () => import('@/views/service/Favorites.vue')
      },
      {
        path: 'settings',
        name: 'service-settings',
        component: () => import('@/views/service/Settings.vue')
      }
    ]
  },
  {
    path: '/map-selector',
    name: 'map-selector',
    component: () => import('@/views/MapSelector.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 全局前置路由守卫
router.beforeEach((to, _from, next) => {
  const accessToken = getAccessToken()
  const refreshToken = getRefreshToken()
  
  // 白名单页面（不需要登录也能访问）
  const whiteList: string[] = ['login', 'register']
  
  // 如果访问的是白名单页面，直接放行
  if (to.name && whiteList.includes(to.name as string)) {
    // 如果已登录，访问登录页会跳到首页
    if (accessToken && refreshToken) {
      next({ name: 'home' })
    } else {
      next()
    }
  } else {
    // 访问其他页面，检查是否登录
    if (accessToken && refreshToken) {
      // 双 token 都存在，正常访问
      next()
    } else if (refreshToken && !accessToken) {
      // 只有 refresh_token，没有 access_token
      // 这种情况应该通过 API 请求拦截器自动刷新
      // 这里先放行，让第一个 API 请求去触发刷新
      console.warn('检测到 access_token 缺失，将通过 refresh_token 自动刷新')
      next()
    } else {
      // 完全没有 token，强制跳转到登录页
      next({ name: 'login' })
    }
  }
})

export default router
