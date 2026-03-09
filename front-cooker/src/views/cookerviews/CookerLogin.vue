<!-- 厨师登录 -->
<template>
  <div class="cooker-login">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <div class="logo">
            <el-icon :size="48"><UserFilled /></el-icon>
          </div>
          <h1>厨师登录</h1>
          <p>欢迎使用厨师上门服务平台</p>
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
    </div>
  </div>
</template>

<script>
import { UserFilled, User, Lock } from '@element-plus/icons-vue'

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
          this.loading = true;
          // TODO: 调用登录接口
          setTimeout(() => {
            this.loading = false;
            this.$message.success('登录成功！');
            this.$router.push('/cooker');
          }, 1000);
        } else {
          this.$message.error('请填写完整的登录信息');
          return false;
        }
      });
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
  background: linear-gradient(135deg, hsl(69, 77%, 88%) 0%, #c0a4dd 100%);
  position: relative;
  overflow: hidden;

  // 背景装饰圆圈
  &::before {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    top: -100px;
    right: -100px;
    animation: float 6s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    bottom: -50px;
    left: -50px;
    animation: float 8s ease-in-out infinite reverse;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.login-container {
  position: relative;
  z-index: 1;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #66b5ea 0%, #1792da 100%);
    border-radius: 50%;
    margin-bottom: 15px;
    color: white;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  }

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: #999;
    margin: 0;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
    }

    &.is-focus {
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
    }
  }
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #66a4dd 0%, #17b2f0 100%);
  border: none;
  border-radius: 10px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 16px;

  span {
    margin-right: 8px;
  }
}
</style>
