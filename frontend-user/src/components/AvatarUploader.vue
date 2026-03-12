<template>
  <div class="avatar-uploader">
    <!-- 头像显示区域 -->
    <div class="avatar-wrapper" @click="triggerUpload">
      <el-avatar :size="size" :src="currentAvatar" fit="cover" class="user-avatar" />
      
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
import { getOssSignature } from '@/api/settings'
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

// 当前头像
const currentAvatar = computed(() => {
  return props.modelValue || getUserAvatar()
})

// 文件输入引用
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)

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

// 上传文件到 OSS
const uploadFile = async (file: File) => {
  try {
    uploading.value = true
    
    // 步骤 1：获取上传签名（从后端）
    const signatureRes = await getOssSignature(file.name)
    const { signatureUrl, fullUrl, relativePath } = signatureRes.data
    
    console.log('获取签名成功:', { signatureUrl, fullUrl, relativePath })
    
    // 步骤 2：使用签名直接上传到 OSS（PUT 方式）
    const response = await fetch(signatureUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type
      }
    })
    
    if (!response.ok) {
      throw new Error(`OSS 上传失败：${response.status} ${response.statusText}`)
    }
    
    console.log('OSS 上传成功，相对路径:', relativePath)
    
    // 步骤 3：通知父组件保存完整 URL 到数据库
    emit('update:modelValue', fullUrl)
    emit('success', fullUrl)
    
    ElMessage.success('头像上传成功')
    
  } catch (error: any) {
    console.error('头像上传失败:', error)
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

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    // 由父组件处理缓存更新
  }
})
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
