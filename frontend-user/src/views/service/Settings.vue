<template>
  <div class="settings">
    <div class="panel-header">
      <h2 class="panel-title">
        <el-icon><Setting /></el-icon>
        账户设置
      </h2>
      <p class="panel-desc">管理您的账户信息和安全设置</p>
    </div>
    
    <div class="settings-list">
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><User /></el-icon>
          <span class="setting-label">个人信息</span>
        </div>
        <el-button text type="primary" @click="$emit('edit-profile')">编辑 <el-icon><Right /></el-icon></el-button>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Phone /></el-icon>
          <span class="setting-label">手机号码</span>
        </div>
        <span class="setting-value">{{ phone }} <el-button text type="primary" @click="$emit('change-phone')">修改</el-button></span>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Location /></el-icon>
          <span class="setting-label">地址管理</span>
        </div>
        <el-button text type="primary" @click="$emit('show-address')">管理 <el-icon><Right /></el-icon></el-button>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Lock /></el-icon>
          <span class="setting-label">账户安全</span>
        </div>
        <el-button text type="primary" @click="$emit('change-password')">修改密码 <el-icon><Right /></el-icon></el-button>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Bell /></el-icon>
          <span class="setting-label">消息通知</span>
        </div>
        <el-switch v-model="notificationsEnabled" />
      </div>
      
      <div class="setting-item danger">
        <div class="setting-left">
          <el-icon class="setting-icon"><Delete /></el-icon>
          <span class="setting-label">注销账户</span>
        </div>
        <el-button text type="danger" @click="$emit('delete-account')">注销 <el-icon><Right /></el-icon></el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Setting, User, Phone, Location, Lock, Bell, Delete, Right } from '@element-plus/icons-vue'

const emit = defineEmits(['edit-profile', 'change-phone', 'show-address', 'change-password', 'delete-account'])

const props = defineProps({
  userPhone: String
})

const phone = ref(props.userPhone || '135****6810')
const notificationsEnabled = ref(true)
</script>

<style scoped>
.settings {
  min-height: 600px;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: var(--bg-color);
  border-radius: 12px;
  transition: var(--transition);
}

.setting-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.setting-item.danger:hover {
  background: rgba(245, 108, 108, 0.05);
}

.setting-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.setting-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-size: 20px;
}

.setting-label {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-primary);
}

.setting-value {
  font-size: 14px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
