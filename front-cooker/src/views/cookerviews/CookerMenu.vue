<template>
  <div class="page">
    <el-card shadow="hover">
      <template #header>
        <div class="head-row">
          <span>我的菜单</span>
          <div>
            <el-button type="primary" @click="openCreate">新增菜品/套餐</el-button>
          </div>
        </div>
      </template>

      <div class="filters">
        <el-input v-model="keyword" clearable placeholder="按菜品名/标签搜索" class="w-260" />
        <el-select v-model="statusFilter" clearable placeholder="上架状态" class="w-140">
          <el-option label="上架中" value="on" />
          <el-option label="已下架" value="off" />
        </el-select>
      </div>

      <el-table :data="filteredMenus" border>
        <el-table-column prop="name" label="名称" min-width="170" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column label="售价" width="110">
          <template #default="scope">¥{{ money(scope.row.price) }}</template>
        </el-table-column>
        <el-table-column label="预计备料" width="120">
          <template #default="scope">{{ scope.row.prepMinutes }} 分钟</template>
        </el-table-column>
        <el-table-column label="标签" min-width="200">
          <template #default="scope">
            <el-tag v-for="tag in scope.row.tags" :key="`${scope.row.id}-${tag}`" size="small" class="tag">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'on' ? 'success' : 'info'">{{ scope.row.status === 'on' ? '上架中' : '已下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link @click="toggleStatus(scope.row)">{{ scope.row.status === 'on' ? '下架' : '上架' }}</el-button>
            <el-button link type="danger" @click="removeMenu(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑菜单' : '新增菜单'" width="560px">
      <el-form label-width="92px" :model="form">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="如：轻奢六人家宴套餐" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.type" placeholder="选择类型" style="width: 100%">
            <el-option label="家宴" value="家宴" />
            <el-option label="私厨" value="私厨" />
            <el-option label="团建" value="团建" />
            <el-option label="宴会" value="宴会" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" required>
          <el-input-number v-model="form.price" :min="1" :step="20" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备料时间">
          <el-input-number v-model="form.prepMinutes" :min="20" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tagsText" placeholder="多个标签用顿号或逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { money } from '@/utils/cookerOrder'

const STORAGE_KEY = 'cooker_menu_v1'

const defaultMenus = [
  {
    id: 1,
    name: '家宴精品六菜一汤',
    type: '家宴',
    price: 668,
    prepMinutes: 120,
    tags: ['少油低盐', '儿童友好'],
    status: 'on'
  },
  {
    id: 2,
    name: '商务私厨轻奢套餐',
    type: '私厨',
    price: 888,
    prepMinutes: 150,
    tags: ['摆盘精致', '定制口味'],
    status: 'on'
  },
  {
    id: 3,
    name: '团队团建热菜套餐',
    type: '团建',
    price: 1288,
    prepMinutes: 180,
    tags: ['大份量', '出餐快'],
    status: 'off'
  }
]

function readMenus() {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(defaultMenus))
    return [...defaultMenus]
  }
  try {
    const list = JSON.parse(raw)
    return Array.isArray(list) ? list : [...defaultMenus]
  } catch (error) {
    return [...defaultMenus]
  }
}

const menus = ref(readMenus())
const keyword = ref('')
const statusFilter = ref('')

const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({
  name: '',
  type: '',
  price: 100,
  prepMinutes: 90,
  tagsText: ''
})

const filteredMenus = computed(() => {
  return menus.value.filter((item) => {
    const text = `${item.name} ${(item.tags || []).join(' ')}`
    const hitKeyword = !keyword.value || text.includes(keyword.value.trim())
    const hitStatus = !statusFilter.value || item.status === statusFilter.value
    return hitKeyword && hitStatus
  })
})

function persist() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(menus.value))
}

function openCreate() {
  editingId.value = null
  form.value = {
    name: '',
    type: '',
    price: 100,
    prepMinutes: 90,
    tagsText: ''
  }
  dialogVisible.value = true
}

function openEdit(item) {
  editingId.value = item.id
  form.value = {
    name: item.name,
    type: item.type,
    price: item.price,
    prepMinutes: item.prepMinutes,
    tagsText: (item.tags || []).join('、')
  }
  dialogVisible.value = true
}

function submitForm() {
  if (!form.value.name.trim() || !form.value.type || !form.value.price) {
    ElMessage.warning('请完善菜单必填信息')
    return
  }

  const payload = {
    name: form.value.name.trim(),
    type: form.value.type,
    price: Number(form.value.price),
    prepMinutes: Number(form.value.prepMinutes || 90),
    tags: form.value.tagsText
      .split(/[、,，\s]+/)
      .map((item) => item.trim())
      .filter(Boolean)
      .slice(0, 6)
  }

  if (editingId.value) {
    menus.value = menus.value.map((item) =>
      item.id === editingId.value
        ? {
            ...item,
            ...payload
          }
        : item
    )
  } else {
    menus.value.unshift({
      id: Date.now(),
      status: 'on',
      ...payload
    })
  }

  persist()
  dialogVisible.value = false
  ElMessage.success('菜单保存成功')
}

function toggleStatus(item) {
  item.status = item.status === 'on' ? 'off' : 'on'
  persist()
  ElMessage.success(`已${item.status === 'on' ? '上架' : '下架'}菜单`)
}

async function removeMenu(id) {
  try {
    await ElMessageBox.confirm('确认删除该菜单项吗？', '提示', { type: 'warning' })
    menus.value = menus.value.filter((item) => item.id !== id)
    persist()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 20px;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.w-260 {
  width: 260px;
}

.w-140 {
  width: 140px;
}

.tag {
  margin-right: 6px;
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

  .w-260,
  .w-140 {
    width: 100%;
  }
}
</style>
