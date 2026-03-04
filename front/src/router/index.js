import {
    createRouter,
    createWebHistory
} from 'vue-router'
import CookerHomeView from '../views/cookerviews/CookerHome.vue'
import CookerLoginView from '../views/cookerviews/CookerLogin.vue'
import IndexView from '../views/index.vue'


const routes = [{
        path: '/',
        name: 'home',
        component: IndexView
    },
    {
        path: '/cooker/home',
        name: 'cookerhome',
        component: CookerHomeView
    },
    {
        path: '/cooker/login',
        name: 'cookerlogin',
        component: CookerLoginView

    }


]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router