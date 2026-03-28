<!-- 厨师登录 - 现代化设计 -->
<template>
  <div class="cooker-login">
    <div class="login-wrapper">
      <!-- 左侧品牌展示区 -->
      <div class="login-banner">
        <div class="banner-overlay"></div>
        <div class="banner-content">
          <div class="brand-logo">
            <div class="logo-icon">
              <el-icon :size="56" color="#fff"><Sunrise /></el-icon>
            </div>
            <h1 class="brand-title">厨师上门</h1>
          </div>
          <h2 class="banner-title">专业厨师 · 品质服务</h2>
          <p class="banner-desc">
            加入我们的专业厨师团队，<br />
            开启您的上门烹饪事业之旅
          </p>
          <div class="banner-features">
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="24"><Checked /></el-icon>
              </div>
              <span>严格身份认证</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="24"><Timer /></el-icon>
              </div>
              <span>时间自由灵活</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <el-icon :size="24"><Money /></el-icon>
              </div>
              <span>收入透明可观</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-form-section">
        <div class="form-container">
          <!-- 登录表单 -->
          <transition name="fade" mode="out-in">
            <div v-if="isLogin" key="login" class="form-content">
              <div class="form-header">
                <h2 class="form-title">欢迎回来</h2>
                <p class="form-subtitle">请登录您的厨师账号</p>
              </div>

              <el-form :model="user" :rules="rules" ref="ruleForm" label-width="0">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input
                      v-model="user.username"
                      placeholder="请输入用户名"
                      size="large"
                      clearable
                    />
                  </div>
                </el-form-item>

                <el-form-item prop="password">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Lock /></el-icon>
                    <el-input
                      v-model="user.password"
                      type="password"
                      placeholder="请输入密码"
                      size="large"
                      show-password
                      @keyup.enter="submitForm('ruleForm')"
                    />
                  </div>
                </el-form-item>

                <div class="form-options">
                  <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                  <el-link class="forgot-link" :underline="false">忘记密码？</el-link>
                </div>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    class="submit-btn"
                    @click="submitForm('ruleForm')"
                    :loading="loading"
                  >
                    登录
                  </el-button>
                </el-form-item>
              </el-form>

              <div class="divider">
                <span>其他登录方式</span>
              </div>

              <div class="social-login">
                <el-button circle class="social-btn wechat">
                  <el-icon><ChatDotRound /></el-icon>
                </el-button>
                <el-button circle class="social-btn phone">
                  <el-icon><Phone /></el-icon>
                </el-button>
              </div>

              <div class="form-footer">
                <span>还没有账号？</span>
                <el-link class="switch-link" :underline="false" @click="toggleForm">
                  立即注册
                </el-link>
              </div>
            </div>

            <!-- 注册表单 -->
            <div v-else key="register" class="form-content">
              <div class="form-header">
                <h2 class="form-title">创建账号</h2>
                <p class="form-subtitle">填写以下信息完成注册</p>
              </div>

              <el-form :model="form" :rules="rules" ref="registerForm" label-width="0">
                <el-form-item prop="username">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><User /></el-icon>
                    <el-input
                      v-model="form.username"
                      placeholder="请输入用户名"
                      size="large"
                      clearable
                    />
                  </div>
                </el-form-item>

                <el-form-item prop="phone">
                  <div class="input-wrapper">
                    <el-icon class="input-icon"><Phone /></el-icon>
                    <el-input
                      v-model="form.phone"
                      placeholder="请输入手机号"
                      size="large"
                      clearable
                      maxlength="11"
                    />
                  </div>
                </el-form-item>

                <el-form-item prop="code">
                  <div class="code-input-wrapper">
                    <div class="input-wrapper code-input">
                      <el-icon class="input-icon"><Message /></el-icon>
                      <el-input
                        v-model="form.code"
                        placeholder="请输入验证码"
                        size="large"
                        clearable
                        maxlength="6"
                      />
                    </div>
                    <el-button 
                      class="code-btn" 
                      :disabled="countdown > 0"
                      @click="sendCode"
                    >
                      {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                    </el-button>
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
                      placeholder="请确认密码"
                      size="large"
                      show-password
                    />
                  </div>
                </el-form-item>

                <el-form-item>
                  <div class="agreement">
                    <el-checkbox v-model="agreed"></el-checkbox>
                    <span>我已阅读并同意</span>
                    <el-link class="agreement-link" :underline="false">《服务协议》</el-link>
                    <span>和</span>
                    <el-link class="agreement-link" :underline="false">《隐私政策》</el-link>
                  </div>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    class="submit-btn"
                    @click="submitRegister('registerForm')"
                    :loading="loading"
                    :disabled="!agreed"
                  >
                    同意协议并注册
                  </el-button>
                </el-form-item>
              </el-form>

              <div class="form-footer">
                <span>已有账号？</span>
                <el-link class="switch-link" :underline="false" @click="toggleForm">
                  立即登录
                </el-link>
              </div>
            </div>
          </transition>
        </div>

        <!-- 底部链接 -->
        <div class="form-footer-links">
          <el-link :underline="false">关于我们</el-link>
          <el-link :underline="false">帮助中心</el-link>
          <el-link :underline="false">隐私政策</el-link>
          <el-link :underline="false">服务条款</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { User, Lock, Sunrise, Checked, Timer, Money, ChatDotRound, Phone, Message } from '@element-plus/icons-vue'

export default {
  name: 'CookerLogin',
  components: {
    Sunrise,
    ChatDotRound
  },
  data() {
    var validateUsername = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入用户名'));
      } else if (value.length < 3) {
        callback(new Error('用户名至少 3 个字符'));
      } else {
        callback();
      }
    };
    var validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else if (value.length < 6) {
        callback(new Error('密码至少 6 个字符'));
      } else {
        callback();
      }
    };
    var validatePhone = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入手机号'));
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'));
      } else {
        callback();
      }
    };
    var validateCode = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入验证码'));
      } else if (value.length !== 6) {
        callback(new Error('验证码为 6 位数字'));
      } else {
        callback();
      }
    };
    var validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认密码'));
      } else if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      isLogin: true,
      loading: false,
      rememberMe: false,
      agreed: false,
      countdown: 0,
      timer: null,
      user: {
        username: '',
        password: ''
      },
      form: {
        username: '',
        phone: '',
        code: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [{ validator: validateUsername, trigger: 'blur' }],
        password: [{ validator: validatePassword, trigger: 'blur' }],
        phone: [{ validator: validatePhone, trigger: 'blur' }],
        code: [{ validator: validateCode, trigger: 'blur' }],
        confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
      }
    }
  },
  methods: {
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          setTimeout(() => {
            this.loading = false;
            this.$message.success('登录成功！');
            this.$router.push('/cooker/todo');
          }, 1000);
        } else {
          this.$message.error('请填写完整的登录信息');
          return false;
        }
      });
    },
    submitRegister(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (!this.agreed) {
            this.$message.warning('请先同意服务协议和隐私政策');
            return;
          }
          this.loading = true;
          setTimeout(() => {
            this.loading = false;
            this.$message.success('注册成功！');
            this.isLogin = true;
          }, 1000);
        } else {
          this.$message.error('请填写完整的注册信息');
          return false;
        }
      });
    },
    sendCode() {
      if (!this.form.phone || !/^1[3-9]\d{9}$/.test(this.form.phone)) {
        this.$message.warning('请先输入正确的手机号');
        return;
      }
      this.$message.success('验证码已发送');
      this.countdown = 60;
      this.timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(this.timer);
        }
      }, 1000);
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }
}
</script>

