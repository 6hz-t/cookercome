<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <div class="card-head">
        <h1>厨师端登录</h1>
        <p>请输入手机号和密码</p>
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
    </el-card>
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

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f6f8fb;
  font-family: 'Source Han Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  border-radius: 14px;
  border: 1px solid var(--el-border-color-light);
}

.card-head {
  margin-bottom: 10px;
  text-align: center;
}

.card-head h1 {
  margin: 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.card-head p {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.auth-form {
  margin-top: 8px;
}

.w-full {
  width: 100%;
}

.footer-link {
  margin-top: 8px;
  text-align: center;
  color: var(--el-text-color-secondary);
}

@media (max-width: 640px) {
  .auth-page {
    padding: 12px;
  }

  .auth-card {
    border-radius: 12px;
  }
}
</style>
