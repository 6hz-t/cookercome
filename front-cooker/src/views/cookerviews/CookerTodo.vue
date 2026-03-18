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
            orders: []
        }
    },
    methods: {
        async handleAccept(data) {
            const { order } = data;

            
            
            try {
                // 调用接单API，传递订单ID和厨师ID
                await acceptOrder({
                    orderId: order.orderid,
                    chefId: localStorage.getItem('userId') // 从localStorage获取厨师ID
                });
                console.log('接单  orderid:', order.orderid, '厨师ID:', localStorage.getItem('userId'));
                
                // 接单成功后，从列表中移除该订单
                this.orders = this.orders.filter(item => item.orderid !== order.orderid);
                
                // 显示成功提示
                this.$message.success('接单成功');
            } catch (error) {
                console.error('接单失败:', error);
                this.$message.error('接单失败: ' + (error.message || '未知错误'));
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
        /*
        * {
      "id": 2,
      "orderNo": "20260316100000002",
      "customerId": 1002,
      "chefId": 2002,
      "addressId": 3002,
      "reserveDate": "2026-03-18T16:00:00.000+00:00",
      "reserveTime": "17:00-19:00",
      "dishRequirements": "清淡口味，适合老人",
      "totalAmount": 398,
      "status": 1,
      "paymentTime": "2026-03-16T02:05:30.000+00:00",
      "serviceStartTime": null,
      "serviceEndTime": null,
      "remark": "",
      "createTime": "2026-03-16T07:31:38.000+00:00",
      "updateTime": "2026-03-16T07:31:38.000+00:00"
    }
        */





        //todo: 将接口返回的数据转换成订单列表的格式
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
