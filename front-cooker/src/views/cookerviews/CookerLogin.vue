<!-- 厨师登录 -->
<template>
  <div class="cooker-login">
    <div class="login-background">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
    
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="logo-wrapper">
            <el-icon :size="48" color="#fff"><Cooking /></el-icon>
          </div>
          <h2>厨师登录</h2>
          <p>欢迎使用厨师上门服务平台</p>
        </div>
      </template>

      <el-form :model="user" :rules="rules" ref="ruleForm" label-width="0">
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
          <el-checkbox v-model="rememberMe" size="large">记住我</el-checkbox>
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
    </el-card>
  </div>
</template>

<script>
import { UserFilled, User, Lock, Food } from '@element-plus/icons-vue'

export default {
  name: 'CookerLogin',
  components: {
    UserFilled,
    Food
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
      rememberMe: false,
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
            this.$router.push('/cooker/todo');
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
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-accent) 100%);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;

  .bg-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.15);
    animation: float 6s ease-in-out infinite;
  }

  .circle-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  .circle-2 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    left: -50px;
    animation-delay: 2s;
  }

  .circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    left: 10%;
    animation-delay: 4s;
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

.login-card {
  width: 440px;
  position: relative;
  z-index: 1;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(74, 68, 62, 0.12);

  :deep(.el-card__header) {
    background: transparent;
    border: none;
    padding: 0;
  }

  .card-header {
    text-align: center;
    padding: 20px 0 30px;

    .logo-wrapper {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
      border-radius: 50%;
      margin-bottom: 16px;
      box-shadow: 0 4px 15px rgba(217, 127, 74, 0.3);
    }

    h2 {
      font-size: 22px;
      font-weight: 600;
      color: var(--color-text-primary);
      margin: 0 0 8px;
    }

    p {
      font-size: 14px;
      color: var(--color-text-secondary);
      margin: 0;
    }
  }
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 12px 16px;
  box-shadow: 0 0 0 1px var(--color-border) inset;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 0 0 1px var(--color-border-dark) inset;
  }

  &.is-focus {
    box-shadow: 0 0 0 1px var(--color-accent) inset;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-accent) 100%);
  border: none;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(217, 127, 74, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-top: 20px;

  span {
    margin-right: 8px;
  }
}
</style>
