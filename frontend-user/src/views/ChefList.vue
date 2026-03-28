<template>
  <div class="chefInfo-list">
    <h2>鍘ㄥ笀鍒楄〃</h2>

    <!-- 鎼滅储绛涢€?-->
    <div class="search-box">
      <el-input v-model="searchForm.specialty" placeholder="鎿呴暱鑿滅郴" style="width: 200px" clearable @keyup.enter="handleSearch" />
      <el-select v-model="searchForm.level" placeholder="鍘ㄥ笀绛夌骇" style="width: 150px; margin: 0 10px" clearable>
        <el-option label="鍒濈骇鍘ㄥ笀" :value="1" />
        <el-option label="涓骇鍘ㄥ笀" :value="2" />
        <el-option label="楂樼骇鍘ㄥ笀" :value="3" />
        <el-option label="鍚嶅帹" :value="4" />
      </el-select>
      <el-button type="primary" @click="handleSearch">鎼滅储</el-button>
      <el-button @click="handleReset">閲嶇疆</el-button>
      <el-button @click="handleClearCache" title="重新从数据库加载最新数据">刷新数据</el-button>
    </div>

    <!-- 鍘ㄥ笀鍒楄〃 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="chef in chefList" :key="chef.id">
        <el-card class="chefInfo-card">
          <div class="chefInfo-header">
            <el-avatar :size="60" :src="chef.avatarUrl || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
            <div class="chefInfo-info">
              <h4>{{ chef.realName || chef.name || '鍘ㄥ笀' }}</h4>
              <el-tag size="small" v-if="chef.chefLevel === 1">鍒濈骇鍘ㄥ笀</el-tag>
              <el-tag size="small" type="success" v-else-if="chef.chefLevel === 2">涓骇鍘ㄥ笀</el-tag>
              <el-tag size="small" type="warning" v-else-if="chef.chefLevel === 3">楂樼骇鍘ㄥ笀</el-tag>
              <el-tag size="small" type="danger" v-else-if="chef.chefLevel === 4">璧勬繁鍘ㄥ笀</el-tag>
              <el-tag size="small" type="danger" v-else-if="chef.chefLevel === 5" effect="dark">鐗圭骇鍘ㄥ笀</el-tag>
            </div>
          </div>

          <div class="chefInfo-detail">
            <p><el-icon><Location /></el-icon> 服务半径：{{ chef.serviceRadius || chef.minPrice || 10 }}km</p>
            <p><el-icon><Star /></el-icon> 评分：{{ chef.rating || 5.0 }}</p>
            <p><el-icon><User /></el-icon> 服务次数：{{ chef.completedOrders || 0 }}</p>
            <p>从业年限：{{ chef.experienceYears || 0 }}年</p>
            <p>起步价：¥{{ chef.minPrice || 100 }}</p>
            <p class="intro">简介：{{ chef.introduction || '暂无简介' }}</p>
          </div>

          <el-button type="primary" style="width: 100%" @click="$router.push(`/chef/${chef.id}`)">
            鏌ョ湅璇︽儏
          </el-button>
        </el-card>
      </el-col>
      <el-col :span="8" v-if="chefList.length === 0">
        <el-empty description="鏆傛棤鍘ㄥ笀淇℃伅" />
      </el-col>
    </el-row>

    <!-- 鍒嗛〉 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Location, Star, User } from '@element-plus/icons-vue'
import { getChefList, type ChefListParams } from '@/api/chef'
import request from '@/utils/request'

const searchForm = reactive<ChefListParams>({
  specialty: '',
  level: null
})

const chefList = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadChefs = async () => {
  try {
    const params: ChefListParams = {
      page: currentPage.value,
      size: pageSize.value,
      specialty: searchForm.specialty || undefined,
      level: searchForm.level ?? undefined
    }
    const res = await getChefList(params)
    console.log('鍘ㄥ笀鍒楄〃鍝嶅簲:', res)

    // 鍒嗛〉鏁版嵁鍦?res.data 涓紙MyBatis-Plus Page 瀵硅薄锛?
    const pageData = res.data || res
    chefList.value = pageData.records || pageData.list || []
    total.value = pageData.total || 0

    console.log('瑙ｆ瀽鍚庣殑鏁版嵁:', {
      list: chefList.value,
      total: total.value
    })
    
    // 璋冭瘯锛氭墦鍗版瘡涓帹甯堢殑绛夌骇
    chefList.value.forEach((chef, index) => {
      console.log(`鍘ㄥ笀${index + 1}: ${chef.realName}, chefLevel=${chef.chefLevel}, status=${chef.status}`)
    })
  } catch (error) {
    console.error('鍔犺浇鍘ㄥ笀鍒楄〃澶辫触:', error)
    chefList.value = []
    total.value = 0
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadChefs()
}

const handleReset = () => {
  searchForm.specialty = ''
  searchForm.level = null
  currentPage.value = 1
  loadChefs()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadChefs()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadChefs()
}

// 娓呴櫎缂撳瓨骞堕噸鏂板姞杞?
const handleClearCache = async () => {
  try {
    await request.post('/api/customer/booking/chef/cache/clear')
    ElMessage.success('数据已刷新')
    loadChefs()
  } catch (error) {
    console.error('娓呴櫎缂撳瓨澶辫触:', error)
    ElMessage.error('刷新数据失败')
  }
}

onMounted(() => {
  loadChefs()
})
</script>

<style scoped>
.chefInfo-list {
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
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.chefInfo-card {
  margin-bottom: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.chefInfo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.chefInfo-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.chefInfo-info {
  margin-left: 15px;
  flex: 1;
}

.chefInfo-info h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.chefInfo-detail p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.chefInfo-detail .intro {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.chefInfo-detail .el-icon {
  margin-right: 5px;
  color: #409eff;
}

.pagination {
  text-align: center;
  margin-top: 30px;
}
</style>

