<template>
  <div class="avatar-uploader">
    <!-- 头像显示区域 -->
    <div class="avatar-wrapper" @click="triggerUpload">
      <el-avatar 
        :size="size" 
        :src="displaySrc" 
        fit="cover" 
        class="user-avatar"
        @error="handleImageError"
      />
      
      <!-- 悬停时显示上传按钮 -->
      <div class="upload-overlay" v-if="editable">
        <el-icon class="upload-icon"><Camera /></el-icon>
        <span class="upload-text">点击上传</span>
      </div>
      
      <!-- 加载中 -->
      <div class="loading-overlay" v-if="uploading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span class="loading-text">上传中...</span>
      </div>
    </div>
    
    <!-- 隐藏的文件输入 -->
    <input 
      ref="fileInput" 
      type="file" 
      accept="image/*" 
      style="display: none"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, Loading } from '@element-plus/icons-vue'
import { uploadAvatar } from '@/api/avatar'
import defaultAvatar from '@/assets/images/default-avatar.svg'

interface Props {
  modelValue?: string  // 头像 URL
  size?: number        // 头像大小
  editable?: boolean   // 是否可编辑
  userId?: number      // 用户 ID
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  size: 100,
  editable: true,
  userId: 0
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'success', url: string): void
  (e: 'error', error: Error): void
}>()

// 显示头像（直接使用后端返回的 URL，不做任何处理）
const displaySrc = computed(() => {
  // 如果加载失败，显示默认头像
  if (hasError.value) {
    return defaultAvatar
  }
  // 如果有头像 URL，直接使用（后端已经处理好签名）
  if (props.modelValue) {
    return props.modelValue
  }
  // 否则使用默认头像
  return defaultAvatar
})

// 文件输入引用
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const hasError = ref(false)  // 图片加载是否失败

// 处理图片加载错误（显示默认头像）
const handleImageError = (e: Event) => {
  // 加载失败时使用默认头像
  hasError.value = true
}

// 触发文件选择
const triggerUpload = () => {
  if (!props.editable || uploading.value) return
  fileInput.value?.click()
}

// 处理文件变化
const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }
  
  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }
  
  await uploadFile(file)
}

// 上传文件到后端（后端再传 OSS）
const uploadFile = async (file: File) => {
  try {
    uploading.value = true
    
    // 直接调用后端接口上传
    const response = await uploadAvatar(file)
    const { fullUrl, relativePath } = response.data
    
    // 通知父组件更新显示（使用完整 URL）
    emit('update:modelValue', fullUrl)
    emit('success', fullUrl, relativePath)
    
    ElMessage.success('头像上传成功')
    
  } catch (error: any) {
    ElMessage.error('上传失败：' + (error.message || '请重试'))
    emit('error', error)
  } finally {
    uploading.value = false
    // 清空文件输入，允许重复选择同一文件
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}
</script>

<style scoped>
.avatar-uploader {
  position: relative;
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .upload-overlay {
  opacity: 1;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.upload-text {
  font-size: 12px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.loading-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.loading-text {
  font-size: 12px;
}

.user-avatar {
  border: 2px solid #e4e7ed;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.avatar-wrapper:hover .user-avatar {
  border-color: #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}
</style>
