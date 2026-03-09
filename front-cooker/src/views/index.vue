<template>
  <div class="index">
    <!-- 粒子背景效果 -->
    <div class="particles">
      <span v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></span>
    </div>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>

    <div class="content">
      <!-- Logo 区域 -->
      <div class="logo-container">
        <div class="logo-glow"></div>
        <div class="logo-icon">
          <el-icon :size="64"><KnifeFork /></el-icon>
        </div>
        <div class="logo-ring"></div>
      </div>

      <!-- 标题 -->
      <h1 class="main-title">
        <span class="title-text">厨师上门服务平台</span>
      </h1>
      <p class="subtitle">专业厨师 · 上门服务 · 美味到家</p>

      <!-- 分隔线 -->
      <div class="divider-line"></div>

      <!-- 功能卡片 -->
      <div class="feature-cards">
        <div class="feature-card" :style="getCardDelayStyle(0)">
          <div class="feature-icon icon-1">
            <el-icon :size="32"><Timer /></el-icon>
          </div>
          <h3>快速预约</h3>
          <p>随时随地预约厨师</p>
          <div class="card-glow"></div>
        </div>
        <div class="feature-card" :style="getCardDelayStyle(1)">
          <div class="feature-icon icon-2">
            <el-icon :size="32"><Star /></el-icon>
          </div>
          <h3>专业团队</h3>
          <p>认证厨师专业服务</p>
          <div class="card-glow"></div>
        </div>
        <div class="feature-card" :style="getCardDelayStyle(2)">
          <div class="feature-icon icon-3">
            <el-icon :size="32"><Location /></el-icon>
          </div>
          <h3>上门服务</h3>
          <p>足不出户享受美食</p>
          <div class="card-glow"></div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          class="enter-btn"
          @click="$router.push('/cooker/login')"
        >
          <el-icon class="btn-icon"><User /></el-icon>
          加入我们
          <span class="btn-shine"></span>
        </el-button>
      </div>

      <!-- 底部链接 -->
      <div class="footer-links">
        <el-link type="info" :underline="false" @click="$router.push('/user/login')">
          <el-icon><User /></el-icon>
          用户端登录
        </el-link>
        <span class="divider">|</span>
        <el-link type="info" :underline="false" @click="$router.push('/cooker/register')">
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
  },
  methods: {
    getParticleStyle(i) {
      const delay = Math.random() * 5
      const left = Math.random() * 100
      const duration = 15 + Math.random() * 10
      const size = 2 + Math.random() * 4
      return {
        left: `${left}%`,
        animationDelay: `${delay}s`,
        animationDuration: `${duration}s`,
        width: `${size}px`,
        height: `${size}px`
      }
    },
    getCardDelayStyle(index) {
      return {
        animationDelay: `${index * 0.15}s`
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.index {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('@/assets/b1g.jpg') no-repeat center center/cover;
  position: relative;
  overflow: hidden;

  // 遮罩层，让背景图变暗以突出内容
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.8) 0%, rgba(118, 75, 162, 0.85) 50%, rgba(102, 126, 234, 0.8) 100%);
    z-index: 0;
  }
}

// 粒子效果
.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;

  .particle {
    position: absolute;
    bottom: -10px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 50%;
    animation: rise linear infinite;
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);

    @keyframes rise {
      0% {
        transform: translateY(0) translateX(0);
        opacity: 0;
      }
      10% {
        opacity: 1;
      }
      90% {
        opacity: 1;
      }
      100% {
        transform: translateY(-100vh) translateX(20px);
        opacity: 0;
      }
    }
  }
}

// 背景装饰
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;

  .circle {
    position: absolute;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
    animation: float 8s ease-in-out infinite;
  }

  .circle-1 {
    width: 400px;
    height: 400px;
    top: -150px;
    right: -150px;
  }

  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -100px;
    left: -100px;
    animation-delay: -3s;
  }

  .circle-3 {
    width: 200px;
    height: 200px;
    top: 40%;
    left: 5%;
    animation-delay: -5s;
  }

  .circle-4 {
    width: 250px;
    height: 250px;
    top: 20%;
    right: 15%;
    animation-delay: -2s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  33% {
    transform: translateY(-30px) rotate(5deg) scale(1.05);
  }
  66% {
    transform: translateY(-15px) rotate(-5deg) scale(0.95);
  }
}

.content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 2;
  max-width: 1000px;
  padding: 50px 40px;
  animation: fadeInUp 0.8s ease-out;

  .logo-container {
    position: relative;
    display: inline-block;
    margin-bottom: 35px;

    .logo-glow {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 140px;
      height: 140px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.4) 0%, transparent 70%);
      border-radius: 50%;
      animation: glow 2s ease-in-out infinite;
    }

    .logo-icon {
      position: relative;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 120px;
      height: 120px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.1) 100%);
      backdrop-filter: blur(20px);
      border-radius: 50%;
      color: white;
      box-shadow: 
        0 8px 32px rgba(0, 0, 0, 0.3),
        inset 0 2px 10px rgba(255, 255, 255, 0.3);
      border: 2px solid rgba(255, 255, 255, 0.3);
      animation: logoFloat 3s ease-in-out infinite;
      z-index: 1;
    }

    .logo-ring {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 160px;
      height: 160px;
      border: 2px solid rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      animation: ringPulse 3s ease-in-out infinite;
    }
  }
}

