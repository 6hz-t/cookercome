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
      activeIndex2: '1',
      routeMap: {
        '1': '/cooker/todo',      // 待处理
        '2': '/cooker/desk',      // 我的工作台
        '3': '/cooker/message',   // 消息中心
        '4': '/cooker/orders'     // 订单管理
      }
    };
  },
  mounted() {
    this.$router.push(this.routeMap['1']);
  },
  methods: {
    handleSelect(key, keyPath) {
      if (this.routeMap[key]) {
        this.$router.push(this.routeMap[key]);
        this.activeIndex = key;
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
  background-color: var(--color-bg-white);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.el-menu {
  background-color: var(--color-bg-white) !important;
  border-color: var(--color-border) !important;
  border-bottom: none !important;
}

.el-menu-item {
  color: var(--color-text-primary) !important;
  font-weight: 500;
}

.el-menu-item:hover {
  background-color: var(--color-bg-soft) !important;
  color: var(--color-accent) !important;
}

.el-menu-item.is-active {
  background-color: var(--color-accent) !important;
  color: var(--color-bg-white) !important;
}

/* 选中状态下边框指示器 */
.el-menu-item.is-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 3px;
  background-color: var(--color-accent);
  border-radius: 2px;
}

.my-profile {
  position: absolute;
  right: 20px;
  top: 10px;
  background-color: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
}

.my-profile:hover {
  background-color: var(--color-accent) !important;
  border-color: var(--color-accent) !important;
}

.el-main {
  padding-top: 60px;
  background-color: var(--color-bg-soft);
  text-align: center;
  width: 100%;
  min-height: 100vh;
}
</style>
