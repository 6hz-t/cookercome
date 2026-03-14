import {
    createRouter,
    createWebHistory
} from 'vue-router'
import CookerHomeView from '../views/cookerviews/CookerHome.vue'
import CookerLoginView from '../views/cookerviews/CookerLogin.vue'
import IndexView from '../views/index.vue'
import CookerTodoView from '../views/cookerviews/CookerTodo.vue'
import CookerMessageView from '../views/cookerviews/CookerMessage.vue'
import CookerOrderView from '../views/cookerviews/CookerOrders.vue'
import CookerDeskView from '../views/cookerviews/CookerDesk.vue'
import CookerProfileView from '../views/cookerviews/CookerProfile.vue'



const routes = [{
        path: '/',
        name: 'home',
        component: IndexView
    },
    {
        path: '/cooker',
        name: 'cookerhome',
        component: CookerHomeView,
        children: [{
            path: 'message',
            name: 'cookermessage',
            component: CookerMessageView
        },
        {
            path: 'orders',
            name: 'cookerorders',
            component: CookerOrderView
        },
        {
            path: 'desk',
            name: 'cookerdesk',
            component: CookerDeskView
        },
        {
            path: 'todo',
            name: 'cookertodo',
            component: CookerTodoView
        },
        {
            path: 'profile',
            name: 'cookerprofile',
            component: CookerProfileView
        }
        ]

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