@keyframes glow {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.8;
  }
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-8px) rotate(2deg);
  }
}

@keyframes ringPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.15);
    opacity: 0.6;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.main-title {
  font-size: 56px;
  font-weight: 700;
  margin: 0 0 16px 0;
  letter-spacing: 6px;
  position: relative;

  .title-text {
    background: linear-gradient(180deg, #ffffff 0%, #e0e0e0 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    text-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
    animation: titleGlow 3s ease-in-out infinite;
  }
}

@keyframes titleGlow {
  0%, 100% {
    filter: brightness(1);
  }
  50% {
    filter: brightness(1.15);
  }
}

.subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin: 0 0 30px 0;
  letter-spacing: 3px;
  font-weight: 300;
}

// 分隔线
.divider-line {
  width: 100px;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
  margin: 0 auto 40px;
  border-radius: 2px;
  animation: lineExpand 1s ease-out 0.3s both;
}

@keyframes lineExpand {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 100px;
    opacity: 1;
  }
}

// 功能卡片
.feature-cards {
  display: flex;
  justify-content: center;
  gap: 28px;
  margin-bottom: 55px;
  flex-wrap: wrap;

  .feature-card {
    position: relative;
    width: 200px;
    padding: 30px 24px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.25);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    cursor: pointer;
    overflow: hidden;
    animation: cardEntrance 0.6s ease-out both;

    @keyframes cardEntrance {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .card-glow {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.5s ease;
    }

    &:hover {
      transform: translateY(-12px) scale(1.03);
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.25) 0%, rgba(255, 255, 255, 0.15) 100%);
      box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.3),
        0 0 40px rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.4);

      .card-glow {
        left: 100%;
      }

      .feature-icon {
        transform: scale(1.15) rotate(5deg);
      }
    }

    .feature-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 70px;
      height: 70px;
      margin: 0 auto 18px;
      border-radius: 50%;
      color: white;
      transition: all 0.4s ease;
      position: relative;
      z-index: 1;

      &.icon-1 {
        background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
        box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
      }

      &.icon-2 {
        background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
        box-shadow: 0 6px 20px rgba(161, 140, 209, 0.4);
      }

      &.icon-3 {
        background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
        box-shadow: 0 6px 20px rgba(132, 250, 176, 0.4);
      }
    }

    h3 {
      font-size: 19px;
      margin: 0 0 10px 0;
      font-weight: 600;
      position: relative;
      z-index: 1;
    }

    p {
      font-size: 13px;
      margin: 0;
      opacity: 0.85;
      line-height: 1.6;
      position: relative;
      z-index: 1;
    }
  }
}

// 操作按钮
.action-buttons {
  margin-bottom: 45px;
  position: relative;

  .enter-btn {
    position: relative;
    height: 60px;
    padding: 0 56px;
    font-size: 19px;
    font-weight: 600;
    background: linear-gradient(135deg, #ffffff 0%, #f5f5f5 100%);
    color: #667eea;
    border: none;
    border-radius: 14px;
    box-shadow: 
      0 10px 40px rgba(0, 0, 0, 0.25),
      0 0 0 1px rgba(255, 255, 255, 0.3) inset;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    overflow: hidden;

    .btn-icon {
      margin-right: 10px;
      font-size: 20px;
    }

    .btn-shine {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        90deg,
        transparent,
        rgba(255, 255, 255, 0.6),
        transparent
      );
      transition: left 0.6s ease;
    }

    &:hover {
      transform: translateY(-6px) scale(1.02);
      box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.35),
        0 0 60px rgba(255, 255, 255, 0.2);

      .btn-shine {
        left: 100%;
      }
    }

    &:active {
      transform: translateY(-3px) scale(0.98);
    }
  }
}

// 底部链接
.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  flex-wrap: wrap;

  .divider {
    color: rgba(255, 255, 255, 0.4);
    font-weight: 300;
  }

  :deep(.el-link) {
    color: rgba(255, 255, 255, 0.85);
    font-weight: 500;
    transition: all 0.3s ease;
    padding: 8px 16px;
    border-radius: 8px;

    .el-icon {
      margin-right: 6px;
    }

    &:hover {
      color: white;
      background: rgba(255, 255, 255, 0.1);
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .content {
    padding: 30px 20px;
  }

  .main-title {
    font-size: 32px;
    letter-spacing: 3px;
  }

  .subtitle {
    font-size: 14px;
    letter-spacing: 2px;
  }

  .divider-line {
    margin: 0 auto 30px;
  }

  .feature-cards {
    flex-direction: column;
    align-items: center;
    gap: 20px;

    .feature-card {
      width: 100%;
      max-width: 300px;
      padding: 24px 20px;
    }
  }

  .action-buttons {
    .enter-btn {
      width: 100%;
      max-width: 300px;
      padding: 0 40px;
      font-size: 17px;
      height: 54px;
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

// 大屏优化
@media (min-width: 1200px) {
  .content {
    max-width: 1200px;
  }

  .main-title {
    font-size: 64px;
  }

  .feature-cards {
    gap: 40px;

    .feature-card {
      width: 220px;
      padding: 35px 28px;
    }
  }
}
</style>
