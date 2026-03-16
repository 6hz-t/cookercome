<template>
  <el-card class="order-card">


    <div class="order-header flex-between">
      <span class="order-id">订单编号：{{ order.orderid || '暂无编号' }}</span>
      <!-- 订单总价 -->
      <div class="order-total">
        <span class="total-label">订单总价：</span>
        <span class="total-price">¥{{ order.totalprice || '0.00' }}</span>
      </div>
    </div>

    <div class="customer-info">
      <span>{{ order.username }}</span>
      <span>{{ order.userphone }}</span>
    </div>

    <div class="order-detail-row">
      <span class="detail-icon">🕒</span>
      <span class="detail-label">服务时间：</span>
      <span class="detail-value">{{ order.servetime || '暂无时间' }}</span>
    </div>


    <div class="order-detail-row">
      <span class="detail-icon">📍</span>
      <span class="detail-label">服务地址：</span>
      <span class="detail-value">{{ order.serveaddress || '暂无地址' }}</span>
    </div>





    <div class="requirement-wrap" v-if="order.requirement">
      <!-- 折叠/展开头部（点击切换） -->
      <div class="requirement-header" @click="showRequirement = !showRequirement">
        <span class="requirement-label">菜品定制要求</span>
        <span class="toggle-icon">{{ showRequirement ? '▲' : '▼' }}</span>
      </div>
      <!-- 定制要求内容（展开时显示） -->
      <div class="requirement-content">
        {{ order.requirement }}
      </div>
    </div>

    <div class="order-actions">
      <button class="refuse-btn" @click="handleRefuse">拒单</button>
      <button class="accept-btn" @click="handleAccept">接单</button>
    </div>

  </el-card>
</template>
<script>
import { timelineItemProps } from 'element-plus';


export default {
  name: 'OrderCard',
  props: {
    order: {
      type: Object,
      required: true
    }
  },
  components: {
  },
  data() {
    return {
    }
  },
  methods: {
    handleAccept() {
      this.$emit('accept', this.order);
    },
    handleReject() {
      this.$emit('reject', this.order);
    }
  },
  created() {
  },
  mounted() {
  }
}
</script>
<style scoped>
.order-card {
  width: 100%;
  border: none;
  box-shadow: 0 2px 12px rgba(74, 68, 62, 0.08);
  border-radius: 8px;
}

/* 新增：菜品定制要求样式 */
.requirement-wrap {
  margin: 10px 0 0 0;
}

/* 新增：操作按钮样式 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 10px;
  border-top: 1px solid var(--color-border);
}

/* 拒单按钮：文字按钮，浅灰色 */
.refuse-btn {
  padding: 6px 16px;
  font-size: 14px;
  color: var(--color-text-secondary);
  background-color: var(--color-bg-white);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.refuse-btn:hover {
  border-color: var(--color-text-light);
  background-color: var(--color-bg-mute);
}

.accept-btn {
  padding: 6px 16px;
  font-size: 14px;
  color: #fff;
  background-color: var(--color-accent);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.accept-btn:hover {
  background-color: var(--color-accent-hover);
}

/* 折叠头部 */
.requirement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 5px 0;
}

/* 折叠图标 */
.toggle-icon {
  font-size: 12px;
  color: var(--color-text-light);
  transition: transform 0.2s;
}

/* 定制要求内容 */
.requirement-content {
  margin-top: 8px;
  padding: 10px;
  background-color: var(--color-bg-soft);
  border-radius: 4px;
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.5;
  word-break: break-all;
}

/* 新增：服务时间/地址通用样式 */
.order-detail-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  margin: 8px 0;
  line-height: 1.5;
}

/* 图标样式 */
.detail-icon {
  font-size: 14px;
  margin-top: 1px;
  white-space: nowrap;
}

/* 标签样式（服务时间：/服务地址：） */
.detail-label {
  color: var(--color-text-secondary);
  white-space: nowrap;
}

/* 内容值样式（时间/地址） */
.detail-value {
  color: var(--color-text-primary);
  flex: 1;
  word-break: break-all;
}

/* 新增：客户姓名 + 电话样式 */
.customer-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
  margin: 8px 0;
}

.customer-name {
  font-weight: 500;
  color: var(--color-text-primary);
}

.customer-phone {
  color: var(--color-text-secondary);
  font-size: 13px;
}

/* 通用 flex 布局（你的原有样式，保留） */
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 订单头部行样式（保留，补充 width:100%） */
.order-header {
  width: 100%;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border);
}

/* 订单编号文字（保留） */
.order-id {
  font-size: 12px;
  color: var(--color-text-light);
  white-space: nowrap;
}

/* 状态标签通用样式（保留） */
.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

/* 待接单状态样式（保留） */
.status-tag.pending {
  color: var(--color-danger);
  background-color: #fff0f1;
  border: 1px solid #ffccd0;
}

/* 菜品清单区域 */
.dishes-section {
  margin: 15px 0;
  padding: 12px;
  background-color: var(--color-bg-soft);
  border-radius: 4px;
}

/* 区域标题 */
.section-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--color-border);
}

/* 单个菜品项 */
.dish-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  border-bottom: 1px dashed var(--color-border-light);
}

.dish-item:last-child {
  border-bottom: none;
}

/* 菜品名称 */
.dish-name {
  color: var(--color-text-primary);
  font-weight: 500;
  flex: 1;
}

/* 菜品规格 */
.dish-spec {
  color: var(--color-text-secondary);
  font-size: 12px;
  margin: 0 10px;
}

/* 菜品数量 */
.dish-quantity {
  color: var(--color-text-secondary);
  margin: 0 10px;
}

/* 菜品价格 */
.dish-price {
  color: var(--color-accent);
  font-weight: 500;
  min-width: 70px;
  text-align: right;
}

/* 订单总价区域 */
.order-total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 12px 0;
  margin-top: 10px;
}

/* 总价标签 */
.total-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* 总价金额 */
.total-price {
  font-size: 20px;
  color: var(--color-danger);
  font-weight: bold;
  margin-left: 10px;
}
</style>
