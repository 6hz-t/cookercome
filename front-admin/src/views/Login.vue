<template>
  <div class="login-container">
    <el-card class="login-card">
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
      </el-form>
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

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  phone: '',
  password: ''
})

// 表单验证规则
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

// 表单引用
const loginFormRef = ref(null)

// 登录方法
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (!valid) return

    loading.value = true

    // 调用后端登录接口（使用 request 工具）
    request.post('/api/auth/login', {
      phone: loginForm.phone,
      password: loginForm.password
    }).then(res => {
      // res 已经是后端返回的 data 字段（AuthResponse）
      if (res && res.accessToken) {
        // 保存 accessToken 到 localStorage
        localStorage.setItem('admin-token', res.accessToken)
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
      // err 可能是被 reject 的响应对象
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
</style>
