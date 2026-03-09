<template>
  <div class="register-container">
    <!-- 背景层 -->
    <div class="background-layer">
      <div class="background-gradient-left"></div>
      <div class="background-image"></div>
      <div class="background-gradient-right"></div>
    </div>
    
    <div class="register-card scroll-animate" data-direction="bottom">
      <!-- 左侧装饰 -->
      <div class="register-left">
        <div class="brand-section">
          <div class="logo-icon">
            <el-icon><Star /></el-icon>
          </div>
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-subtitle">开启您的美食之旅</p>
          
          <div class="benefit-list">
            <div class="benefit-item">
              <el-icon><Check /></el-icon>
              <span>专业认证厨师</span>
            </div>
            <div class="benefit-item">
              <el-icon><Check /></el-icon>
              <span>灵活预约时间</span>
            </div>
            <div class="benefit-item">
              <el-icon><Check /></el-icon>
              <span>透明消费保障</span>
            </div>
            <div class="benefit-item">
              <el-icon><Check /></el-icon>
              <span>贴心客服支持</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="register-right">
        <div class="form-header">
          <h2 class="title">创建账号</h2>
          <p class="subtitle">填写以下信息，立即开始使用</p>
        </div>
        
        <el-form :model="form" :rules="rules" ref="formRef" label-width="0px">
          <el-form-item prop="phone">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Cellphone /></el-icon>
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号码"
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
                placeholder="请输入密码（至少 6 位）"
                size="large"
                show-password
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                size="large"
                show-password
                @keyup.enter="handleRegister"
              />
            </div>
          </el-form-item>
          
          <div class="agreement">
            <el-checkbox v-model="agreeTerms" size="large">
              我已阅读并同意
              <el-link type="primary" :underline="'never'" @click.stop="showAgreement('user')">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="'never'" @click.stop="showAgreement('privacy')">《隐私政策》</el-link>
            </el-checkbox>
          </div>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              style="width: 100%" 
              :loading="loading" 
              @click="handleRegister"
              class="register-btn"
              :disabled="!agreeTerms"
            >
              <span v-if="!loading">注 册</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>
          
          <div class="tips">
            <span>已有账号？</span>
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { User, Lock, Cellphone, Check, Star } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const agreeTerms = ref(false)

const form = reactive({
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 显示协议
const showAgreement = (type) => {
  const title = type === 'user' ? '用户协议' : '隐私政策'
  const content = type === 'user' 
    ? '这里是用户协议的详细内容...'
    : '这里是隐私政策的详细内容...'
  
  ElMessageBox.alert(content, title, {
    confirmButtonText: '我已知晓',
    dangerouslyUseHTMLString: true
  })
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  if (!agreeTerms.value) {
    ElMessage.warning('请先同意用户协议和隐私政策')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.registerAction(
          form.phone,
          form.password
        )
        ElMessage.success('注册成功', {
          duration: 1500
        })
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } catch (error) {
        console.error('注册失败:', error)
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

.register-container {
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
  background: linear-gradient(90deg, rgba(0, 0, 0, 1) 0%, rgba(255, 255, 255, 0) 100%);
}

.background-gradient-right {
  position: absolute;
  top: 0;
  right: 0;
  width: 65%;
  height: 100%;
  background: linear-gradient(270deg, rgba(0, 0, 0, 1) 0%, rgba(255, 255, 255, 0) 100%);
}

.register-card {
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
.register-left {
  flex: 1;
  background: linear-gradient(135deg, #3193af 0%, #151515 100%);
  padding: 0 0 0 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
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

.benefit-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.95;
}

.benefit-item .el-icon {
  font-size: 20px;
  color: #4ade80;
}

/* 右侧表单区域 */
.register-right {
  flex: 1.3;
  padding: 40px 30px;
  background: white;
}

/* 页面进入动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.4s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(100px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-100px);
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

.agreement {
  margin: 20px 0 25px 0;
}

.register-btn {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.tips {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 20px;
}

.login-link {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.login-link:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    flex-direction: column;
  }
  
  .register-left {
    padding: 40px 20px;
  }
  
  .register-right {
    padding: 40px 20px;
  }
  
  .brand-title {
    font-size: 28px;
  }
  
  .title {
    font-size: 26px;
  }
}
</style>
