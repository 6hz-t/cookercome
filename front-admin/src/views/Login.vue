<template>
  <div class="login-container">
    <el-card class="login-card">
      <!-- 登录表单 -->
      <div v-if="showLogin">
        <h2 class="login-title">管理员登录</h2>
        <el-form
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          label-width="80px"
          class="login-form"
        >
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入管理员手机号"
              maxlength="11"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item class="login-btn-group">
            <el-button type="primary" @click="handleLogin" class="login-btn" :loading="loading">登录</el-button>
          </el-form-item>
          <div class="switch-form">
            还没有账号？
            <el-link type="primary" @click="showLogin = false">立即注册</el-link>
          </div>
        </el-form>
      </div>

      <!-- 注册表单 -->
      <div v-else>
        <h2 class="login-title">管理员注册</h2>
        <el-form
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          label-width="80px"
          class="login-form"
        >
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              maxlength="11"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input
              v-model="registerForm.realName"
              placeholder="请输入姓名"
            ></el-input>
          </el-form-item>
          <el-form-item class="login-btn-group">
            <el-button type="primary" @click="handleRegister" class="login-btn" :loading="loading">注册</el-button>
          </el-form-item>
          <div class="switch-form">
            已有账号？
            <el-link type="primary" @click="showLogin = true">立即登录</el-link>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 路由实例
const router = useRouter()

// 显示登录还是注册表单
const showLogin = ref(true)

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  phone: '',
  password: ''
})

// 注册表单数据
const registerForm = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  realName: ''
})

// 表单引用
const loginFormRef = ref(null)
const registerFormRef = ref(null)

// 登录表单验证规则
const loginRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度为 6-16 位', trigger: 'blur' }
  ]
})

// 注册表单验证规则
const registerRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度为 6-16 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为 2-20 位', trigger: 'blur' }
  ]
})

// 登录方法
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (!valid) return

    loading.value = true

    // 调用后端登录接口
    request.post('/api/auth/login', {
      phone: loginForm.phone,
      password: loginForm.password
    }).then(res => {
      if (res && res.accessToken) {
        // 保存 token 到 localStorage
        localStorage.setItem('admin-token', res.accessToken)
        if (res.refreshToken) {
          localStorage.setItem('admin-refreshToken', res.refreshToken)
        }
        if (res.userInfo) {
          localStorage.setItem('admin-userInfo', JSON.stringify(res.userInfo))
        }
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error('登录失败')
      }
    }).catch(err => {
      console.error('登录错误:', err)
      if (err && err.msg) {
        ElMessage.error(err.msg)
      } else {
        ElMessage.error('登录失败，请检查账号密码或后端服务')
      }
    }).finally(() => {
      loading.value = false
    })
  })
}

// 注册方法
const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (!valid) return

    loading.value = true

    // 调用后端注册接口
    request.post('/api/auth/register', {
      phone: registerForm.phone,
      password: registerForm.password,
      role: 2,  // 2 表示管理员角色
      realName: registerForm.realName
    }).then(res => {
      console.log('注册响应:', res)
      if (res && res.code === 200) {
        ElMessage.success('注册成功，请登录')
        // 切换到登录表单
        showLogin.value = true
        // 清空注册表单
        registerForm.phone = ''
        registerForm.password = ''
        registerForm.confirmPassword = ''
        registerForm.realName = ''
      } else {
        ElMessage.error(res.msg || '注册失败')
      }
    }).catch(err => {
      console.error('注册错误:', err)
      console.error('错误详情:', err.response?.data)
      if (err && err.msg) {
        ElMessage.error(err.msg)
      } else if (err.response && err.response.data && err.response.data.msg) {
        ElMessage.error(err.response.data.msg)
      } else {
        ElMessage.error('注册失败，请稍后重试')
      }
    }).finally(() => {
      loading.value = false
    })
  })
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 400px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.login-form {
  margin-top: 20px;
}

.login-btn-group {
  text-align: center;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.switch-form {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #666;
}

.switch-form .el-link {
  margin-left: 5px;
}
</style>
