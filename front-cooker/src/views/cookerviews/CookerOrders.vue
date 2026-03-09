<template>
  <div class="container">
    <h3>历史订单</h3>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 200px"
        @keyup.enter="handleSearch">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>

      <el-select v-model="searchForm.status" placeholder="订单状态" clearable style="width: 150px">
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>

      <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
        end-placeholder="结束日期" style="width: 240px" value-format="YYYY-MM-DD" />

      <el-button type="primary" @click="handleSearch">
        <el-icon>
          <Search />
        </el-icon>
        搜索
      </el-button>
      <el-button @click="handleReset">
        <el-icon>
          <Refresh />
        </el-icon>
        重置
      </el-button>
    </div>

    <!-- 订单统计卡片 -->
    <div class="stats-cards">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-icon completed">
            <el-icon :size="28">
              <CircleCheck />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completed }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-icon cancelled">
            <el-icon :size="28">
              <CircleClose />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.cancelled }}</div>
            <div class="stat-label">已取消</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-icon total">
            <el-icon :size="28">
              <Document />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总订单数</div>
          </div>
        </div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-item">
          <div class="stat-icon revenue">
            <el-icon :size="28">
              <Money />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ stats.revenue.toFixed(2) }}</div>
            <div class="stat-label">总收入</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <el-card shadow="hover">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info-left">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.status)" size="large">
                {{ getStatusName(order.status) }}
              </el-tag>
            </div>
          </div>

          

          <!-- 订单信息 -->
          <div class="order-info">
            <div class="info-row">
              <span class="info-label">上门地址：</span>
              <span class="info-value">{{ order.address }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">客户备注：</span>
              <span class="info-value">{{ order.remark || '无' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">联系方式：</span>
              <span class="info-value">{{ order.customerPhone }}</span>
              <el-button link type="primary" @click="handleCall(order.customerPhone)">
                <el-icon>
                  <Phone />
                </el-icon>
              </el-button>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-total">
              <span>合计：</span>
              <span class="total-price">¥{{ order.totalPrice.toFixed(2) }}</span>
            </div>
            <div class="order-actions">
              <el-button type="info" @click="handleViewOrderDetail(order)">
                <el-icon>
                  <Document />
                </el-icon>
                查看详情
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions title="订单信息" :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="客户姓名">{{ currentOrder.customerName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="配送地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <el-table :data="currentOrder.items" style="margin-top: 20px" border>
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="price" label="单价" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column label="小计">
            <template #default="{ row }">
              ¥{{ (row.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>

        <div class="order-total-detail">
          <span>合计：</span>
          <span class="total-price">¥{{ currentOrder.totalPrice.toFixed(2) }}</span>
        </div>

        <div class="dialog-actions">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  Search, Refresh, CircleCheck, CircleClose, Document, Money, Phone
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CookerOrders',
  components: {
    Search,
    Refresh,
    CircleCheck,
    CircleClose,
    Document,
    Money,
    Phone
  },
  data() {
    return {
      // 搜索表单
      searchForm: {
        orderNo: '',
        status: '',
        dateRange: []
      },
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      // 订单详情对话框
      detailDialogVisible: false,
      currentOrder: null,
      // 模拟历史订单数据（只包含已完成和已取消）
      orders: [
        {
          id: 4,
          orderNo: '202403030002',
          status: 'completed',
          createTime: new Date('2024-03-03 12:15:00'),
          customerName: '周女士',
          customerPhone: '139****2222',
          address: '北京市海淀区 xxx 大厦 B 座 1205',
          remark: '口味清淡的厨师',
    
          totalQuantity: 2,
          totalPrice: 76
        },
        {
          id: 5,
          orderNo: '202403030003',
          status: 'cancelled',
          createTime: new Date('2024-03-03 13:15:00'),
          customerName: '王先生',
          customerPhone: '138****3333',
          address: '上海市浦东新区 xxx 大厦 C 座 1305',
          remark: '无',
          totalQuantity: 1,
          totalPrice: 28
        }
      ]
    }
  },
  computed: {
    // 订单统计
    stats() {
      const completedOrders = this.orders.filter(o => o.status === 'completed');
      const cancelledOrders = this.orders.filter(o => o.status === 'cancelled');
      return {
        completed: completedOrders.length,
        cancelled: cancelledOrders.length,
        total: this.orders.length,
        revenue: completedOrders.reduce((sum, o) => sum + o.totalPrice, 0)
      }
    },
    // 过滤后的订单列表（只显示历史订单）
    filteredOrders() {
      let result = this.orders;

      // 订单号搜索
      if (this.searchForm.orderNo) {
        result = result.filter(o => o.orderNo.includes(this.searchForm.orderNo));
      }

      // 状态筛选（只筛选已完成或已取消）
      if (this.searchForm.status) {
        result = result.filter(o => o.status === this.searchForm.status);
      }

      // 日期范围筛选
      if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
        const startDate = new Date(this.searchForm.dateRange[0]);
        const endDate = new Date(this.searchForm.dateRange[1]);
        endDate.setHours(23, 59, 59, 999);
        result = result.filter(o => {
          const orderDate = new Date(o.createTime);
          return orderDate >= startDate && orderDate <= endDate;
        });
      }

      // 分页
      this.pagination.total = result.length;
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
      const end = start + this.pagination.pageSize;

      return result.slice(start, end);
    }
  },
  methods: {
    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1;
      ElMessage.success('搜索完成');
    },
    // 重置
    handleReset() {
      this.searchForm = {
        orderNo: '',
        status: '',
        dateRange: []
      };
      this.pagination.currentPage = 1;
    },
    // 查看详情
    handleViewOrderDetail(order) {
      this.currentOrder = order;
      this.detailDialogVisible = true;
    },
    // 打电话
    handleCall(phone) {
      ElMessageBox.confirm(`拨打客户电话：${phone}？`, '提示', {
        confirmButtonText: '拨打',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        window.location.href = `tel:${phone}`;
      }).catch(() => { });
    },
    // 分页
    handleSizeChange() {
      this.pagination.currentPage = 1;
    },
    handleCurrentChange() { },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        completed: 'success',
        cancelled: 'info'
      };
      return typeMap[status] || '';
    },
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        completed: '已完成',
        cancelled: '已取消'
      };
      return nameMap[status] || '';
    },
    // 格式化时间
    formatTime(time) {
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;

      if (diff < 60000) return '刚刚';
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前';
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前';
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前';

      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  }
}
</script>

<style scoped>
.container {
   width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

h3 {
  margin: 20px 0;
  text-align: center;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.completed {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-icon.cancelled {
  background: linear-gradient(135deg, #cfd9df 0%, #e2ebf0 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

/* 订单列表 */
.order-list {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 16px;
}

.order-card :deep(.el-card__body) {
  padding: 16px;
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.order-info-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-size: 14px;
  color: #606266;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

/* 订单商品 */
.order-items {
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.item-spec {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.item-quantity {
  font-size: 14px;
  color: #606266;
  margin: 0 16px;
}

.item-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 500;
}

/* 订单信息 */
.order-info {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #909399;
  min-width: 80px;
}

.info-value {
  color: #606266;
  flex: 1;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.order-total {
  font-size: 14px;
  color: #606266;
}

.total-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 订单详情 */
.order-total-detail {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  font-size: 16px;
}

.order-total-detail .total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 16px;
}

/* 对话框操作按钮 */
.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>