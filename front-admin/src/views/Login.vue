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
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router' // 路由跳转用
import request from '@/utils/request' // 引入封装的axios
import { ElMessage } from 'element-plus'

// 路由实例
const router = useRouter()

// 登录表单数据
const loginForm = reactive({
  phone: '', // 手机号
  password: '' // 密码
})

// 表单验证规则
const loginRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度为6-16位', trigger: 'blur' }
  ]
})

// 表单引用（用于验证）
const loginFormRef = ref(null)

// 登录按钮点击事件
const handleLogin = () => {
  // 先验证表单
  loginFormRef.value.validate((valid) => {
    if (!valid) return

    // 表单验证通过，调用后端登录接口
    request.post('/user/login', loginForm)
      .then((res) => {
        // 登录成功：保存token到本地存储（持久化，刷新页面不丢失）
        localStorage.setItem('admin-token', res.data)
        ElMessage.success('登录成功！')
        // 跳转到仪表盘页面
        router.push('/dashboard')
      })
      .catch((err) => {
        ElMessage.error(err.msg || '登录失败，请检查账号密码')
      })
  })
}
</script>

<style scoped>
/* 登录页面样式，新手可直接用 */
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