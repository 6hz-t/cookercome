<!-- 厨师注册 -->
<template>
  <div class="cooker-register">
    <div class="register-container">
      <el-row class="register-box">
        <!-- 左侧宣传区域 -->
        <el-col :xs="0" :sm="12" :md="14" class="register-left">
          <div class="left-content">
            <div class="logo">
              <el-icon :size="64">
                <KnifeFork />
              </el-icon>
            </div>
            <h1>厨师上门服务平台</h1>
            <p>专业厨师 · 上门服务 · 美味到家</p>
            <div class="features">
              <div class="feature-item">
                <el-icon :size="40" color="#409eff"><Timer /></el-icon>
                <span>快速预约</span>
              </div>
              <div class="feature-item">
                <el-icon :size="40" color="#67c23a"><Star /></el-icon>
                <span>专业团队</span>
              </div>
              <div class="feature-item">
                <el-icon :size="40" color="#e6a23c"><Location /></el-icon>
                <span>上门服务</span>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧注册表单 -->
        <el-col :xs="24" :sm="12" :md="10" class="register-right">
          <div class="right-content">
            <div class="form-header">
              <h2>创建账号</h2>
              <p>加入我们，开启厨师服务之旅</p>
            </div>

            <el-form :model="form" :rules="rules" ref="ruleForm" class="register-form">
              <el-form-item prop="username">
                <el-input 
                  v-model="form.username" 
                  placeholder="请输入用户名" 
                  :prefix-icon="User" 
                  size="large" 
                  clearable 
                />
              </el-form-item>

              <el-form-item prop="password">
                <el-input 
                  v-model="form.password" 
                  type="password" 
                  placeholder="请输入密码" 
                  :prefix-icon="Lock" 
                  size="large"
                  show-password 
                />
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <el-input 
                  v-model="form.confirmPassword" 
                  type="password" 
                  placeholder="请确认密码" 
                  :prefix-icon="Lock" 
                  size="large"
                  show-password 
                />
              </el-form-item>

              <el-form-item prop="phone">
                <el-input 
                  v-model="form.phone" 
                  placeholder="请输入手机号" 
                  :prefix-icon="Phone" 
                  size="large" 
                  clearable
                  maxlength="11"
                />
              </el-form-item>

              <el-form-item prop="email">
                <el-input 
                  v-model="form.email" 
                  placeholder="请输入邮箱" 
                  :prefix-icon="Message" 
                  size="large" 
                  clearable 
                />
              </el-form-item>

              <el-form-item prop="realName">
                <el-input 
                  v-model="form.realName" 
                  placeholder="请输入真实姓名" 
                  :prefix-icon="UserFilled" 
                  size="large" 
                  clearable 
                />
              </el-form-item>

              <el-form-item prop="agree">
                <el-checkbox v-model="form.agree" size="large">
                  我已阅读并同意 <el-link type="primary" :underline="false">《用户协议》</el-link> 和 <el-link type="primary" :underline="false">《隐私政策》</el-link>
                </el-checkbox>
              </el-form-item>

              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  class="register-btn" 
                  @click="submitForm('ruleForm')" 
                  :loading="loading"
                >
                  立即注册
                </el-button>
              </el-form-item>

              <div class="form-footer">
                <span>已有账号？</span>
                <el-link type="primary" @click="goLogin">立即登录</el-link>
              </div>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { UserFilled, User, Lock, Phone, Message, KnifeFork, Timer, Star, Location } from '@element-plus/icons-vue'
import CryptoJS from 'crypto-js'

export default {
  name: 'CookerRegister',
  components: {
    UserFilled
  },
  data() {
    var validateUsername = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入用户名'));
      } else if (value.length < 3) {
        callback(new Error('用户名至少 3 个字符'));
      } else if (value.length > 20) {
        callback(new Error('用户名不能超过 20 个字符'));
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

    var validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认密码'));
      } else if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    var validatePhone = (rule, value, callback) => {
      if (value === '') {
        callback();
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'));
      } else {
        callback();
      }
    };

    var validateEmail = (rule, value, callback) => {
      if (value === '') {
        callback();
      } else if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
        callback(new Error('请输入正确的邮箱地址'));
      } else {
        callback();
      }
    };

    var validateAgree = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请同意用户协议和隐私政策'));
      } else {
        callback();
      }
    };

    return {
      User,
      Lock,
      Phone,
      Message,
      loading: false,
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        email: '',
        realName: '',
        agree: false
      },
      rules: {
        username: [
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        agree: [
          { validator: validateAgree, trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          
          // SHA-256 加密密码
          const encryptedPassword = CryptoJS.SHA256(this.form.password).toString()

          // 调用注册 API
          // 注意：需要在 api/cooker.js 中添加 register 方法
          import('../../api/cooker').then(({ register }) => {
            register({
              username: this.form.username,
              password: encryptedPassword,
            }).then(res => {
              
              console.log(res)
              if (res.data.code === 200 || res.code === 200) {
                this.$message.success('注册成功，请登录')
                this.$router.push('/cooker/login')
              } else {
                this.$message.error(res.data.msg || res.msg || '注册失败')
              }
            }).catch(err => {
              this.loading = false;
              console.error(err)
              this.$message.error('注册失败，请稍后重试')
            })
          })
        }
      })
    },

    goLogin() {
      this.$router.push('/cooker/login');
    }
  }
}
</script>

<style lang="scss" scoped>
.cooker-register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.register-container {
  width: 100%;
  max-width: 900px;
  padding: 20px;
}

.register-box {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.register-left {
  background-color: #409eff;
  padding: 60px 40px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;

  .left-content {
    text-align: center;

    .logo {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 100px;
      height: 100px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      margin-bottom: 24px;
      color: white;
      backdrop-filter: blur(10px);
    }

    h1 {
      font-size: 28px;
      font-weight: 600;
      margin: 0 0 12px 0;
    }

    p {
      font-size: 14px;
      opacity: 0.9;
      margin: 0 0 40px 0;
    }

    .features {
      display: flex;
      flex-direction: column;
      gap: 24px;

      .feature-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;

        span {
          font-size: 14px;
          opacity: 0.9;
        }
      }
    }
  }
}

.register-right {
  padding: 40px 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;

  .right-content {
    width: 100%;
    max-width: 320px;

    .form-header {
      text-align: center;
      margin-bottom: 24px;

      h2 {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
      }

      p {
        font-size: 14px;
        color: #909399;
        margin: 0;
      }
    }
  }
}

.register-form {
  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-checkbox) {
    margin-left: 0;
  }
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #606266;
  margin-top: 16px;

  span {
    margin-right: 8px;
  }
}

// 响应式
@media (max-width: 768px) {
  .register-left {
    display: none;
  }

  .register-right {
    padding: 40px 30px;
  }
}
</style>