<style lang="scss" scoped>
// 主题色变量
$primary-gradient: linear-gradient(135deg, #9ED2BE 0%, #5A9367 100%);
$primary-color: #5A9367;
$primary-light: #9ED2BE;
$text-primary: #3A4B3F;
$text-secondary: #7A8C80;
$text-light: #A3B3A8;
$bg-soft: #F2F8F5;
$border-color: #E6EEE9;

.cooker-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #F2F8F5 0%, #E8F0EC 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 900px;
  min-height: 650px;
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(90, 147, 103, 0.15);
  display: flex;
  overflow: hidden;
}

/* ========== 左侧品牌展示区 ========== */
.login-banner {
  flex: 1;
  min-width: 350px;
  background: linear-gradient(135deg, #9ED2BE 0%, #5A9367 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  overflow: hidden;

  .banner-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="40" fill="rgba(255,255,255,0.05)"/></svg>');
    background-size: 100px 100px;
  }

  .banner-content {
    position: relative;
    z-index: 1;
    text-align: center;
    color: #fff;

    .brand-logo {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;
      margin-bottom: 32px;

      .logo-icon {
        width: 80px;
        height: 80px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        backdrop-filter: blur(10px);
      }

      .brand-title {
        font-size: 2.2rem;
        font-weight: 700;
        margin: 0;
      }
    }

    .banner-title {
      font-size: 1.8rem;
      font-weight: 600;
      margin: 0 0 16px;
    }

    .banner-desc {
      font-size: 1rem;
      font-weight: 400;
      opacity: 0.9;
      margin: 0 0 40px;
      line-height: 1.6;
    }

    .banner-features {
      display: flex;
      flex-direction: column;
      gap: 20px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 12px;

        .feature-icon {
          width: 40px;
          height: 40px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        span {
          font-size: 0.95rem;
          font-weight: 500;
        }
      }
    }
  }
}

