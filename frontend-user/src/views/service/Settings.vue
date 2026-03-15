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
        <span class="setting-value">{{ phone }} 
          <el-button text type="primary" @click="openPhoneDialog">
            改绑 <el-icon><Right /></el-icon>
          </el-button>
        </span>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Location /></el-icon>
          <span class="setting-label">地址管理</span>
        </div>
        <el-button text type="primary" @click="openAddressDialog">管理 <el-icon><Right /></el-icon></el-button>
      </div>
      
      <div class="setting-item">
        <div class="setting-left">
          <el-icon class="setting-icon"><Lock /></el-icon>
          <span class="setting-label">账户安全</span>
        </div>
        <el-button text type="primary" @click="openPasswordDialog">
          修改密码 <el-icon><Right /></el-icon>
        </el-button>
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
      top="5vh"
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

    <!-- 手机号修改对话框 -->
    <el-dialog 
      v-model="phoneDialogVisible" 
      title="修改手机号码"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="phone-bind-dialog"
      append-to-body
      top="15vh"
    >
      <div class="dialog-content">
        <el-form :model="phoneForm" label-width="120px" label-position="left" ref="phoneFormRef">
          <el-form-item label="当前手机号">
            <el-input 
              v-model="currentPhone" 
              disabled
              placeholder="当前手机号"
            />
          </el-form-item>

          <el-divider />

          <el-form-item 
            label="新手机号" 
            prop="newPhone"
            :rules="[
              { required: true, message: '请输入新手机号', trigger: 'blur' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
            ]"
          >
            <el-input 
              v-model="phoneForm.newPhone" 
              placeholder="请输入新手机号"
              clearable
              maxlength="11"
            />
          </el-form-item>

          <el-form-item 
            label="当前密码" 
            prop="currentPassword"
            :rules="[{ required: true, message: '请输入当前密码', trigger: 'blur' }]"
          >
            <el-input 
              v-model="phoneForm.currentPassword" 
              type="password"
              placeholder="请输入当前密码验证身份"
              show-password
              clearable
            />
            <div class="form-tips">
              <el-icon><Lock /></el-icon>
              <span>为了账户安全，修改手机号需要验证当前密码</span>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePhoneDialog">取消</el-button>
          <el-button type="primary" @click="savePhone" :loading="phoneSaving">
            {{ phoneSaving ? '修改中...' : '确认修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="passwordDialogVisible" 
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="password-change-dialog"
      append-to-body
      top="20vh"
    >
      <div class="dialog-content">
        <el-form :model="passwordForm" label-width="120px" label-position="left" ref="passwordFormRef">
          <el-form-item 
            label="原密码" 
            prop="oldPassword"
            :rules="[{ required: true, message: '请输入原密码', trigger: 'blur' }]"
          >
            <el-input 
              v-model="passwordForm.oldPassword" 
              type="password"
              placeholder="请输入原密码"
              show-password
              clearable
            />
          </el-form-item>

          <el-divider />

          <el-form-item 
            label="新密码" 
            prop="newPassword"
            :rules="[
              { required: true, message: '请输入新密码', trigger: 'blur' },
              { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
            ]"
          >
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password"
              placeholder="请输入新密码（至少 6 位）"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item 
            label="确认新密码" 
            prop="confirmPassword"
            :rules="[
              { required: true, message: '请再次输入新密码', trigger: 'blur' },
              { validator: validateConfirmPassword, trigger: 'blur' }
            ]"
          >
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password"
              placeholder="请再次输入新密码"
              show-password
              clearable
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePasswordDialog">取消</el-button>
          <el-button type="primary" @click="savePassword" :loading="passwordSaving">
            {{ passwordSaving ? '修改中...' : '确认修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 地址管理对话框 -->
    <el-dialog 
      v-model="addressDialogVisible" 
      title="地址管理"
      width="900px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="address-manage-dialog"
      append-to-body
      top="5vh"
    >
      <div class="dialog-content">
        <div class="address-toolbar">
          <el-button 
            type="primary" 
            @click="openAddAddressDialog"
            :icon="Plus"
            class="tech-btn-add"
          >
            <span class="btn-text">新增地址</span>
            <span class="btn-icon-wrapper">
              <span class="btn-icon-bg"></span>
              <el-icon class="btn-icon"><Plus /></el-icon>
            </span>
          </el-button>
          <el-button 
            @click="loadAddressList"
            :icon="Refresh"
            class="tech-btn-refresh"
            :loading="addressLoading"
          >
            <span class="btn-text">刷新列表</span>
            <span class="btn-icon-wrapper">
              <span class="btn-icon-bg"></span>
              <el-icon class="btn-icon"><Refresh /></el-icon>
            </span>
          </el-button>
        </div>

        <el-divider />

        <div class="address-list" v-loading="addressLoading">
          <div v-if="addressList.length === 0" class="empty-state">
            <el-empty description="暂无地址，请点击“新增地址”添加" />
          </div>
          
          <div v-else class="address-cards">
            <div 
              v-for="address in addressList" 
              :key="address.id" 
              class="address-card"
              :class="{ 'is-default': address.isDefault === 1 }"
            >
              <div class="address-header">
                <div class="address-info">
                  <span class="receiver-name">{{ address.receiver }}</span>
                  <span class="receiver-phone">{{ address.phone }}</span>
                </div>
                <el-tag v-if="address.isDefault === 1" size="small" type="warning">默认</el-tag>
              </div>
              
              <div class="address-detail">
                <el-icon><Location /></el-icon>
                <span>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</span>
              </div>
              
              <div class="address-actions">
                <el-button 
                  v-if="address.isDefault !== 1" 
                  text 
                  type="primary" 
                  size="small"
                  @click="setAsDefault(address.id)"
                >
                  设为默认
                </el-button>
                <el-button 
                  text 
                  type="primary" 
                  size="small"
                  @click="editAddress(address)"
                >
                  编辑
                </el-button>
                <el-button 
                  text 
                  type="danger" 
                  size="small"
                  @click="deleteAddressItem(address.id)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddressDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog 
      v-model="addressFormDialogVisible" 
      :title="isEditing ? '编辑地址' : '新增地址'"
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="address-form-dialog"
      append-to-body
      top="10vh"
    >
      <div class="dialog-content">
        <el-form 
          :model="addressForm" 
          label-width="100px" 
          label-position="left"
          ref="addressFormRef"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item 
                label="收件人" 
                prop="receiver"
                :rules="[{ required: true, message: '请输入收件人姓名', trigger: 'blur' }]"
              >
                <el-input 
                  v-model="addressForm.receiver" 
                  placeholder="请输入收件人姓名"
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item 
                label="联系电话" 
                prop="phone"
                :rules="[
                  { required: true, message: '请输入联系电话', trigger: 'blur' },
                  { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
                ]"
              >
                <el-input 
                  v-model="addressForm.phone" 
                  placeholder="请输入手机号"
                  clearable
                  maxlength="11"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item 
                label="省份" 
                prop="province"
                :rules="[{ required: true, message: '请选择省份', trigger: 'change' }]"
              >
                <el-input 
                  v-model="addressForm.province" 
                  placeholder="请输入省份"
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item 
                label="城市" 
                prop="city"
                :rules="[{ required: true, message: '请选择城市', trigger: 'change' }]"
              >
                <el-input 
                  v-model="addressForm.city" 
                  placeholder="请输入城市"
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item 
                label="区县" 
                prop="district"
                :rules="[{ required: true, message: '请选择区县', trigger: 'change' }]"
              >
                <el-input 
                  v-model="addressForm.district" 
                  placeholder="请输入区县"
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item 
            label="详细地址" 
            prop="detailAddress"
            :rules="[{ required: true, message: '请输入详细地址', trigger: 'blur' }]"
          >
            <el-input 
              v-model="addressForm.detailAddress" 
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址（街道、门牌号等）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="坐标信息">
            <div class="location-section">
              <el-input 
                v-model="addressForm.latitude" 
                placeholder="纬度"
                style="width: 48%; margin-right: 4%;"
                readonly
              />
              <el-input 
                v-model="addressForm.longitude" 
                placeholder="经度"
                readonly
              />
              <el-button 
                type="primary" 
                @click="getLocation"
                style="margin-left: 10px;"
                :loading="locating"
              >
                <el-icon><Location /></el-icon>
                {{ locating ? '定位中...' : '点击定位' }}
              </el-button>
            </div>
            <p class="form-tips">
              <el-icon><InfoFilled /></el-icon>
              点击“点击定位”可自动获取当前位置坐标，或手动输入
            </p>
          </el-form-item>

          <el-form-item label="设为默认">
            <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
            <span class="form-tips-inline">
              <el-icon><InfoFilled /></el-icon>
              设置为默认地址后，将作为下单时的首选配送地址
            </span>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddressFormDialog">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveAddress" 
            :loading="addressSaving"
            class="tech-btn-submit"
          >
            <span class="btn-text">{{ addressSaving ? '保存中...' : '确定' }}</span>
            <span class="btn-icon-wrapper">
              <span class="btn-icon-bg"></span>
              <el-icon class="btn-icon"><Check /></el-icon>
            </span>
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
  InfoFilled, Check, Loading, Plus, Refresh
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AvatarUploader from '@/components/AvatarUploader.vue'
import { getProfile, updateProfile, bindPhone, changePassword } from '@/api/settings'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'
import { getFullAvatarUrl } from '@/utils/avatar'

const emit = defineEmits(['edit-profile', 'change-phone', 'show-address', 'change-password', 'delete-account'])

const props = defineProps({
  userPhone: String
})

const phone = ref(props.userPhone || '135****6810')
const notificationsEnabled = ref(true)

// 手机号修改
const phoneDialogVisible = ref(false)
const phoneSaving = ref(false)
const currentPhone = ref('')
const phoneForm = ref({
  newPhone: '',
  currentPassword: ''
})
const phoneFormRef = ref(null)

// 密码修改
const passwordDialogVisible = ref(false)
const passwordSaving = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordFormRef = ref(null)

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
const handleAvatarSuccess = async (fullUrl, relativePath) => {
  // fullUrl 是完整 URL，用于前端显示
  // relativePath 是相对路径，用于保存到数据库
  profileForm.value.avatar = fullUrl
  console.log('头像上传成功，完整 URL:', fullUrl, '相对路径:', relativePath)
  
  // 立即保存到数据库（使用完整 URL，后端会自动提取相对路径）
  try {
    saving.value = true
    await updateProfile(profileForm.value)
    ElMessage.success('头像已保存')
  } catch (error) {
    console.error('保存头像失败:', error)
    ElMessage.error('保存头像失败：' + (error.message || '请重试'))
  } finally {
    saving.value = false
  }
}

// 头像上传失败
const handleAvatarError = (error) => {
  console.error('头像上传失败:', error)
}

// 禁用日期（不能选择未来日期）
const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

// 验证两次密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 打开手机号修改对话框
const openPhoneDialog = () => {
  currentPhone.value = phone.value
  phoneForm.value = {
    newPhone: '',
    currentPassword: ''
  }
  phoneDialogVisible.value = true
}

// 关闭手机号修改对话框
const closePhoneDialog = () => {
  phoneDialogVisible.value = false
  if (phoneFormRef.value) {
    phoneFormRef.value.clearValidate()
  }
}

// 保存手机号
const savePhone = async () => {
  if (!phoneFormRef.value) return
  
  // 先清除之前的验证错误
  phoneFormRef.value.clearValidate()
  
  try {
    await phoneFormRef.value.validate()
  } catch (error) {
    // 表单验证失败，不显示错误提示
    return
  }
  
  // 验证新手机号是否与当前相同
  if (phoneForm.value.newPhone === currentPhone.value) {
    ElMessage.warning('新手机号与当前手机号相同')
    return
  }
  
  try {
    phoneSaving.value = true
    await bindPhone(phoneForm.value)
    
    ElMessage.success('手机号修改成功')
    closePhoneDialog()
    
    // 更新显示的手机号
    phone.value = phoneForm.value.newPhone
    
    // 通知父组件更新用户信息
    emit('change-phone', phoneForm.value.newPhone)
  } catch (error) {
    console.error('修改手机号失败:', error)
    // 错误信息由 request 拦截器统一处理，这里不需要再显示
  } finally {
    phoneSaving.value = false
  }
}

// 打开密码修改对话框
const openPasswordDialog = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordDialogVisible.value = true
}

// 关闭密码修改对话框
const closePasswordDialog = () => {
  passwordDialogVisible.value = false
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

// 保存密码
const savePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
  } catch (error) {
    return
  }
  
  try {
    passwordSaving.value = true
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    ElMessage.success('密码修改成功')
    closePasswordDialog()
  } catch (error) {
    console.error('修改密码失败:', error)
    // 错误信息由 request 拦截器统一处理
  } finally {
    passwordSaving.value = false
  }
}

// ========== 地址管理相关 ==========

// 地址列表
const addressDialogVisible = ref(false)
const addressLoading = ref(false)
const addressList = ref([])

// 地址表单
const addressFormDialogVisible = ref(false)
const addressSaving = ref(false)
const isEditing = ref(false)
const locating = ref(false)
const addressFormRef = ref(null)
const addressForm = ref({
  id: null,
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  latitude: null,
  longitude: null,
  isDefault: 0
})

// 打开地址管理对话框
const openAddressDialog = async () => {
  addressDialogVisible.value = true
  await loadAddressList()
}

// 关闭地址管理对话框
const closeAddressDialog = () => {
  addressDialogVisible.value = false
}

// 加载地址列表
const loadAddressList = async () => {
  try {
    addressLoading.value = true
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('加载地址列表失败:', error)
    ElMessage.error('加载失败：' + (error.message || '请重试'))
  } finally {
    addressLoading.value = false
  }
}

// 打开添加地址对话框
const openAddAddressDialog = async () => {
  isEditing.value = false
  
  // 先加载用户信息，获取用户名和手机号
  let defaultReceiver = ''
  let defaultPhone = ''
  
  try {
    const res = await getProfile()
    if (res.data) {
      // 使用用户名作为收件人默认值
      defaultReceiver = res.data.username || ''
      // 使用后端返回的真实手机号（完整无脱敏）
      defaultPhone = res.data.phone || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 如果加载失败，使用空值
  }
  
  addressForm.value = {
    id: null,
    receiver: defaultReceiver,  // 自动填充用户名
    phone: defaultPhone,        // 自动填充完整手机号
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    latitude: null,
    longitude: null,
    isDefault: 0
  }
  addressFormDialogVisible.value = true
}

// 编辑地址
const editAddress = (address) => {
  isEditing.value = true
  addressForm.value = {
    id: address.id,
    receiver: address.receiver,
    phone: address.phone,
    province: address.province,
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    latitude: address.latitude,
    longitude: address.longitude,
    isDefault: address.isDefault
  }
  addressFormDialogVisible.value = true
}

// 关闭地址表单对话框
const closeAddressFormDialog = () => {
  addressFormDialogVisible.value = false
  if (addressFormRef.value) {
    addressFormRef.value.clearValidate()
  }
}

// 获取当前位置
const getLocation = async () => {
  if (!navigator.geolocation) {
    ElMessage.warning('您的浏览器不支持地理位置功能')
    return
  }
  
  locating.value = true
  
  navigator.geolocation.getCurrentPosition(
    async (position) => {
      try {
        const lat = position.coords.latitude.toFixed(6)
        const lng = position.coords.longitude.toFixed(6)
        
        const response = await fetch(
          `https://photon.komoot.io/reverse?lat=${lat}&lon=${lng}`
        )
        
        if (!response.ok) {
          throw new Error('Photon API 请求失败')
        }
        
        const data = await response.json()
        
        // 解析地址信息
        if (data.features && data.features.length > 0) {
          const feature = data.features[0]
          const props = feature.properties
          
          // 填充表单字段
          addressForm.value.latitude = lat
          addressForm.value.longitude = lng
          addressForm.value.province = props.state || props.region || ''
          addressForm.value.city = props.city || props.town || ''
          addressForm.value.district = props.district || props.county || ''
          
          // 组合详细地址：街道 + 门牌号等
          let detailAddr = ''
          if (props.street) detailAddr += props.street
          if (props.housenumber) detailAddr += ' ' + props.housenumber
          if (props.name) detailAddr += ' (' + props.name + ')'
          
          addressForm.value.detailAddress = detailAddr.trim() || ''
          
          ElMessage.success(`定位成功，已自动填充地址：${addressForm.value.province}${addressForm.value.city}${addressForm.value.district}`)
        } else {
          // 没有地址信息，只填充经纬度
          addressForm.value.latitude = lat
          addressForm.value.longitude = lng
          ElMessage.warning('定位成功，但无法获取详细地址，请手动输入')
        }
      } catch (error) {
        console.error('逆地理编码失败:', error)
        ElMessage.warning('获取地址失败，请手动输入地址')
      } finally {
        locating.value = false
      }
    },
    (error) => {
      locating.value = false
      console.error('定位失败:', error)
      
      let errorMessage = '定位失败'
      switch(error.code) {
        case error.PERMISSION_DENIED:
          errorMessage = '用户拒绝了定位请求，请手动输入地址'
          break
        case error.POSITION_UNAVAILABLE:
          errorMessage = '位置信息不可用，请手动输入地址'
          break
        case error.TIMEOUT:
          errorMessage = '定位超时，请手动输入地址'
          break
      }
      ElMessage.warning(errorMessage)
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 300000
    }
  )
}

// 保存地址
const saveAddress = async () => {
  if (!addressFormRef.value) return
  
  try {
    await addressFormRef.value.validate()
  } catch (error) {
    return
  }
  
  try {
    addressSaving.value = true
    
    if (isEditing.value) {
      await updateAddress(addressForm.value)
      ElMessage.success('地址更新成功')
    } else {
      await addAddress(addressForm.value)
      ElMessage.success('地址添加成功')
    }
    
    closeAddressFormDialog()
    await loadAddressList()
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('保存失败：' + (error.message || '请重试'))
  } finally {
    addressSaving.value = false
  }
}

// 设为默认地址
const setAsDefault = async (addressId) => {
  try {
    await ElMessageBox.confirm(
      '<div class="confirm-star-content">' +
        '<div class="star-bg"></div>' +
        '<div class="star-icon-wrapper">⭐</div>' +
        '<h3 class="star-title">设为默认地址</h3>' +
        '<p class="star-desc">该地址将成为下单时的首选配送地址</p>' +
        '<div class="star-tips">' +
          '<span class="tips-icon">ℹ️</span>' +
          '<span>每个用户只能有一个默认地址</span>' +
        '</div>' +
      '</div>',
      '',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定设置',
        cancelButtonText: '取消',
        customClass: 'tech-star-confirm',
        showClose: true,
        closeOnClickModal: false
      }
    )
    
    await setDefaultAddress(addressId)
    ElMessage.success('默认地址设置成功')
    await loadAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设置默认地址失败:', error)
      ElMessage.error('设置失败：' + (error.message || '请重试'))
    }
  }
}

