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
        <el-button text type="primary" @click="openProfileDialog">编辑 <el-icon><Right /></el-icon></el-button>
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

    <!-- 个人信息编辑对话框 -->
    <el-dialog 
      v-model="profileDialogVisible" 
      title="编辑个人信息" 
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="profile-edit-dialog"
      append-to-body
    >
      <div class="dialog-content">
        <el-form :model="profileForm" label-width="120px" label-position="left">
          <!-- 头像上传区域 -->
          <el-form-item label="头像">
            <div class="avatar-section">
              <AvatarUploader 
                v-model="profileForm.avatar" 
                :size="140"
                :editable="true"
                @success="handleAvatarSuccess"
                @error="handleAvatarError"
              />
              <div class="avatar-tips">
                <p><el-icon><InfoFilled /></el-icon> 支持 JPG、PNG 格式</p>
                <p><el-icon><InfoFilled /></el-icon> 图片大小不超过 5MB</p>
                <p><el-icon><InfoFilled /></el-icon> 建议使用正方形图片</p>
              </div>
            </div>
          </el-form-item>

          <el-divider />

          <!-- 基本信息 -->
          <el-form-item label="用户名">
            <el-input 
              v-model="profileForm.username" 
              placeholder="请输入用户名"
              clearable
              maxlength="20"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="真实姓名">
            <el-input 
              v-model="profileForm.realName" 
              placeholder="请输入真实姓名"
              clearable
              maxlength="20"
            />
          </el-form-item>

          <el-form-item label="性别">
            <el-radio-group v-model="profileForm.gender" class="gender-group">
              <el-radio :label="0" border>未知</el-radio>
              <el-radio :label="1" border>男</el-radio>
              <el-radio :label="2" border>女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="邮箱">
            <el-input 
              v-model="profileForm.email" 
              placeholder="请输入邮箱地址"
              clearable
            />
          </el-form-item>

          <el-form-item label="生日">
            <el-date-picker
              v-model="profileForm.birthday"
              type="date"
              placeholder="选择生日"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              :disabled-date="disabledDate"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeProfileDialog">取消</el-button>
          <el-button type="primary" @click="saveProfile" :loading="saving">
            <el-icon v-if="!saving"><Check /></el-icon>
            <el-icon v-else><Loading /></el-icon>
            {{ saving ? '保存中...' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  Setting, User, Phone, Location, Lock, Bell, Delete, Right,
  InfoFilled, Check, Loading
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import AvatarUploader from '@/components/AvatarUploader.vue'
import { getProfile, updateProfile } from '@/api/settings'

const emit = defineEmits(['edit-profile', 'change-phone', 'show-address', 'change-password', 'delete-account'])

const props = defineProps({
  userPhone: String
})

const phone = ref(props.userPhone || '135****6810')
const notificationsEnabled = ref(true)

// 个人信息编辑
const profileDialogVisible = ref(false)
const saving = ref(false)
const profileForm = ref({
  avatar: '',
  username: '',
  realName: '',
  gender: 0,
  email: '',
  birthday: ''
})

// 打开对话框
const openProfileDialog = () => {
  profileDialogVisible.value = true
  loadProfile()
}

// 关闭对话框
const closeProfileDialog = () => {
  profileDialogVisible.value = false
}

// 加载用户资料
const loadProfile = async () => {
  try {
    const res = await getProfile()
    if (res.data) {
      profileForm.value = {
        avatar: res.data.avatar || '',
        username: res.data.username || '',
        realName: res.data.realName || '',
        gender: res.data.gender ?? 0,
        email: res.data.email || '',
        birthday: res.data.birthday || ''
      }
    }
  } catch (error) {
    console.error('加载用户资料失败:', error)
    ElMessage.error('加载失败：' + (error.message || '请重试'))
  }
}

// 保存个人资料
const saveProfile = async () => {
  // 验证必填字段
  if (!profileForm.value.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  // 验证邮箱格式
  if (profileForm.value.email && !/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(profileForm.value.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }

  try {
    saving.value = true
    await updateProfile(profileForm.value)
    
    ElMessage.success('保存成功')
    closeProfileDialog()
    
    // 通知父组件更新用户信息
    emit('edit-profile', profileForm.value)
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败：' + (error.message || '请重试'))
  } finally {
    saving.value = false
  }
}

// 头像上传成功
const handleAvatarSuccess = (url) => {
  profileForm.value.avatar = url
  console.log('头像上传成功:', url)
}

// 头像上传失败
const handleAvatarError = (error) => {
  console.error('头像上传失败:', error)
}

// 禁用日期（不能选择未来日期）
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}
</script>

<style scoped>
.settings {
  min-height: 600px;
}

/* 面板头部 */
.panel-header {
  margin-bottom: 25px;
}

.panel-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.panel-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
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

/* 个人信息编辑对话框样式 - 科技感增强版 */
.profile-edit-dialog :deep(.el-dialog) {
  border-radius: 24px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(20, 20, 50, 0.98), rgba(30, 30, 70, 0.95));
  backdrop-filter: blur(30px);
  box-shadow: 
    0 0 80px rgba(102, 126, 234, 0.3),
    0 0 120px rgba(118, 75, 162, 0.2),
    inset 0 0 60px rgba(102, 126, 234, 0.08);
  border: 1px solid rgba(102, 126, 234, 0.3);
}

.profile-edit-dialog :deep(.el-dialog__header) {
  padding: 35px 40px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.15));
  border-bottom: 1px solid rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.profile-edit-dialog :deep(.el-dialog__header)::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  height: 3px;
  background: linear-gradient(90deg, transparent, #667eea, #00f3ff, #667eea, transparent);
  animation: glowLine 3s ease-in-out infinite;
}

@keyframes glowLine {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 1; }
}

