<template>
  <div class="index">
    <div class="content">
      <!-- Logo 区域 -->
      <div class="logo-container">
        <el-icon :size="64" color="#409eff"><KnifeFork /></el-icon>
      </div>

      <!-- 标题 -->
      <h1 class="main-title">厨师上门服务平台</h1>
      <p class="subtitle">专业厨师 · 上门服务 · 美味到家</p>

      <!-- 分隔线 -->
      <el-divider />

      <!-- 功能卡片 -->
      <div class="feature-cards">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="32" color="#409eff"><Timer /></el-icon>
            </div>
          </template>
          <h3>快速预约</h3>
          <p>随时随地预约厨师</p>
        </el-card>
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="32" color="#67c23a"><Star /></el-icon>
            </div>
          </template>
          <h3>专业团队</h3>
          <p>认证厨师专业服务</p>
        </el-card>
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon :size="32" color="#e6a23c"><Location /></el-icon>
            </div>
          </template>
          <h3>上门服务</h3>
          <p>足不出户享受美食</p>
        </el-card>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          @click="$router.push('/cooker/login')"
        >
          <el-icon style="margin-right: 5px"><User /></el-icon>
          加入我们
        </el-button>
      </div>

      <!-- 底部链接 -->
      <div class="footer-links">
        <el-link :underline="false" @click="$router.push('/user/login')">
          <el-icon><User /></el-icon>
          用户端登录
        </el-link>
        <span class="divider">|</span>
        <el-link :underline="false" @click="$router.push('/cooker/register')">
          <el-icon><UserFilled /></el-icon>
          厨师注册
        </el-link>
      </div>
    </div>
  </div>
</template>

<script>
import { Timer, Star, Location, User, KnifeFork, UserFilled } from '@element-plus/icons-vue'

export default {
  name: 'Index',
  components: {
    Timer,
    Star,
    Location,
    User,
    KnifeFork,
    UserFilled
  }
}
</script>

<style lang="scss" scoped>
.index {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.content {
  text-align: center;
  max-width: 1000px;
  padding: 50px 40px;
  width: 100%;

  .logo-container {
    margin-bottom: 20px;
  }

  .main-title {
    font-size: 36px;
    font-weight: 500;
    color: #303133;
    margin: 16px 0 8px 0;
  }

  .subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0 0 20px 0;
  }
}

.feature-cards {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px 0;
  flex-wrap: wrap;

  .feature-card {
    width: 250px;
    text-align: center;

    .card-header {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    h3 {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin: 10px 0 5px 0;
    }

    p {
      font-size: 13px;
      color: #909399;
      margin: 0;
    }
  }
}

.action-buttons {
  margin: 30px 0;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  font-size: 14px;

  .divider {
    color: #dcdfe6;
  }

  :deep(.el-link) {
    color: #606266;

    .el-icon {
      margin-right: 5px;
    }

    &:hover {
      color: #409eff;
    }
  }
}

@media (max-width: 768px) {
  .content {
    padding: 30px 20px;
  }

  .main-title {
    font-size: 28px;
  }

  .feature-cards {
    flex-direction: column;
    align-items: center;

    .feature-card {
      width: 100%;
      max-width: 300px;
    }
  }

  .footer-links {
    flex-direction: column;
    gap: 10px;

    .divider {
      display: none;
    }
  }
}
</style>