// 删除地址
const deleteAddressItem = async (addressId) => {
  try {
    await ElMessageBox.confirm(
      '<div class="confirm-delete-content">' +
        '<div class="delete-shield">🛡️</div>' +
        '<h3 class="delete-title">删除地址</h3>' +
        '<p class="delete-desc">此操作将永久删除该地址</p>' +
        '<div class="delete-warning">' +
          '<span class="warn-icon">⚠️</span>' +
          '<span>删除后无法恢复，请谨慎操作</span>' +
        '</div>' +
      '</div>',
      '',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'tech-delete-confirm',
        showClose: true,
        closeOnClickModal: false
      }
    )
    
    await deleteAddress(addressId)
    ElMessage.success('地址删除成功')
    await loadAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败：' + (error.message || '请重试'))
    }
  }
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
</style>

<!-- 个人信息编辑对话框样式 - 科技感增强版（非 scoped，因为 Dialog 使用 Teleport） -->
<style>
/* 强制覆盖 Element Plus 默认样式 */
.profile-edit-dialog{
  border-radius: 24px !important;
  overflow: hidden !important;
  background: #16213e !important;
  backdrop-filter: blur(30px);
  box-shadow:
    0 0 80px rgba(102, 126, 234, 0.3),
    0 0 120px rgba(118, 75, 162, 0.2),
    inset 0 0 60px rgba(102, 126, 234, 0.08) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
}

