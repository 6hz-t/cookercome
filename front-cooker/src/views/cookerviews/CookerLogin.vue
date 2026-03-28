<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <div class="card-head">
        <h1>厨师端登录</h1>
        <p>请输入手机号和密码</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" class="auth-form" @keyup.enter="submit">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="w-full" :loading="loading" @click="submit">登录</el-button>
        </el-form-item>
      </el-form>

      <div class="footer-link">
        没有账号？
        <el-link type="primary" :underline="false" @click="$router.push('/cooker/register')">去注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import { login } from '@/api/cooker'

export default {
  name: 'CookerLogin',
  data() {
    return {
      loading: false,
      form: {
        phone: '',
        password: ''
      },
      rules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少 6 位', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async submit() {
      if (this.loading) return
      const valid = await this.$refs.formRef.validate().catch(() => false)
      if (!valid) return

      this.loading = true
      try {
        const res = await login(this.form)
        if (res.code !== 200 || !res.data) {
          this.$message.error(res.message || '登录失败')
          return
        }

        const token = res.data.accessToken
        const userId = res.data.userInfo?.id
        if (!token || !userId) {
          this.$message.error('登录返回数据不完整')
          return
        }

        localStorage.setItem('accesstoken', token)
        localStorage.setItem('userId', String(userId))
        localStorage.setItem('userRole', String(res.data.userInfo?.role ?? ''))

        this.$message.success('登录成功')
        this.$router.push('/cooker/desk')
      } catch (error) {
        this.$message.error(error?.response?.data?.message || error?.message || '登录失败')
      } finally {
        this.loading = false
      }
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