/* 背景装饰网格 */
.profile-edit-dialog :deep(.el-dialog__header)::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(102, 126, 234, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(102, 126, 234, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
  pointer-events: none;
}

.profile-edit-dialog :deep(.el-dialog__title) {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #667eea, #00f3ff, #bc13fe);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 5s ease infinite;
  position: relative;
  z-index: 1;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% center; }
  50% { background-position: 100% center; }
}

.profile-edit-dialog :deep(.el-dialog__body) {
  padding: 45px 40px;
  max-height: 70vh;
  overflow-y: auto;
  background: transparent;
  position: relative;
}

/* 侧边光效装饰 */
.profile-edit-dialog :deep(.el-dialog__body)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #667eea, #00f3ff, #bc13fe);
  opacity: 0.5;
}

.profile-edit-dialog :deep(.el-dialog__body)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #bc13fe, #00f3ff, #667eea);
  opacity: 0.5;
}

.dialog-content {
  position: relative;
  z-index: 1;
}

.avatar-section {
  display: flex;
  align-items: flex-start;
  gap: 35px;
  padding: 25px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.08));
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  box-shadow: 
    0 8px 32px rgba(102, 126, 234, 0.15),
    inset 0 0 20px rgba(102, 126, 234, 0.05);
}

.avatar-tips {
  flex: 1;
  padding: 20px;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 12px;
  border-left: 3px solid #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.1);
}

.avatar-tips p {
  margin: 10px 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  display: flex;
  align-items: center;
  gap: 8px;
  transition: var(--transition);
}

.avatar-tips p:hover {
  color: #00f3ff;
  transform: translateX(5px);
}

.avatar-tips .el-icon {
  color: #667eea;
  font-size: 16px;
  filter: drop-shadow(0 0 8px rgba(102, 126, 234, 0.6));
}

.gender-group {
  display: flex;
  gap: 15px;
}

.gender-group .el-radio {
  flex: 1;
  max-width: 130px;
}

.profile-edit-dialog :deep(.el-form-item) {
  margin-bottom: 28px;
  position: relative;
}

