<template>
  <div class="page">
    <el-card shadow="hover">
      <template #header>个性化设置</template>
      <el-form label-width="120px">
        <el-form-item label="主题模式">
          <el-radio-group v-model="form.theme">
            <el-radio-button label="light">浅色</el-radio-button>
            <el-radio-button label="dark">深色</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="字体大小">
          <div class="font-row">
            <el-slider v-model="fontPercent" :min="90" :max="120" :step="5" show-stops />
            <el-tag>{{ fontPercent }}%</el-tag>
          </div>
        </el-form-item>

        <el-divider />

        <el-form-item label="订单提醒">
          <el-switch v-model="form.orderReminder" />
        </el-form-item>
        <el-form-item label="系统提醒">
          <el-switch v-model="form.systemReminder" />
        </el-form-item>
        <el-form-item label="评价提醒">
          <el-switch v-model="form.evalReminder" />
        </el-form-item>

        <el-divider />

        <el-form-item label="偏好订单类型">
          <el-checkbox-group v-model="form.preferredOrderTypes">
            <el-checkbox label="家宴" />
            <el-checkbox label="私厨" />
            <el-checkbox label="团建" />
            <el-checkbox label="宴会" />
            <el-checkbox label="节日餐" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="常用入口推荐">
          <el-switch v-model="form.autoRecommend" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="save">保存设置</el-button>
          <el-button @click="reset">恢复默认</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <template #header>预览与提示</template>
      <div class="preview">
        <div class="sample" :style="{ fontSize: `${14 * (fontPercent / 100)}px` }">
          当前字号预览：平台将按照设置调整全局文本展示。
        </div>
        <el-alert type="info" :closable="false" show-icon title="网络异常时会自动提示离线模式，恢复后立即同步消息和订单状态。" />
        <el-alert
          type="success"
          :closable="false"
          show-icon
          :title="`当前推荐入口：${recommendedEntrances.join('、') || '暂无推荐'}`"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { defaultCookerSettings, getCookerSettings, updateCookerSettings } from '@/utils/cookerSettings'

const source = getCookerSettings()

const form = ref({
  ...source
})

const fontPercent = ref(Math.round((form.value.fontScale || 1) * 100))

const recommendedEntrances = computed(() => {
  if (!form.value.autoRecommend || !form.value.preferredOrderTypes?.length) {
    return ['工作台', '待接单', '历史订单']
  }

  const mapping = {
    家宴: '我的菜单',
    私厨: '收益明细',
    团建: '消息中心',
    宴会: '历史订单',
    节日餐: '工作台'
  }

  return form.value.preferredOrderTypes.map((item) => mapping[item]).filter(Boolean)
})

function save() {
  const payload = {
    ...form.value,
    fontScale: Number((fontPercent.value / 100).toFixed(2))
  }
  const next = updateCookerSettings(payload)
  form.value = { ...next }
  fontPercent.value = Math.round(next.fontScale * 100)
  ElMessage.success('设置已保存并生效')
}

function reset() {
  const next = updateCookerSettings(defaultCookerSettings)
  form.value = { ...next }
  fontPercent.value = Math.round(next.fontScale * 100)
  ElMessage.success('已恢复默认设置')
}
</script>

<style scoped>
.page {
  max-width: 980px;
  margin: 0 auto;
  padding: 0 20px 20px;
  display: grid;
  gap: 14px;
}

.font-row {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 12px;
}

.preview {
  display: grid;
  gap: 10px;
}

.sample {
  color: #2f4159;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f4f7fc;
  border: 1px solid #e6ebf4;
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }
}
</style>