.profile-edit-dialog .el-dialog__header {
  padding: 25px 35px;
  background: rgba(102, 126, 234, 0.1);
  border-bottom: 1px solid rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.profile-edit-dialog .el-dialog__header::before {
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
.profile-edit-dialog .el-dialog__header::after {
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

.profile-edit-dialog .el-dialog__title {
  font-size: 20px;
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
  
.profile-edit-dialog .el-dialog__body {
  padding: 20px 35px 25px 35px;
  max-height: none;
  overflow-y: visible;
  overflow-x: hidden;
  background: transparent;
  position: relative;
}

/* 侧边光效装饰 */
.profile-edit-dialog .el-dialog__body::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #667eea, #00f3ff, #bc13fe);
  opacity: 0.5;
}

.profile-edit-dialog .el-dialog__body::after {
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
  gap: 25px;
  padding: 18px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  box-shadow: 
    0 6px 24px rgba(102, 126, 234, 0.08),
    inset 0 0 15px rgba(102, 126, 234, 0.02);
}

.avatar-tips {
  flex: 1;
  padding: 12px;
  background: rgba(102, 126, 234, 0.04);
  border-radius: 10px;
  border-left: 2px solid rgba(102, 126, 234, 0.5);
  box-shadow: 0 3px 15px rgba(102, 126, 234, 0.05);
}

.avatar-tips p {
  margin: 6px 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  gap: 6px;
  transition: var(--transition);
}

.avatar-tips p:hover {
  color: #00f3ff;
  transform: translateX(5px);
}

.avatar-tips .el-icon {
  color: #667eea;
  font-size: 14px;
  filter: drop-shadow(0 0 6px rgba(102, 126, 234, 0.5));
}

.gender-group {
  display: flex;
  gap: 15px;
}

.gender-group .el-radio {
  flex: 1;
  max-width: 130px;
}

.profile-edit-dialog .el-form-item {
  margin-bottom: 14px;
  position: relative;
}

.profile-edit-dialog .el-form-item__label {
  font-weight: 600;
  color: #fff;
  font-size: 13px;
  background: linear-gradient(135deg, #667eea, #00f3ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  padding: 6px 10px;
  border-radius: 6px;
  background-color: rgba(102, 126, 234, 0.05);
}

.profile-edit-dialog .el-input__wrapper,
.profile-edit-dialog .el-textarea__wrapper {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(102, 126, 234, 0.2);
  box-shadow: 
    0 3px 15px rgba(102, 126, 234, 0.05),
    inset 0 0 8px rgba(102, 126, 234, 0.01);
  border-radius: 10px;
  padding: 10px 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.profile-edit-dialog .el-input__wrapper:hover,
.profile-edit-dialog .el-textarea__wrapper:hover {
  border-color: #667eea;
  box-shadow: 
    0 0 25px rgba(102, 126, 234, 0.4),
    inset 0 0 15px rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
}

.profile-edit-dialog .el-input__wrapper.is-focus,
.profile-edit-dialog .el-textarea__wrapper.is-focus {
  border-color: #00f3ff;
  box-shadow: 
    0 0 35px rgba(0, 243, 255, 0.5),
    inset 0 0 20px rgba(0, 243, 255, 0.08);
}

.profile-edit-dialog .el-input__inner,
.profile-edit-dialog .el-textarea__inner {
  color: #fff;
  font-size: 14px;
}

.profile-edit-dialog .el-input__inner::placeholder,
.profile-edit-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.profile-edit-dialog .el-radio-group {
  display: flex;
  gap: 15px;
  padding: 6px;
  background: rgba(102, 126, 234, 0.04);
  border-radius: 10px;
  border: 1px solid rgba(102, 126, 234, 0.15);
}

.profile-edit-dialog .el-radio {
  margin: 0;
  flex: 1;
}

.profile-edit-dialog .el-radio__input {
  display: none;
}

.profile-edit-dialog .el-radio__label {
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

.profile-edit-dialog .el-radio__label:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.4);
  color: #fff;
  transform: translateY(-2px);
}

.profile-edit-dialog .el-radio.is-checked .el-radio__label {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.4), rgba(118, 75, 162, 0.3));
  border-color: #667eea;
  color: #00f3ff;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.profile-edit-dialog .el-divider {
  background-color: rgba(102, 126, 234, 0.2);
  margin: 25px 0;
  position: relative;
}

.profile-edit-dialog .el-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

.profile-edit-dialog .el-date-picker {
  width: 100%;
}

.profile-edit-dialog .el-date-picker__editor-wrap {
  input {
    color: #fff;
  }
}

/* 修复字数限制提示的背景色 - 个人资料 */
.profile-edit-dialog .el-input__count-inner {
  background: rgba(255, 255, 255, 0.03) !important;
  color: rgba(255, 255, 255, 0.4) !important;
  border-radius: 4px !important;
  padding: 2px 6px !important;
  font-size: 12px !important;
  border: none !important;
  box-shadow: none !important;
}

/* 修复性别选项样式 */
.profile-edit-dialog .gender-group .el-radio {
  margin: 0 !important;
}

.profile-edit-dialog .gender-group .el-radio__input {
  display: none;
}

.profile-edit-dialog .gender-group .el-radio__label {
  padding: 8px 20px !important;
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  border-radius: 8px !important;
  color: rgba(255, 255, 255, 0.8) !important;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px !important;
  line-height: 1.5 !important;
  height: auto !important;
}

.profile-edit-dialog .el-dialog__footer {
  padding: 18px 35px;
  border-top: 1px solid rgba(102, 126, 234, 0.2);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: rgba(102, 126, 234, 0.02);
}

.dialog-footer .el-button {
  min-width: 90px;
  font-size: 13px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 10px;
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
.profile-edit-dialog .el-dialog__body::-webkit-scrollbar {
  width: 8px;
}

.profile-edit-dialog .el-dialog__body::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 4px;
}

.profile-edit-dialog .el-dialog__body::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #00f3ff);
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
}

.profile-edit-dialog .el-dialog__body::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #00f3ff, #bc13fe);
  box-shadow: 0 0 20px rgba(0, 243, 255, 0.6);
}

