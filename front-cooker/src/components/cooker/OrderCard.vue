<template>
  <el-card class="order-card">
    <div class="order-header flex-between">
      <span class="order-id">订单编号：{{ order.orderNo || '暂无编号' }}</span>
      <div class="order-total">
        <span class="total-label">订单总价：</span>
        <span class="total-price">¥{{ order.totalprice || '0.00' }}</span>
      </div>
    </div>

    <div class="customer-info">
      用户:<span>{{ order.username }}</span>
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
      <div class="requirement-header" @click="showRequirement = !showRequirement">
        <span class="requirement-label">菜品定制要求</span>
        <span class="toggle-icon">{{ showRequirement ? '▲' : '▼' }}</span>
      </div>
      <div class="requirement-content" v-show="showRequirement">
        {{ order.requirement }}
      </div>
    </div>

    <div class="order-actions">
      <button class="refuse-btn" @click="handleRefuse">拒单</button>
      <button class="accept-btn" @click="showAcceptDialog = true">接单</button>
    </div>

    <!-- 优化布局但保留原配色的接单确认弹窗 -->
    <el-dialog
      v-model="showAcceptDialog"
      title="确认接单"
      width="580px"
      :close-on-click-modal="false"
      center
      class="accept-order-dialog"
    >
      <div class="accept-dialog-content">
        <!-- 订单摘要卡片 -->
        <div class="order-summary-card">
          <h3 class="summary-title">订单信息</h3>
          <div class="summary-list">
            <div class="summary-item">
              <span class="item-label">订单编号：</span>
              <span class="item-value">{{ order.orderid || '暂无编号' }}</span>
            </div>
            <div class="summary-item">
              <span class="item-label">服务时间：</span>
              <span class="item-value">{{ order.servetime || '暂无时间' }}</span>
            </div>
            <div class="summary-item">
              <span class="item-label">服务地址：</span>
              <span class="item-value">{{ order.serveaddress || '暂无地址' }}</span>
            </div>
            <div class="summary-item total-item">
              <span class="item-label">订单金额：</span>
              <span class="item-value">¥{{ order.totalprice || '0.00' }}</span>
            </div>
          </div>
        </div>

        <!-- 温馨提示 -->
        <div class="confirm-tips">
          <el-alert
            title="温馨提示"
            type="warning"
            :closable="false"
            show-icon
            class="tips-alert"
          >
            <template #default>
              <ul class="tips-list">
                <li>请确认您能够按时到达服务地点</li>
                <li>请提前与客户联系确认服务细节</li>
                <li>接单后请按时完成服务，否则会影响您的接单权限</li>
              </ul>
            </template>
          </el-alert>
        </div>

        <!-- 备注输入框 -->
        <div class="confirm-input">
          <el-form label-width="80px" class="remark-form">
            <el-form-item label="接单备注：">
              <el-input
                v-model="remark"
                type="textarea"
                :rows="4"
                placeholder="请输入接单备注（选填，如：已确认可按时服务）"
                maxlength="200"
                show-word-limit
                class="remark-input"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button 
            @click="showAcceptDialog = false"
            class="cancel-btn"
          >取消</el-button>
          <el-button 
            type="primary" 
            @click="handleConfirmAccept" 
            :loading="submitting"
            class="confirm-btn"
          >确认接单</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  name: 'OrderCard',
  props: {
    order: {
      type: Object,
      required: true
    // // {
    //                 orderNo: '20240316000000003',
    //                 orderid: 3,
    //                 username: '王芳',
    //                 userphone: '13700137000',
    //                 servetime: '2024-03-16 12:00:00',
    //                 serveaddress: '广州市天河区天河路 385 号太古汇一座 18 层',
    //                 requirement: '粤菜为主，少油少盐，需要提前 1 小时到达准备，5 人份',
    //                 totalprice: '268.00',
    //                 status: 'waiting'
    //             }
    }

  },
  data() {
    return {
      showAcceptDialog: false,
      showRequirement: true,
      remark: '',
      submitting: false
    }
  },
  methods: {
    handleRefuse() {
      this.$emit('reject', this.order);
    },
    handleConfirmAccept() {
      this.submitting = true;
      this.$emit('accept', {
        order: this.order,
        remark: this.remark
      });
      setTimeout(() => {
        this.submitting = false;
        this.showAcceptDialog = false;
        this.remark = '';
      }, 800);
    }
  }
}
</script>

