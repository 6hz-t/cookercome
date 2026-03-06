<template>
  <div class="chef-list">
    <h2>厨师列表</h2>
    
    <!-- 搜索筛选 -->
    <div class="search-box">
      <el-input v-model="searchForm.specialty" placeholder="擅长菜系" style="width: 200px" />
      <el-select v-model="searchForm.level" placeholder="厨师等级" style="width: 150px; margin: 0 10px">
        <el-option label="初级厨师" :value="1" />
        <el-option label="中级厨师" :value="2" />
        <el-option label="高级厨师" :value="3" />
        <el-option label="名厨" :value="4" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    
    <!-- 厨师列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="chef in chefList" :key="chef.id">
        <el-card class="chef-card">
          <div class="chef-header">
            <el-avatar :size="60" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            <div class="chef-info">
              <h4>{{ chef.name || '厨师' }}</h4>
              <el-tag size="small" v-if="chef.level === 1">初级</el-tag>
              <el-tag size="small" type="success" v-else-if="chef.level === 2">中级</el-tag>
              <el-tag size="small" type="warning" v-else-if="chef.level === 3">高级</el-tag>
              <el-tag size="small" type="danger" v-else-if="chef.level === 4">名厨</el-tag>
            </div>
          </div>
          
          <div class="chef-detail">
            <p><el-icon><Location /></el-icon> 服务半径：{{ chef.serviceRadius || 10 }}km</p>
            <p><el-icon><Star /></el-icon> 评分：{{ chef.rating || 5.0 }}</p>
            <p><el-icon><User /></el-icon> 服务次数：{{ chef.serviceCount || 0 }}</p>
            <p>擅长：{{ chef.specialty || '川菜、湘菜' }}</p>
            <p>起步价：¥{{ chef.basePrice || 100 }}</p>
          </div>
          
          <el-button type="primary" style="width: 100%" @click="$router.push(`/chef/${chef.id}`)">
            查看详情
          </el-button>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Location, Star, User } from '@element-plus/icons-vue'
import { getChefList } from '@/api/chef'

const searchForm = reactive({
  specialty: '',
  level: null
})

const chefList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadChefs = async () => {
  try {
    const res = await getChefList(currentPage.value, pageSize.value, searchForm.specialty, searchForm.level)
    chefList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载厨师列表失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadChefs()
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadChefs()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadChefs()
}

onMounted(() => {
  loadChefs()
})
</script>

<style scoped>
.chef-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.search-box {
  text-align: center;
  margin-bottom: 30px;
}

.chef-card {
  margin-bottom: 20px;
}

.chef-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.chef-info {
  margin-left: 15px;
}

.chef-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.chef-detail p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.chef-detail .el-icon {
  margin-right: 5px;
}

.pagination {
  text-align: center;
  margin-top: 30px;
}
</style>
