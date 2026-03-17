<template>
  <div class="login-container">
    <!-- 背景层 -->
    <div class="background-layer">
      <div class="background-gradient-left"></div>
      <div class="background-image"></div>
      <div class="background-gradient-right"></div>
    </div>
    
    <div class="login-card scroll-animate" data-direction="bottom">
      <!-- 左侧装饰 -->
      <div class="login-left">
        <div class="brand-section">
          <div class="logo-icon">
            <el-icon><Star /></el-icon>
          </div>
          <h1 class="brand-title">厨师上门</h1>
          <p class="brand-subtitle">专业厨师 · 上门服务 · 美味到家</p>
          
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Star /></el-icon>
              <span>专业认证</span>
            </div>
            <div class="feature-item">
              <el-icon><Location /></el-icon>
              <span>准时上门</span>
            </div>
            <div class="feature-item">
              <el-icon><Lock /></el-icon>
              <span>安全保障</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="login-right">
        <div class="form-header">
          <h2 class="title">欢迎回来</h2>
          <p class="subtitle">登录后享受更多服务</p>
        </div>
        
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0px">
          <el-form-item prop="phone">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Cellphone /></el-icon>
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号"
                size="large"
                clearable
                maxlength="11"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                size="large"
                @keyup.enter="handleLogin"
              />
            </div>
          </el-form-item>
          
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="'never'">忘记密码？</el-link>
          </div>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              style="width: 100%" 
              :loading="loading" 
              @click="handleLogin"
              class="login-btn"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
          
          <div class="divider">
            <span>其他登录方式</span>
          </div>
          
          <div class="social-login">
            <el-button circle size="large" class="social-btn wechat">
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
            <el-button circle size="large" class="social-btn qq">
              <el-icon><ChatLineRound /></el-icon>
            </el-button>
            <el-button circle size="large" class="social-btn phone">
              <el-icon><Cellphone /></el-icon>
            </el-button>
          </div>
          
          <div class="tips">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getAccountInfo, hasRememberMe } from '@/utils/token'
import { User, Lock, Star, Location, ChatDotRound, ChatLineRound, Cellphone } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

// 页面加载时检查是否有记住的账号信息
onMounted(() => {
  const accountInfo = getAccountInfo()
  if (accountInfo) {
    // 自动填充手机号和解密后的密码
    form.phone = accountInfo.phone
    form.password = accountInfo.password
    rememberMe.value = hasRememberMe()
  }
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 传入 rememberMe 参数
        const res = await userStore.loginAction(form.phone, form.password, rememberMe.value)
        ElMessage.success('登录成功')
        
        // 根据角色跳转不同页面
        // role: 0-客户，1-厨师，2-管理员
        const role = res.userInfo?.role
        if (role === 1) {
          // 厨师角色，跳转到厨师端首页（使用 History 模式，不带 #）
          window.location.href = 'http://localhost:5175/cooker/todo'
        } else if (role === 2) {
          // 管理员角色，跳转到管理后台（使用 History 模式，不带 #）
          window.location.href = 'http://localhost:5174/dashboard'
        } else {
          // 默认客户角色，跳转到用户端首页
          await router.push('/')
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 滚动渐入动画 */
.scroll-animate {
  opacity: 1;
  transform: translateY(0);
  transition: all 0.6s ease-out;
}

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding-top: 100px;
}

/* 背景层 */
.background-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.background-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('@/assets/images/lorrbackground.png') center/cover no-repeat;
  opacity: 0.7;
}

.background-gradient-left {
  position: absolute;
  top: 0;
  left: 0;
  width: 65%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.9) 0%, rgba(255, 255, 255, 0) 100%);
}

.background-gradient-right {
  position: absolute;
  top: 0;
  right: 0;
  width: 65%;
  height: 100%;
  background: linear-gradient(270deg, rgba(0, 0, 0, 0.9) 0%, rgba(255, 255, 255, 0) 100%);
}

.login-card {
  display: flex;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  min-height: 550px;
  max-width: 700px;
  width: 100%;
}

/* 左侧品牌区域 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #374ca5 0%, #1b1b1b 100%);
  padding: 0 0 0 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}


@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.brand-section {
  position: relative;
  z-index: 1;
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  backdrop-filter: blur(10px);
}

.logo-icon .el-icon {
  font-size: 36px;
}

.brand-title {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 13px;
  opacity: 0.9;
  margin: 0 0 30px 0;
  line-height: 1.6;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.95;
}

.feature-item .el-icon {
  font-size: 20px;
  color: #ffd700;
}

/* 右侧表单区域 */
.login-right {
  flex: 1.3;
  padding: 40px 30px;
  background: white;
}

.form-header {
  margin-bottom: 30px;
}

.title {
  font-size: 26px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 0 15px;
  transition: all 0.3s ease;
}

.input-wrapper:hover {
  border-color: #dcdfe6;
}

.input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-icon {
  font-size: 20px;
  color: #999;
  margin-right: 10px;
}

.input-wrapper :deep(.el-input__wrapper) {
  box-shadow: none !important;
  flex: 1;
}

.input-wrapper :deep(.el-input__inner) {
  font-size: 15px;
  padding: 12px 0;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.login-btn {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 10px 0;
  color: #999;
  font-size: 14px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #e4e7ed;
}

.divider span {
  padding: 0 15px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 30px;
}

.social-btn {
  width: 45px;
  height: 45px;
  border: 2px solid #e4e7ed;
  background: white;
  transition: all 0.3s ease;
}

.social-btn:hover {
  transform: scale(1.1);
}

.social-btn.wechat:hover {
  border-color: #07c160;
  color: #07c160;
}

.social-btn.qq:hover {
  border-color: #12b7f5;
  color: #12b7f5;
}

.social-btn.phone:hover {
  border-color: #667eea;
  color: #667eea;
}

.tips {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
  }
  
  .login-left {
    padding: 40px 20px;
  }
  
  .login-right {
    padding: 40px 20px;
  }
  
  .brand-title {
    font-size: 28px;
  }
  
  .title {
    font-size: 26px;
  }
}

/* 页面进入动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.4s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(-100px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(100px);
}
</style>
