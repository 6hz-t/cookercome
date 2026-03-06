<template>
  <div class="index">
    <div class="container">
      <h1>🍳 厨师上门服务平台</h1>
      
      <div class="entry-cards">
        <!-- 用户端 -->
        <div class="card">
          <div class="icon">👤</div>
          <h2>用户端</h2>
          <p>预约厨师，享受上门美食服务</p>
          <router-link to="/user/login" class="btn">进入用户端</router-link>
        </div>

        <!-- 厨师端 -->
        <div class="card">
          <div class="icon">👨‍🍳</div>
          <h2>厨师端</h2>
          <p>接单服务，展示厨艺风采</p>
          <router-link to="/cooker/login" class="btn">进入厨师端</router-link>
        </div>

        <!-- 管理端 -->
        <div class="card">
          <div class="icon">⚙️</div>
          <h2>管理端</h2>
          <p>平台管理，数据监控</p>
          <router-link to="/admin/login" class="btn">进入管理端</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Index'
}
</script>

<style scoped>
.index {
  width: 1000px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  text-align: center;
}

h1 {
  color: white;
  font-size: 2rem;
  margin-bottom: 2rem;
}

.entry-cards {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  width: 260px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s;
}

.card:hover {
  transform: translateY(-5px);
}

.icon {
  font-size: 3.5rem;
  margin-bottom: 1rem;
}

h2 {
  color: #333;
  font-size: 1.25rem;
  margin: 0 0 0.5rem;
}

p {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 1.5rem;
}

.btn {
  display: inline-block;
  padding: 0.6rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 20px;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  h1 {
    font-size: 1.5rem;
  }
  
  .card {
    width: 100%;
  }
}
</style>
