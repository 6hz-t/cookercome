<template>
    <div class="container">
        <h3>待接单</h3>

        <div class="orders-area">
            <OrderCard v-for="order in orders" :key="order.orderid" :order="order" @accept="handleAccept"></OrderCard>
        </div>
    </div>
</template>
<script>
import OrderCard from '@/components/cooker/OrderCard.vue';
import { getNewOrders, acceptOrder } from '@/api/cooker';

export default {
    name: 'CookerTodo',
    components: {
        OrderCard
    },
    data() {
        return {
            orders: [
                {
                    orderNo: '20240315000000001',
                    orderid: 1,
                    username: '张三',
                    userphone: '13800138000',
                    servetime: '2024-03-15 11:00:00',
                    serveaddress: '北京市朝阳区建国路 88 号 SOHO 现代城 1 号楼 2305 室',
                    requirement: '不要放洋葱和香菜，菜品偏清淡，4 人份',
                    totalprice: '156.00',
                    status: 'waiting'
                },
                {
                    orderNo: '20240315000000002',
                    orderid: 2,
                    username: '李四',
                    userphone: '13900139000',
                    servetime: '2024-03-15 17:30:00',
                    serveaddress: '上海市浦东新区张江高科技园区博云路 2 号浦软大厦 4 层',
                    requirement: '特辣口味，优先川菜，需要做一份水煮鱼，6 人份',
                    totalprice: '328.00',
                    status: 'waiting'
                },
                {
                    orderNo: '20240316000000003',
                    orderid: 3,
                    username: '王芳',
                    userphone: '13700137000',
                    servetime: '2024-03-16 12:00:00',
                    serveaddress: '广州市天河区天河路 385 号太古汇一座 18 层',
                    requirement: '粤菜为主，少油少盐，需要提前 1 小时到达准备，5 人份',
                    totalprice: '268.00',
                    status: 'waiting'
                }
            ]
        }
    },
    methods: {
        async handleAccept(data) {
            const { order, remark } = data;

            try {
                // 调用接单 API
                await acceptOrder({
                    orderId: order.orderid,
                    chefId: localStorage.getItem('userId')
                });
                console.log('接单 orderid:', order.orderid, '厨师 ID:', localStorage.getItem('userId'));

                // 将订单添加到待服务列表
                const servingOrder = {
                    orderNo: order.orderNo,
                    orderid: order.orderid,
                    status: 'accepted',
                    username: order.username,
                    userphone: order.userphone,
                    servetime: order.servetime,
                    serveaddress: order.serveaddress,
                    requirement: order.requirement,
                    totalprice: order.totalprice,
                    createTime: new Date().toISOString()
                };
                
                // 保存到 localStorage
                const servingOrders = localStorage.getItem('servingOrders')
                let orders = servingOrders ? JSON.parse(servingOrders) : []
                orders.push(servingOrder)
                localStorage.setItem('servingOrders', JSON.stringify(orders))

                // 从待接单列表中移除
                this.orders = this.orders.filter(item => item.orderid !== order.orderid);

                // 显示成功提示
                this.$message.success('接单成功');
            } catch (error) {
                console.error('接单失败:', error);
                this.$message.error('接单失败：' + (error.message || '未知错误'));
            }
        }
    },
    async mounted() {

        const res = await getNewOrders();

        this.orders = res.records.map(order => {
            return {
                orderNo: order.orderNo,
                orderid: order.id,
                status: 'waiting',
                username: order.customerId,
                userphone: order.customerId,
                servetime: order.reserveDate + ' ' + order.reserveTime.split('-')[0],
                serveaddress: order.addressId,
                requirement: order.dishRequirements,
                totalprice: order.totalAmount
            }
        });
        console.log(res.records);
        console.log(this.orders);
    }
}
</script>
<style scoped>
.container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
}

h3 {
    margin: 20px 0;
    text-align: center;
    color: var(--color-text-primary);
    font-size: 20px;
    font-weight: 600;
}

.orders-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
}
</style>
