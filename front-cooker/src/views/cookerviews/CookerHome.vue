<template>
  <div class="cooker-home">
    <el-container>
      <el-header>
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
          <el-menu-item index="1">待接单</el-menu-item>
          <el-menu-item index="2">我的工作台</el-menu-item>
          <el-menu-item index="3">消息中心</el-menu-item>
          <el-menu-item index="4">订单管理</el-menu-item>
        </el-menu>

        <el-button @click="handleProfileClick" type="primary" circle class="my-profile">
          <el-icon>
            <User />
          </el-icon>
        </el-button>






      </el-header>

      

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeIndex: '1',
      activeIndex2: '1'
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      const routeMap = {
        '1': '/cooker/todo',      // 待处理
        '2': '/cooker/desk',      // 我的工作台
        '3': '/cooker/message',   // 消息中心
        '4': '/cooker/orders'     // 订单管理
      };
      if (routeMap[key]) {
        this.$router.push(routeMap[key]);
      }
    },
    handleProfileClick() {
      console.log('跳转到个人资料页');
      this.$router.push('/cooker/profile');
      console.log('当前路由：', this.$route.path);
    }
  }
}
</script>

<style scoped>
.el-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background-color: #fff;
  text-align: center;
}

.my-profile {
  position: absolute;
  right: 20px;
  top: 10px;
}

.el-main {
  padding-top: 60px;
  text-align: center;
  width: 100%;
}
</style>
