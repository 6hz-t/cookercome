<template>
    <div class="container">
        <h3>待接单</h3>

        <div class="orders-area">
            <OrderCard 
                v-for="order in orders" 
                :key="order.orderid" 
                :order="order"
                @accept="handleAccept"
                @reject="handleReject"
            ></OrderCard>
        </div>
    </div>
</template>
<script>
import OrderCard from '@/components/cooker/OrderCard.vue';

export default {
    name: 'CookerTodo',
    components: {
        OrderCard
    },
    data() {
        return {
            orders: [
                {
                    orderid: 1,
                    username: '张三',
                    userphone: '13800138000',
                    servetime: '2024-03-15 11:00:00',
                    serveaddress: '北京市朝阳区建国路 88 号 SOHO 现代城 1 号楼 2305 室',
                    requirement: '不要放洋葱和香菜，菜品偏清淡，4 人份',
                    totalprice: '156.00',
                    dishes: [
                        { name: '宫保鸡丁', spec: '中辣', price: 48, quantity: 1 },
                        { name: '清炒时蔬', spec: '标准', price: 28, quantity: 2 },
                        { name: '米饭', spec: '大碗', price: 3, quantity: 4 }
                    ],
                    status: 'waiting'
                },
                {
                    orderid: 2,
                    username: '李四',
                    userphone: '13900139000',
                    servetime: '2024-03-15 17:30:00',
                    serveaddress: '上海市浦东新区张江高科技园区博云路 2 号浦软大厦 4 层',
                    requirement: '特辣口味，优先川菜，需要做一份水煮鱼，6 人份',
                    totalprice: '328.00',
                    dishes: [
                        { name: '水煮鱼', spec: '特辣', price: 128, quantity: 1 },
                        { name: '麻婆豆腐', spec: '中辣', price: 28, quantity: 2 },
                        { name: '夫妻肺片', spec: '标准', price: 48, quantity: 1 },
                        { name: '米饭', spec: '大碗', price: 3, quantity: 6 }
                    ],
                    status: 'waiting'
                },
                {
                    orderid: 3,
                    username: '王芳',
                    userphone: '13700137000',
                    servetime: '2024-03-16 12:00:00',
                    serveaddress: '广州市天河区天河路 385 号太古汇一座 18 层',
                    requirement: '粤菜为主，少油少盐，需要提前 1 小时到达准备，5 人份',
                    totalprice: '268.00',
                    dishes: [
                        { name: '白切鸡', spec: '标准', price: 88, quantity: 1 },
                        { name: '清蒸石斑鱼', spec: '标准', price: 158, quantity: 1 },
                        { name: '米饭', spec: '大碗', price: 3, quantity: 5 }
                    ],
                    status: 'waiting'
                }
            ]
        }
    },
    methods: {
        handleAccept(order) {
            console.log('accepted', order.orderid);
        },
        handleReject(order) {
            console.log('rejected', order.orderid);
        }
    }
}
</script>
<style scoped>
.container {
    width: 1000px;
}

h3 {
    margin: 20px 0;
    text-align: center;
}

.orders-area {
    display: flex;
    flex-direction: column;
    gap: 20px;
}
</style>
