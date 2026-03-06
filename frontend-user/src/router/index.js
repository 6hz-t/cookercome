import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
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
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue')
    },
    {
      path: '/map-selector',
      name: 'map-selector',
      component: () => import('@/views/MapSelector.vue')
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 需要登录的页面
  const authPages = ['order', 'profile']
  
  if (authPages.includes(to.name) && !token) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