/* 遮罩层加强 */
.profile-edit-dialog + .el-overlay {
  background-color: rgba(10, 10, 30, 0.85);
  backdrop-filter: blur(8px);
}

/* 手机号修改对话框样式 */
.phone-bind-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  box-shadow: 
    0 8px 40px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(102, 126, 234, 0.2) !important;
}

.phone-bind-dialog .el-dialog__header {
  padding: 25px 35px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent);
}

.phone-bind-dialog .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.phone-bind-dialog .el-dialog__body {
  padding: 35px;
  background: transparent;
}

.phone-bind-dialog .el-form-item__label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 500;
  font-size: 14px;
}

.phone-bind-dialog .el-input__wrapper,
.phone-bind-dialog .el-textarea__wrapper {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(102, 126, 234, 0.2);
  box-shadow: 
    0 3px 15px rgba(102, 126, 234, 0.05),
    inset 0 0 8px rgba(102, 126, 234, 0.01);
  border-radius: 10px;
  padding: 10px 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.phone-bind-dialog .el-input__wrapper:hover,
.phone-bind-dialog .el-textarea__wrapper:hover {
  border-color: #667eea;
  box-shadow: 
    0 0 25px rgba(102, 126, 234, 0.4),
    inset 0 0 15px rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
}

.phone-bind-dialog .el-input__wrapper.is-focus,
.phone-bind-dialog .el-textarea__wrapper.is-focus {
  border-color: #00f3ff;
  box-shadow: 
    0 0 35px rgba(0, 243, 255, 0.5),
    inset 0 0 20px rgba(0, 243, 255, 0.08);
}

.phone-bind-dialog .el-input__inner,
.phone-bind-dialog .el-textarea__inner {
  color: #fff;
  font-size: 14px;
}

.phone-bind-dialog .el-input__inner::placeholder,
.phone-bind-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.phone-bind-dialog .form-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 10px 12px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.phone-bind-dialog .form-tips .el-icon {
  color: #667eea;
  font-size: 16px;
}

.phone-bind-dialog .form-tips span {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.5;
}

.phone-bind-dialog .el-divider {
  background-color: rgba(102, 126, 234, 0.2);
  margin: 25px 0;
  position: relative;
}

.phone-bind-dialog .el-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

/* 密码修改对话框样式 - 科技感增强版 */
.password-change-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  box-shadow: 
    0 8px 40px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(102, 126, 234, 0.2) !important;
}

