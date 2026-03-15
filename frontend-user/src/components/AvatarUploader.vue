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
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, Loading } from '@element-plus/icons-vue'
import { uploadAvatar, getImageSignatureUrl } from '@/api/avatar'
import { getUserAvatar } from '@/utils/avatar'

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

// 当前头像（优先使用带签名的 URL）
const displaySrc = computed(() => {
  // 如果有签名 URL，优先使用
  if (displayAvatarUrl.value) {
    return displayAvatarUrl.value
  }
  
  // 否则使用原始 URL
  const avatar = props.modelValue || getUserAvatar()
  return avatar
})

// 文件输入引用
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const displayAvatarUrl = ref<string>('')  // 用于存储带签名的 URL

// 加载预签名 URL
const loadSignatureUrl = async (relativePath: string) => {
  try {
    const response = await getImageSignatureUrl(relativePath, 30 * 24 * 60) // 30 天
    displayAvatarUrl.value = response.data.url
  } catch (error) {
    console.error('获取预签名 URL 失败:', error)
  }
}

// 监听外部值变化，自动加载签名 URL
watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal.includes('aliyuncs.com')) {
    // 提取相对路径并加载签名 URL
    let relativePath = newVal.substring(newVal.indexOf('/avatar/'))
    
    // 移除可能的前导斜杠
    if (relativePath.startsWith('/')) {
      relativePath = relativePath.substring(1)
    }
    
    if (relativePath) {
      loadSignatureUrl(relativePath)
    }
  }
}, { immediate: true })

// 处理图片加载错误
const handleImageError = (e: Event) => {
  // 静默处理图片加载错误
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
