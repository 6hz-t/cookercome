<!-- 厨师登录 -->
<template>
  <div class="cooker-login">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <div class="logo">
            <el-icon :size="48">
              <UserFilled />
            </el-icon>
          </div>
          <h1>厨师登录</h1>
          <p>欢迎使用厨师上门服务平台</p>
        </div>

        <el-form :model="user" :rules="rules" ref="ruleForm" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="user.username" placeholder="请输入用户名" :prefix-icon="User" size="large" clearable />
          </el-form-item>

          <el-form-item prop="password">
            <el-input v-model="user.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large"
              show-password @keyup.enter="submitForm('ruleForm')" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" class="login-btn" @click="submitForm('ruleForm')" :loading="loading">
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
        login({
          username: this.user.username,
          // sha256 加密
          password: CryptoJS.SHA256(this.user.password).toString()

        })
      }).then(res => {

        console.log(this.user. username,CryptoJS.SHA256(this.user.password).toString())
        localStorage.setItem('token', res.data.token)
        this.$message.success('登录成功')
        this.$router.push('/cooker')
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
  max-width: 420px;
  padding: 20px;
}

.login-box {
  width: 100%;
  padding: 40px;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 60px;
    height: 60px;
    background: #409eff;
    margin-bottom: 15px;
    color: white;
  }

  h1 {
    font-size: 22px;
    font-weight: 500;
    color: #303133;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 22px;
  }
}

.login-btn {
  width: 100%;
  height: 40px;
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
</style>
