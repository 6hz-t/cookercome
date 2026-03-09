<!-- 厨师登录 -->
<template>
  <div class="cooker-login">
    <div class="login-container">
      <el-row class="login-box">
        <!-- 左侧宣传区域 -->
        <el-col :xs="0" :sm="12" :md="14" class="login-left">
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

        <!-- 右侧登录表单 -->
        <el-col :xs="24" :sm="12" :md="10" class="login-right">
          <div class="right-content">
            <div class="form-header">
              <h2>欢迎回来</h2>
              <p>请登录您的厨师账号</p>
            </div>

            <el-form :model="user" :rules="rules" ref="ruleForm" class="login-form">
              <el-form-item prop="username">
                <el-input 
                  v-model="user.username" 
                  placeholder="请输入用户名" 
                  :prefix-icon="User" 
                  size="large" 
                  clearable 
                />
              </el-form-item>

              <el-form-item prop="password">
                <el-input 
                  v-model="user.password" 
                  type="password" 
                  placeholder="请输入密码" 
                  :prefix-icon="Lock" 
                  size="large"
                  show-password 
                  @keyup.enter="submitForm('ruleForm')" 
                />
              </el-form-item>

              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  class="login-btn" 
                  @click="submitForm('ruleForm')" 
                  :loading="loading"
                >
                  登 录
                </el-button>
              </el-form-item>

              <div class="form-footer">
                <span>还没有账号？</span>
                <el-link type="primary" @click="goRegister">立即注册</el-link>
              </div>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { UserFilled, User, Lock, KnifeFork, Timer, Star, Location } from '@element-plus/icons-vue'
import { login } from '../../api/cooker'
import CryptoJS from 'crypto-js'

export default {
  name: 'CookerLogin',
  components: {
    UserFilled
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
    }

    return {
      User,
      Lock,
      loading: false,
      user: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { validator: validatePassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // SHA-256 加密
          const encryptedPassword = CryptoJS.SHA256(this.user.password).toString()
          
          login({
            username: this.user.username,
            password: encryptedPassword
          }).then(res => {
            console.log(res)
            
            // localStorage.setItem('token', res.data.token)
            if (res.data.code == 200) {
              localStorage.setItem('token', res.data.token)
              this.$message.success('登录成功')
              this.$router.push('/cooker/todo')
            }else {
              this.$message.error(res.data.code)
            }

          })
        }
      })
    },

    goRegister() {
      this.$router.push('/cooker/register');
    }
  }

}
</script>

<style lang="scss" scoped>
.cooker-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.login-container {
  width: 100%;
  max-width: 900px;
  padding: 20px;
}

.login-box {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.login-left {
  background-color : #409eff;
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

.login-right {
  padding: 50px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;

  .right-content {
    width: 100%;
    max-width: 320px;

    .form-header {
      text-align: center;
      margin-bottom: 30px;

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

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 22px;
  }
}

.login-btn {
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
  .login-left {
    display: none;
  }

  .login-right {
    padding: 40px 30px;
  }
}
</style>