.password-change-dialog .el-dialog__header {
  padding: 25px 35px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent) !important;
}

.password-change-dialog .el-dialog__title {
  color: #fff !important;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.password-change-dialog .el-dialog__body {
  padding: 35px;
  background: transparent !important;
}

.password-change-dialog .el-form-item__label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 500;
  font-size: 14px;
}

.password-change-dialog .el-input__wrapper,
.password-change-dialog .el-textarea__wrapper {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(102, 126, 234, 0.2);
  box-shadow: 
    0 3px 15px rgba(102, 126, 234, 0.05),
    inset 0 0 8px rgba(102, 126, 234, 0.01);
  border-radius: 10px;
  padding: 10px 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.password-change-dialog .el-input__wrapper:hover,
.password-change-dialog .el-textarea__wrapper:hover {
  border-color: #667eea;
  box-shadow: 
    0 0 25px rgba(102, 126, 234, 0.4),
    inset 0 0 15px rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
}

.password-change-dialog .el-input__wrapper.is-focus,
.password-change-dialog .el-textarea__wrapper.is-focus {
  border-color: #00f3ff;
  box-shadow: 
    0 0 35px rgba(0, 243, 255, 0.5),
    inset 0 0 20px rgba(0, 243, 255, 0.08);
}

.password-change-dialog .el-input__inner,
.password-change-dialog .el-textarea__inner {
  color: #fff;
  font-size: 14px;
}

.password-change-dialog .el-input__inner::placeholder,
.password-change-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.password-change-dialog .el-divider {
  background-color: rgba(102, 126, 234, 0.2);
  margin: 25px 0;
  position: relative;
}

.password-change-dialog .el-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #667eea, transparent);
}

