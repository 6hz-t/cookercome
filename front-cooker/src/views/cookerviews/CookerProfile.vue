<template>
  <div class="page">
    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>厨师资料</span>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </div>
      </template>

      <el-skeleton v-if="loading" :rows="8" animated />

      <el-form v-else :model="form" label-width="140px" class="form" :rules="rules" ref="formRef">
        <el-form-item label="资料 ID">
          <el-input v-model="form.idText" disabled />
        </el-form-item>

        <el-form-item label="用户 ID" required>
          <el-input v-model="form.userId" disabled />
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="form.gender" style="width: 100%">
            <el-option label="未知" :value="0" />
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="form.idCardNo" placeholder="请输入 18 位身份证号" maxlength="18" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="详细地址">
          <el-input v-model="form.detailAddress" placeholder="从地图选取自动填充">
            <template #append>
              <el-button @click="mapDialogVisible = true">地图选取</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="坐标">
          <div class="coord-row">
            <el-input :model-value="latitudeText" disabled />
            <el-input :model-value="longitudeText" disabled />
            <el-button @click="mapDialogVisible = true">重新选取</el-button>
          </div>
        </el-form-item>

        <el-form-item label="从业年限">
          <el-input-number v-model="form.experienceYears" :min="0" :step="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="厨师等级">
          <el-input-number v-model="form.chefLevel" :min="0" :step="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="最低服务价格">
          <el-input-number v-model="form.minPrice" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>

        <el-form-item label="个人介绍">
          <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="请输入个人介绍" />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="离线" :value="0" />
            <el-option label="在线" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <BaiduMapPickerDialog
      v-model="mapDialogVisible"
      title="地图选点（厨师地址）"
      :initial-location="profileLocation"
      @confirm="handleLocationPicked"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BaiduMapPickerDialog from '@/components/cooker/BaiduMapPickerDialog.vue'
import { getChefProfile, saveChefProfile } from '@/api/cooker'
import { parseChefId } from '@/utils/cookerSession'
import { setChefLocation } from '@/utils/location'
import { isValidIdCard, isValidPhone } from '@/utils/validator'

const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const mapDialogVisible = ref(false)

const form = reactive({
  id: 0,
  idText: '0',
  userId: '',
  realName: '',
  gender: 0,
  idCardNo: '',
  phone: '',
  detailAddress: '',
  experienceYears: 0,
  chefLevel: 0,
  minPrice: 0,
  introduction: '',
  latitude: 0,
  longitude: 0,
  status: 0
})

// 表单校验规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为 2-20 个字符', trigger: 'blur' }
  ],
  idCardNo: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback() // 非必填，空值通过
        } else if (!isValidIdCard(value)) {
          callback(new Error('请输入正确的身份证号'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback() // 非必填，空值通过
        } else if (!isValidPhone(value)) {
          callback(new Error('请输入正确的手机号'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const latitudeText = computed(() => (Number(form.latitude) ? Number(form.latitude).toFixed(6) : '未选择'))
const longitudeText = computed(() => (Number(form.longitude) ? Number(form.longitude).toFixed(6) : '未选择'))

const profileLocation = computed(() => ({
  lng: Number(form.longitude || 0),
  lat: Number(form.latitude || 0),
  address: form.detailAddress || ''
}))

function fillForm(data = {}) {
  form.id = Number(data.id || 0)
  form.idText = String(form.id)
  form.userId = String(data.userId || form.userId || '')
  form.realName = data.realName || ''
  form.gender = Number(data.gender || 0)
  form.idCardNo = data.idCardNo || ''
  form.phone = data.phone || ''
  form.detailAddress = data.detailAddress || ''
  form.experienceYears = Number(data.experienceYears || 0)
  form.chefLevel = Number(data.chefLevel || 0)
  form.minPrice = Number(data.minPrice || 0)
  form.introduction = data.introduction || ''
  form.latitude = Number(data.latitude || 0)
  form.longitude = Number(data.longitude || 0)
  form.status = Number(data.status || 0)

  setChefLocation({
    lng: form.longitude,
    lat: form.latitude,
    address: form.detailAddress
  })
}

function handleLocationPicked(location) {
  form.longitude = Number(location.lng || 0)
  form.latitude = Number(location.lat || 0)
  if (location.address) {
    form.detailAddress = location.address
  }
  setChefLocation({
    lng: form.longitude,
    lat: form.latitude,
    address: form.detailAddress
  })
  ElMessage.success('位置已更新')
}

async function loadProfile() {
  const userId = parseChefId()
  if (!userId) {
    ElMessage.warning('请先登录')
    router.replace('/cooker/login')
    return
  }

  loading.value = true
  try {
    form.userId = String(userId)
    const res = await getChefProfile(userId)
    if (res.code !== 200) {
      ElMessage.error(res.message || '加载资料失败')
      return
    }
    fillForm(res.data || { userId: String(userId) })
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载资料失败')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!form.userId) {
    ElMessage.warning('用户 ID 不能为空')
    return
  }

  // 表单验证
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.error('请检查表单填写是否正确')
    return
  }

  saving.value = true
  try {
    const payload = {
      id: form.id,
      userId: form.userId,
      realName: form.realName,
      gender: form.gender,
      idCardNo: form.idCardNo,
      phone: form.phone,
      detailAddress: form.detailAddress,
      experienceYears: form.experienceYears,
      chefLevel: form.chefLevel,
      minPrice: form.minPrice,
      introduction: form.introduction,
      latitude: form.latitude,
      longitude: form.longitude,
      status: form.status
    }

    const res = await saveChefProfile(payload)
    if (res.code !== 200) {
      ElMessage.error(res.message || '保存失败')
      return
    }

    fillForm(res.data || payload)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px 20px;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form {
  max-width: 760px;
}

.coord-row {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }

  .head-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .coord-row {
    grid-template-columns: 1fr;
  }
}
</style>
