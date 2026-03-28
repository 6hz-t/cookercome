<template>
  <div class="landing">
    <el-card class="landing-card" shadow="hover">
      <h1>厨师上门服务平台</h1>
      <p>欢迎进入厨师端工作系统</p>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/cooker/login')">厨师登录</el-button>
        <el-button @click="$router.push('/cooker/register')">厨师注册</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'IndexView'
}
</script>

<style scoped>
.landing {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: radial-gradient(circle at top left, #edf6ff 0%, #f8fafc 45%, #e8f2f0 100%);
}

.landing-card {
  width: 100%;
  max-width: 560px;
  text-align: center;
  border: 1px solid #e3ebf5;
}

h1 {
  font-size: 30px;
  font-weight: 700;
  color: #223447;
}

p {
  margin-top: 12px;
  color: #607086;
}

.actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