/* 地址管理对话框样式 */
.address-manage-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  box-shadow: 
    0 8px 40px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(102, 126, 234, 0.2) !important;
}

.address-manage-dialog .el-dialog__header {
  padding: 25px 35px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent) !important;
}

.address-manage-dialog .el-dialog__title {
  color: #fff !important;
  font-size: 18px;
  font-weight: 600;
}

.address-manage-dialog .el-dialog__body {
  padding: 35px;
  background: transparent !important;
  max-height: 65vh;
  overflow-y: auto;
}

.address-toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 25px;
  padding: 0 5px;
}

/* 科技感按钮样式 - 新增地址 */
.tech-btn-add {
  position: relative !important;
  overflow: hidden !important;
  border: none !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 
    0 4px 15px rgba(102, 126, 234, 0.4),
    0 0 20px rgba(102, 126, 234, 0.2) !important;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 12px 28px !important;
  font-weight: 600 !important;
  letter-spacing: 0.5px !important;
}

.tech-btn-add::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 70%
  );
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

.tech-btn-add:hover {
  transform: translateY(-2px) scale(1.02) !important;
  box-shadow: 
    0 8px 25px rgba(102, 126, 234, 0.6),
    0 0 30px rgba(102, 126, 234, 0.4),
    inset 0 0 20px rgba(255, 255, 255, 0.1) !important;
}

.tech-btn-add:active {
  transform: translateY(0) scale(0.98) !important;
}

/* 科技感按钮样式 - 刷新列表 */
.tech-btn-refresh {
  position: relative !important;
  overflow: hidden !important;
  border: 2px solid rgba(102, 126, 234, 0.5) !important;
  background: rgba(255, 255, 255, 0.03) !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 12px 28px !important;
  font-weight: 600 !important;
  letter-spacing: 0.5px !important;
}

.tech-btn-refresh .btn-icon-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  opacity: 0;
  transition: opacity 0.3s;
}

.tech-btn-refresh:hover {
  border-color: #667eea !important;
  background: rgba(102, 126, 234, 0.15) !important;
  transform: translateY(-2px) !important;
  box-shadow: 
    0 6px 20px rgba(102, 126, 234, 0.4),
    0 0 15px rgba(102, 126, 234, 0.2) !important;
}

.tech-btn-refresh:hover .btn-icon-bg {
  opacity: 1;
}

.tech-btn-refresh:active {
  transform: translateY(0) !important;
}

/* 按钮文字和图标 */
.btn-text {
  position: relative;
  z-index: 1;
}

.btn-icon-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 6px;
  z-index: 1;
}

.btn-icon {
  position: relative;
  z-index: 2;
  font-size: 16px !important;
  transition: transform 0.3s;
}

.tech-btn-add:hover .btn-icon {
  transform: scale(1.2) rotate(15deg);
}

.tech-btn-refresh:hover .btn-icon {
  transform: scale(1.2);
}

/* 刷新按钮加载时的旋转动画 */
.tech-btn-refresh.is-loading .btn-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.address-list {
  min-height: 300px;
}

.address-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.address-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: cardSlideIn 0.5s ease-out;
}

@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.address-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent);
  opacity: 0;
  transition: opacity 0.4s;
  pointer-events: none; /* 关键：让鼠标事件穿透伪元素 */
}

.address-card:hover::before {
  opacity: 1;
}

.address-card:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(102, 126, 234, 0.5);
  transform: translateY(-4px) scale(1.01);
  box-shadow: 
    0 12px 35px rgba(102, 126, 234, 0.3),
    0 0 20px rgba(102, 126, 234, 0.15),
    inset 0 0 30px rgba(102, 126, 234, 0.05);
}

.address-card.is-default {
  border-color: rgba(255, 193, 7, 0.6);
  background: rgba(255, 193, 7, 0.08);
  box-shadow: 
    0 4px 20px rgba(255, 193, 7, 0.2),
    0 0 30px rgba(255, 193, 7, 0.1);
}

