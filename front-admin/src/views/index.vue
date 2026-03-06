<template>
  <div class="index">
    <h1>首页</h1>

<h2>
    <router-link to="/cooker/login">厨师登录</router-link>
</h2>
    
  </div>
</template>

<script>
export default {
  name: "index"
};
</script>
<style lang="scss" scoped>
.index {
  h1 {
    color: var(--color-heading);
    text-align: center;
    }
}

</style>