<style scoped>
/* 基础样式完全保留原有配色 */
.order-card {
  width: 100%;
  border: none;
  box-shadow: 0 2px 12px rgba(74, 68, 62, 0.08);
  border-radius: 8px;
  padding: 20px;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-header {
  width: 100%;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border);
}

.order-id {
  font-size: 12px;
  color: var(--color-text-light);
  white-space: nowrap;
}

.order-total {
  display: flex;
  align-items: center;
}

.total-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.total-price {
  font-size: 20px;
  color: var(--color-danger);
  font-weight: bold;
  margin-left: 10px;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
  margin: 12px 0;
  color: var(--color-text-primary);
}

.order-detail-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 14px;
  margin: 8px 0;
  line-height: 1.5;
}

.detail-icon {
  font-size: 14px;
  margin-top: 1px;
  white-space: nowrap;
}

.detail-label {
  color: var(--color-text-secondary);
  white-space: nowrap;
}

.detail-value {
  color: var(--color-text-primary);
  flex: 1;
  word-break: break-all;
}

.requirement-wrap {
  margin: 15px 0 0 0;
}

.requirement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px 0;
}

.toggle-icon {
  font-size: 12px;
  color: var(--color-text-light);
  transition: transform 0.2s;
}

.requirement-content {
  margin-top: 8px;
  padding: 12px;
  background-color: var(--color-bg-soft);
  border-radius: 6px;
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.6;
  word-break: break-all;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid var(--color-border);
}

.refuse-btn {
  padding: 8px 20px;
  font-size: 14px;
  color: var(--color-text-secondary);
  background-color: var(--color-bg-white);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refuse-btn:hover {
  border-color: var(--color-text-light);
  background-color: var(--color-bg-mute);
}

.accept-btn {
  padding: 8px 20px;
  font-size: 14px;
  color: #fff;
  background-color: var(--color-accent);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.accept-btn:hover {
  background-color: var(--color-accent-hover);
}

/* 弹窗样式：仅优化布局，完全复用原有配色变量 */
.accept-order-dialog {
  --el-dialog-border-radius: 12px;
}

.accept-order-dialog :deep(.el-dialog__header) {
  padding: 20px 24px 16px;
  border-bottom: 1px solid var(--color-border);
}

.accept-order-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.accept-order-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
}

.accept-order-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px 20px;
  border-top: 1px solid var(--color-border);
}

.accept-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 订单摘要卡片 - 复用原有配色 */
.order-summary-card {
  background-color: var(--color-bg-soft);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid var(--color-border);
}

.summary-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--color-border-light);
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.item-label {
  color: var(--color-text-secondary);
  min-width: 80px;
  flex-shrink: 0;
}

.item-value {
  color: var(--color-text-primary);
  flex: 1;
  word-break: break-all;
}

.total-item {
  margin-top: 4px;
  padding-top: 8px;
  border-top: 1px dashed var(--color-border-light);
}

.total-item .item-value {
  color: var(--color-danger);
  font-weight: 600;
  font-size: 15px;
}

/* 提示样式 - 仅调整布局，保留组件原有样式 */
.confirm-tips {
  margin: 0;
}

.tips-alert {
  --el-alert-padding: 12px 16px;
  --el-alert-border-radius: 8px;
}

.tips-list {
  margin: 8px 0 0 0;
  padding-left: 20px;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 4px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 备注输入框 - 仅调整布局，保留原有配色 */
.confirm-input {
  margin: 0;
}

.remark-form {
  margin: 0;
}

.remark-input {
  --el-input-border-radius: 8px;
  --el-input-padding: 10px 12px;
  font-size: 14px;
  /* 保留Element默认配色，不覆盖 */
}

/* 弹窗按钮 - 复用原有配色 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  /* 保留Element默认按钮配色 */
}

.confirm-btn {
  padding: 8px 24px;
  border-radius: 8px;
  font-size: 14px;
  background-color: var(--color-accent);
  --el-button-hover-bg-color: var(--color-accent-hover);
}
</style>