.address-card.is-default::after {
  content: '👑';
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 20px;
  animation: crownFloat 2s ease-in-out infinite;
}

@keyframes crownFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.address-card.is-default:hover {
  border-color: rgba(255, 193, 7, 0.8);
  background: rgba(255, 193, 7, 0.12);
  box-shadow: 
    0 12px 35px rgba(255, 193, 7, 0.35),
    0 0 40px rgba(255, 193, 7, 0.2),
    inset 0 0 30px rgba(255, 193, 7, 0.08);
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.receiver-name {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.receiver-phone {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  min-height: 60px;
}

.address-detail .el-icon {
  color: #667eea;
  font-size: 16px;
  margin-top: 2px;
}

.address-actions {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid rgba(102, 126, 234, 0.15);
  position: relative;
  z-index: 10; /* 确保按钮在最上层 */
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 地址表单对话框样式 */
.address-form-dialog {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  box-shadow: 
    0 8px 40px rgba(0, 0, 0, 0.6),
    0 0 60px rgba(102, 126, 234, 0.2) !important;
}

.address-form-dialog .el-dialog__header {
  padding: 25px 35px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), transparent) !important;
}

.address-form-dialog .el-dialog__title {
  color: #fff !important;
  font-size: 18px;
  font-weight: 600;
}

.address-form-dialog .el-dialog__body {
  padding: 35px;
  background: transparent !important;
  max-height: 70vh;
  overflow-y: auto;
}

.address-form-dialog .el-form-item__label {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 500;
  font-size: 14px;
}

.address-form-dialog .el-input__wrapper,
.address-form-dialog .el-textarea__inner {
  background: rgba(255, 255, 255, 0.03) !important;
  border: 1px solid rgba(102, 126, 234, 0.3) !important;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.15),
    inset 0 0 4px rgba(102, 126, 234, 0.05) !important;
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.address-form-dialog .el-input__wrapper:hover,
.address-form-dialog .el-textarea__inner:hover {
  background: rgba(255, 255, 255, 0.06) !important;
  border-color: rgba(102, 126, 234, 0.5) !important;
  box-shadow: 
    0 4px 15px rgba(102, 126, 234, 0.25),
    inset 0 0 8px rgba(102, 126, 234, 0.1) !important;
}

.address-form-dialog .el-input__wrapper.is-focus,
.address-form-dialog .el-textarea__inner.is-focus {
  background: rgba(255, 255, 255, 0.06) !important;
  border-color: #667eea !important;
  box-shadow: 
    0 0 0 3px rgba(102, 126, 234, 0.15),
    0 4px 20px rgba(102, 126, 234, 0.35),
    inset 0 0 10px rgba(102, 126, 234, 0.15) !important;
}

.address-form-dialog .el-input__inner,
.address-form-dialog .el-textarea__inner {
  color: #fff !important;
  font-size: 14px;
  caret-color: #667eea;
}

.address-form-dialog .el-input__inner::placeholder,
.address-form-dialog .el-textarea__inner::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.address-form-dialog .el-textarea__inner {
  resize: vertical;
  min-height: 80px;
}

/* 修复字数限制提示的背景色 */
.address-form-dialog .el-input__count,
.address-form-dialog .el-input__count-inner {
  background: rgba(255, 255, 255, 0.03) !important;
  color: rgba(255, 255, 255, 0.4) !important;
  border-radius: 4px !important;
  padding: 2px 6px !important;
  font-size: 12px !important;
  border: none !important;
  box-shadow: none !important;
}

/* 地址卡片中的输入框优化 */
.address-form-dialog .el-radio-group {
  display: flex;
  gap: 15px;
}

.address-form-dialog .el-radio {
  margin: 0;
}

.address-form-dialog .el-radio__input {
  display: none;
}

.address-form-dialog .el-radio__label {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(102, 126, 234, 0.3);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.address-form-dialog .el-radio__label:hover {
  background: rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.5);
  color: #fff;
}

.address-form-dialog .el-radio.is-checked .el-radio__label {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.25));
  border-color: #667eea;
  color: #00f3ff;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.location-section {
  display: flex;
  align-items: center;
}

.form-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 10px 12px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  border-left: 3px solid #667eea;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.5;
}

.form-tips .el-icon {
  color: #667eea;
  font-size: 16px;
}

.form-tips-inline {
  margin-left: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.form-tips-inline .el-icon {
  color: #667eea;
  font-size: 14px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .profile-edit-dialog .el-dialog {
    width: 90% !important;
  }
  
  .avatar-section {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
  
  .profile-edit-dialog .el-dialog__body {
    padding: 30px 20px;
  }
}

/* ========== 设为默认地址 - 紫色科技风确认框 ========== */
.tech-star-confirm {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #1f2a40 100%) !important;
  border: 2px solid rgba(102, 126, 234, 0.4) !important;
  border-radius: 16px !important;
  box-shadow: 
    0 0 40px rgba(102, 126, 234, 0.3),
    0 0 80px rgba(102, 126, 234, 0.15),
    inset 0 0 60px rgba(102, 126, 234, 0.1) !important;
  backdrop-filter: blur(20px);
  animation: starBoxFadeIn 0.3s ease-out;
}

@keyframes starBoxFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}


.confirm-star-content {
  position: relative;
  z-index: 2; /* 确保内容在特效上层 */
  text-align: center;
  padding: 10px 20px 5px;
}

.star-bg {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background-image: 
    radial-gradient(rgba(102, 126, 234, 0.5) 1px, transparent 1px),
    radial-gradient(rgba(102, 126, 234, 0.3) 1px, transparent 1px);
  background-size: 30px 30px, 50px 50px;
  background-position: 0 0, 25px 25px;
  opacity: 0.3;
  animation: starTwinkle 3s ease-in-out infinite;
  pointer-events: none; /* 关键：让背景不阻挡点击 */
}

@keyframes starTwinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.5; }
}

