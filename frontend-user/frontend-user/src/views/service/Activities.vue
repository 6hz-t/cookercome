<template>
  <div class="activities">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Ticket /></el-icon>
        优惠活动
      </h2>
      <p class="panel-desc">精彩活动不容错过</p>
    </div>
    
    <!-- 空状态提示 -->
    <div class="empty-state">
      <div class="empty-icon">
        <el-icon :size="80" color="#667eea"><Ticket /></el-icon>
      </div>
      <div class="empty-text">
        <h3>暂无优惠活动</h3>
        <p>当前没有正在进行的活动，敬请期待！</p>
      </div>
      <div class="empty-decoration">
        <div class="decoration-orb decoration-orb-1"></div>
        <div class="decoration-orb decoration-orb-2"></div>
        <div class="decoration-orb decoration-orb-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Ticket } from '@element-plus/icons-vue'

// 当前不考虑做优惠活动功能，显示空状态提示
</script>

<style scoped>
.activities {
  min-height: 600px;
  display: flex;
  flex-direction: column;
}

/* 面板头部 */
.panel-header {
  margin-bottom: 40px;
  text-align: center;
}

.panel-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.panel-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 空状态样式 */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 60px 20px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.2);
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-text {
  text-align: center;
  z-index: 1;
}

.empty-text h3 {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px 0;
  letter-spacing: 1px;
}

.empty-text p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

/* 装饰元素 */
.empty-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.decoration-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.3;
  animation: orbFloat 8s ease-in-out infinite;
}

.decoration-orb-1 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: 20%;
  left: 15%;
  animation-delay: 0s;
}

.decoration-orb-2 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  bottom: 20%;
  right: 15%;
  animation-delay: 2s;
}

.decoration-orb-3 {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  top: 60%;
  left: 70%;
  animation-delay: 4s;
}

@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }
  25% {
    transform: translate(30px, -30px) scale(1.1);
    opacity: 0.4;
  }
  50% {
    transform: translate(-20px, 20px) scale(0.9);
    opacity: 0.2;
  }
  75% {
    transform: translate(20px, 30px) scale(1.05);
    opacity: 0.35;
  }
}
</style>