/* ========== 右侧表单区 ========== */
.login-form-section {
  flex: 1;
  min-width: 350px;
  display: flex;
  flex-direction: column;
  padding: 40px;
}

.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-content {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-header {
  margin-bottom: 32px;

  .form-title {
    font-size: 1.8rem;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 8px;
  }

  .form-subtitle {
    font-size: 0.95rem;
    font-weight: 400;
    color: $text-secondary;
    margin: 0;
  }
}

/* 输入框样式 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid $border-color;
  border-radius: 8px;
  padding: 0 16px;
  transition: all 0.3s;
  background: #fff;

  &:hover {
    border-color: $text-light;
  }

  &:focus-within {
    border-color: $primary-color;
    box-shadow: 0 0 0 3px rgba(90, 147, 103, 0.1);
  }

  .input-icon {
    font-size: 20px;
    color: $text-light;
    margin-right: 12px;
  }

  :deep(.el-input__wrapper) {
    box-shadow: none;
    padding: 0;

    .el-input__inner {
      height: 50px;
      font-size: 1rem;
      color: $text-primary;
    }
  }
}

.code-input-wrapper {
  display: flex;
  gap: 12px;

  .code-input {
    flex: 1;
  }

  .code-btn {
    height: 50px;
    padding: 0 20px;
    border-radius: 8px;
    background: $primary-gradient;
    border: none;
    font-weight: 600;
    font-size: 0.95rem;
    color: #fff;
    white-space: nowrap;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(90, 147, 103, 0.3);
    }

    &:active {
      transform: translateY(0);
    }

    &:disabled {
      background: $border-color;
      cursor: not-allowed;
      transform: none;
    }
  }
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  :deep(.el-checkbox) {
    font-size: 0.9rem;
    color: $text-secondary;
  }

  .forgot-link {
    font-size: 0.9rem;
    color: $primary-color;
    font-weight: 500;
  }
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 8px;
  background: $primary-gradient;
  border: none;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(90, 147, 103, 0.3);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
}

/* 分割线 */
.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: $border-color;
  }

  span {
    padding: 0 16px;
    font-size: 0.85rem;
    color: $text-secondary;
  }
}

/* 社交登录 */
.social-login {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;

  .social-btn {
    width: 48px;
    height: 48px;
    border: 1px solid $border-color;
    background: #fff;
    color: $text-secondary;
    transition: all 0.3s;

    .el-icon {
      font-size: 22px;
    }

    &:hover {
      border-color: $primary-color;
      color: $primary-color;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(90, 147, 103, 0.15);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

/* 协议区域 */
.agreement {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 0.85rem;
  color: $text-secondary;
  line-height: 1.5;

  :deep(.el-checkbox) {
    margin-top: 2px;
  }

  .agreement-link {
    font-size: 0.85rem;
    color: $primary-color;
    font-weight: 500;
  }
}

/* 表单底部 */
.form-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 0.95rem;
  color: $text-secondary;

  span {
    margin-right: 8px;
  }

  .switch-link {
    font-size: 0.95rem;
    color: $primary-color;
    font-weight: 600;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* 底部链接 */
.form-footer-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid $border-color;

  :deep(.el-link) {
    font-size: 0.8rem;
    color: $text-secondary;

    &:hover {
      color: $primary-color;
    }
  }
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 450px;
    min-height: auto;
  }

  .login-banner {
    min-width: 100%;
    padding: 40px 25px;

    .banner-content {
      .brand-logo {
        margin-bottom: 24px;

        .logo-icon {
          width: 60px;
          height: 60px;
        }

        .brand-title {
          font-size: 1.8rem;
        }
      }

      .banner-title {
        font-size: 1.5rem;
      }

      .banner-desc {
        font-size: 0.9rem;
      }

      .banner-features {
        flex-direction: row;
        flex-wrap: wrap;
        justify-content: center;
      }
    }
  }

  .login-form-section {
    padding: 40px 25px;
  }

  .social-login {
    .social-btn {
      width: 56px;
      height: 56px;
    }
  }
}

@media (max-width: 425px) {
  .login-banner {
    padding: 30px 20px;

    .banner-features {
      .feature-item {
        flex-direction: column;
        text-align: center;
      }
    }
  }

  .login-form-section {
    padding: 30px 20px;
  }

  .form-header {
    .form-title {
      font-size: 1.5rem;
    }
  }

  .social-login {
    flex-direction: column;
    align-items: center;

    .social-btn {
      width: 100%;
      max-width: 280px;
      border-radius: 8px;
    }
  }
}
</style>