.star-icon-wrapper {
  font-size: 64px;
  margin: 25px 0 15px;
  filter: drop-shadow(0 0 20px rgba(102, 126, 234, 0.6));
  animation: starFloat 2s ease-in-out infinite;
}

@keyframes starFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.star-title {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px 0;
  text-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
  letter-spacing: 1px;
}

.star-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 18px 0;
  line-height: 1.6;
}

.star-tips {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(102, 126, 234, 0.3);
  border-radius: 8px;
  color: rgba(102, 126, 234, 0.9);
  font-size: 12px;
  line-height: 1.5;
}

.tips-icon {
  font-size: 16px;
}

.tech-star-confirm .el-message-box__header {
  padding: 0;
}

.tech-star-confirm .el-message-box__content {
  padding: 0;
  display: flex;
  justify-content: center; /* 内容居中 */
  align-items: center;
}

.tech-star-confirm .el-message-box__message {
  width: 100%;
  text-align: center;
}

.tech-star-confirm .el-message-box__footer {
  padding: 20px 30px 25px;
  display: flex;
  justify-content: space-between; /* 按钮分布在两边 */
  gap: 12px;
}

.tech-star-confirm .el-message-box__btns {
  display: flex;
  justify-content: space-between; /* 两端对齐 */
  width: 100%;
}

.tech-star-confirm .el-message-box__footer .el-button {
  flex: 0 0 auto; /* 按钮不拉伸 */
  min-width: 100px;
}

.tech-star-confirm .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.tech-star-confirm .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6) !important;
}

.tech-star-confirm .el-button--default {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 2px solid rgba(102, 126, 234, 0.4) !important;
  transition: all 0.3s !important;
}

.tech-star-confirm .el-button--default:hover {
  background: rgba(102, 126, 234, 0.15) !important;
  border-color: #667eea !important;
  transform: translateY(-2px) !important;
}

/* ========== 删除地址 - 红色警戒风确认框 ========== */
.tech-delete-confirm {
  background: linear-gradient(135deg, #1a0a0a 0%, #2a0f0f 50%, #1f1a1a 100%) !important;
  border: 2px solid rgba(255, 107, 107, 0.4) !important;
  border-radius: 16px !important;
  box-shadow: 
    0 0 40px rgba(255, 107, 107, 0.3),
    0 0 80px rgba(255, 107, 107, 0.15),
    inset 0 0 60px rgba(255, 107, 107, 0.1) !important;
  backdrop-filter: blur(20px);
  overflow: visible !important; /* 关键：让特效可以溢出显示 */
  animation: deleteBoxPulse 0.3s ease-out;
}

@keyframes deleteBoxPulse {
  from {
    opacity: 0;
    transform: scale(0.92);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.tech-delete-confirm::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #ff6b6b, transparent);
  animation: deleteScan 2s ease-in-out infinite;
}

@keyframes deleteScan {
  0%, 100% { 
    transform: translateX(-100%);
    opacity: 0.5;
  }
  50% { 
    transform: translateX(100%);
    opacity: 1;
  }
}

.confirm-delete-content {
  position: relative;
  z-index: 2; /* 确保内容在特效上层 */
  text-align: center;
  padding: 10px 20px 5px;
}

.delete-shield {
  font-size: 56px;
  margin: 30px 0 15px;
  filter: drop-shadow(0 0 20px rgba(255, 107, 107, 0.5));
  animation: shieldGlow 2s ease-in-out infinite;
}

@keyframes shieldGlow {
  0%, 100% { 
    filter: drop-shadow(0 0 15px rgba(255, 107, 107, 0.4));
    transform: scale(1);
  }
  50% { 
    filter: drop-shadow(0 0 30px rgba(255, 107, 107, 0.7));
    transform: scale(1.05);
  }
}

.delete-title {
  font-size: 22px;
  font-weight: 700;
  color: #ff6b6b;
  margin: 0 0 12px 0;
  text-shadow: 0 0 10px rgba(255, 107, 107, 0.5);
  letter-spacing: 1px;
}

.delete-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.65);
  margin: 0 0 18px 0;
  line-height: 1.6;
}

.delete-warning {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px;
  background: rgba(255, 107, 107, 0.15);
  border: 1px solid rgba(255, 107, 107, 0.4);
  border-radius: 8px;
  color: rgba(255, 107, 107, 0.95);
  font-size: 12px;
  line-height: 1.5;
  font-weight: 500;
}

.warn-icon {
  font-size: 18px;
}

.tech-delete-confirm .el-message-box__header {
  padding: 0;
}

.tech-delete-confirm .el-message-box__content {
  padding: 0;
  display: flex;
  justify-content: center; /* 内容居中 */
  align-items: center;
}

.tech-delete-confirm .el-message-box__message {
  width: 100%;
  text-align: center;
}

.tech-delete-confirm .el-message-box__footer {
  padding: 20px 30px 25px;
  display: flex;
  justify-content: space-between; /* 按钮分布在两边 */
  gap: 12px;
}

.tech-delete-confirm .el-message-box__btns {
  display: flex;
  justify-content: space-between; /* 两端对齐 */
  width: 100%;
}

.tech-delete-confirm .el-message-box__footer .el-button {
  flex: 0 0 auto; /* 按钮不拉伸 */
  min-width: 100px;
}

.tech-delete-confirm .el-button--primary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.tech-delete-confirm .el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.6) !important;
}

.tech-delete-confirm .el-button--default {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 2px solid rgba(255, 107, 107, 0.3) !important;
  transition: all 0.3s !important;
}

.tech-delete-confirm .el-button--default:hover {
  background: rgba(255, 107, 107, 0.15) !important;
  border-color: #ff6b6b !important;
  transform: translateY(-2px) !important;
}
</style>