.profile-edit-dialog :deep(.el-form-item__label) {
  font-weight: 600;
  color: #fff;
  font-size: 15px;
  background: linear-gradient(135deg, #667eea, #00f3ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  padding: 8px 12px;
  border-radius: 8px;
  background-color: rgba(102, 126, 234, 0.1);
}

.profile-edit-dialog :deep(.el-input__wrapper),
.profile-edit-dialog :deep(.el-textarea__wrapper) {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(102, 126, 234, 0.05));
  border: 1px solid rgba(102, 126, 234, 0.3);
  box-shadow: 
    0 4px 20px rgba(102, 126, 234, 0.1),
    inset 0 0 10px rgba(102, 126, 234, 0.02);
  border-radius: 12px;
  padding: 12px 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.profile-edit-dialog :deep(.el-input__wrapper:hover),
.profile-edit-dialog :deep(.el-textarea__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 
    0 0 25px rgba(102, 126, 234, 0.4),
    inset 0 0 15px rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
}

.profile-edit-dialog :deep(.el-input__wrapper.is-focus),
.profile-edit-dialog :deep(.el-textarea__wrapper.is-focus) {
  border-color: #00f3ff;
  box-shadow: 
    0 0 35px rgba(0, 243, 255, 0.5),
    inset 0 0 20px rgba(0, 243, 255, 0.08);
}

.profile-edit-dialog :deep(.el-input__inner),
.profile-edit-dialog :deep(.el-textarea__inner) {
  color: #fff;
  font-size: 14px;
}

.profile-edit-dialog :deep(.el-input__inner::placeholder),
.profile-edit-dialog :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

.profile-edit-dialog :deep(.el-radio-group) {
  display: flex;
  gap: 15px;
  padding: 8px;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.profile-edit-dialog :deep(.el-radio) {
  margin: 0;
  flex: 1;
}

.profile-edit-dialog :deep(.el-radio__input) {
  display: none;
}

.profile-edit-dialog :deep(.el-radio__label) {
  width: 100%;
  text-align: center;
  padding: 10px 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.profile-edit-dialog :deep(.el-radio__label:hover) {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.4);
  color: #fff;
  transform: translateY(-2px);
}

.profile-edit-dialog :deep(.el-radio.is-checked) .el-radio__label {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.4), rgba(118, 75, 162, 0.3));
  border-color: #667eea;
  color: #00f3ff;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.profile-edit-dialog :deep(.el-divider) {
  background-color: rgba(102, 126, 234, 0.3);
  margin: 35px 0;
  position: relative;
}

.profile-edit-dialog :deep(.el-divider::before) {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

.profile-edit-dialog :deep(.el-date-picker) {
  width: 100%;
}

.profile-edit-dialog :deep(.el-date-picker__editor-wrap) {
  input {
    color: #fff;
  }
}

.profile-edit-dialog :deep(.el-dialog__footer) {
  padding: 25px 40px;
  border-top: 1px solid rgba(102, 126, 234, 0.3);
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent);
}

.dialog-footer .el-button {
  min-width: 110px;
  font-size: 15px;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.dialog-footer .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.6);
}

.dialog-footer .el-button:not(.el-button--primary) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(102, 126, 234, 0.3);
  color: #fff;
}

.dialog-footer .el-button:not(.el-button--primary):hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: #667eea;
  transform: translateY(-2px);
}

/* 滚动条美化 */
.profile-edit-dialog :deep(.el-dialog__body::-webkit-scrollbar) {
  width: 8px;
}

.profile-edit-dialog :deep(.el-dialog__body::-webkit-scrollbar-track) {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 4px;
}

.profile-edit-dialog :deep(.el-dialog__body::-webkit-scrollbar-thumb) {
  background: linear-gradient(135deg, #667eea, #00f3ff);
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
}

.profile-edit-dialog :deep(.el-dialog__body::-webkit-scrollbar-thumb:hover) {
  background: linear-gradient(135deg, #00f3ff, #bc13fe);
  box-shadow: 0 0 20px rgba(0, 243, 255, 0.6);
}

/* 遮罩层加强 */
.profile-edit-dialog + .el-overlay {
  background-color: rgba(10, 10, 30, 0.85);
  backdrop-filter: blur(8px);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .profile-edit-dialog :deep(.el-dialog) {
    width: 90% !important;
  }
  
  .avatar-section {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
  
  .profile-edit-dialog :deep(.el-dialog__body) {
    padding: 30px 20px;
  }
}
</style>