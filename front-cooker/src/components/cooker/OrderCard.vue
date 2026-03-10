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
  
  /* 新增：给卡片加内边距，避免内容贴边 */
}

/* 新增：菜品定制要求样式 */
.requirement-wrap {
  margin: 10px 0 0 0;
  /* 与上方地址行拉开间距 */
}

/* 新增：操作按钮样式 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  /* 按钮靠右对齐 */
  gap: 10px;
  /* 按钮间距 */
  margin-top: 20px;
  /* 与定制要求拉开间距 */
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  /* 分隔线，区分内容和操作区 */
}

/* 拒单按钮：文字按钮，浅灰色 */
.refuse-btn {
  padding: 6px 16px;
  font-size: 14px;
  color: #666;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.refuse-btn:hover {
  border-color: #ccc;
  background-color: #f5f5f5;
}

.accept-btn {
  padding: 6px 16px;
  font-size: 14px;
  color: #fff;
  background-color: #1890ff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.accept-btn:hover {
  background-color: #096dd9;
}

/* 折叠头部 */
.requirement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  /* 鼠标悬浮显示手型 */
  padding: 5px 0;
}

/* 折叠图标 */
.toggle-icon {
  font-size: 12px;
  color: #999;
  transition: transform 0.2s;
  /* 切换时轻微动画 */
}

/* 定制要求内容 */
.requirement-content {
  margin-top: 8px;
  padding: 10px;
  background-color: #f9f9f9;
  /* 浅灰背景区分内容 */
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  word-break: break-all;
  /* 超长内容换行 */
}

/* 新增：服务时间/地址通用样式 */
.order-detail-row {
  display: flex;
  /* 横向布局：图标+标签+值 */
  align-items: flex-start;
  /* 顶部对齐，适配地址换行 */
  gap: 8px;
  /* 元素间间距 */
  font-size: 14px;
  margin: 8px 0;
  /* 行间距 */
  line-height: 1.5;
  /* 地址换行时行高 */
}

/* 图标样式 */
.detail-icon {
  font-size: 14px;
  margin-top: 1px;
  /* 微调图标垂直位置，更居中 */
  white-space: nowrap;
  /* 防止图标换行 */
}

/* 标签样式（服务时间：/服务地址：） */
.detail-label {
  color: #666;
  /* 浅灰色，次要信息 */
  white-space: nowrap;
  /* 防止标签换行 */
}

/* 内容值样式（时间/地址） */
.detail-value {
  color: #333;
  /* 深色，主要信息 */
  flex: 1;
  /* 占满剩余空间，地址超长时自动换行 */
  word-break: break-all;
  /* 地址超长时强制换行，不溢出 */
}

/* 新增：客户姓名+电话样式 */
.customer-info {
  display: flex;
  /* 横向布局 */
  align-items: center;
  /* 垂直居中 */
  gap: 15px;
  /* 姓名和电话之间的间距 */
  font-size: 14px;
  /* 主文本大小 */
  margin: 8px 0;
  /* 与上下内容的间距 */
}

.customer-name {
  font-weight: 500;
  /* 姓名加粗，突出显示 */
  color: #333;
  /* 深色，醒目 */
}

.customer-phone {
  color: #666;
  /* 浅灰色，次要信息 */
  font-size: 13px;
  /* 略小于姓名 */
}

/* 通用flex布局（你的原有样式，保留） */
.flex-between {
  display: flex;
  justify-content: space-between;
  /* 核心：左右分开对齐 */
  align-items: center;
  /* 核心：垂直居中 */
}

/* 订单头部行样式（保留，补充width:100%） */
.order-header {
  width: 100%;
  /* 新增：确保占满卡片宽度，flex布局才能生效 */
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

/* 订单编号文字（保留） */
.order-id {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  /* 新增：防止长编号换行挤压 */
}

/* 状态标签通用样式（保留） */
.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
  /* 新增：防止标签换行 */
}

/* 待接单状态样式（保留） */
.status-tag.pending {
  color: #ff4757;
  background-color: #fff0f1;
  border: 1px solid #ffccd0;
}

/* 菜品清单区域 */
.dishes-section {
  margin: 15px 0;
  padding: 12px;
  background-color: #fafafa;
  border-radius: 4px;
}

/* 区域标题 */
.section-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e0e0e0;
}

/* 单个菜品项 */
.dish-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
  border-bottom: 1px dashed #f0f0f0;
}

.dish-item:last-child {
  border-bottom: none;
}

/* 菜品名称 */
.dish-name {
  color: #333;
  font-weight: 500;
  flex: 1;
}

/* 菜品规格 */
.dish-spec {
  color: #999;
  font-size: 12px;
  margin: 0 10px;
}

/* 菜品数量 */
.dish-quantity {
  color: #666;
  margin: 0 10px;
}

/* 菜品价格 */
.dish-price {
  color: #ff4757;
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
  color: #666;
}

/* 总价金额 */
.total-price {
  font-size: 20px;
  color: #ff4757;
  font-weight: bold;
  margin-left: 10px;
}
</style>