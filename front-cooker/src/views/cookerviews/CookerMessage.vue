<template>
  <div class="container">
    <h3>消息中心</h3>
    
    <!-- 消息分类标签 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部消息" name="all">
        <span class="badge">{{ totalCount }}</span>
      </el-tab-pane>
      <el-tab-pane label="未读消息" name="unread">
        <span class="badge">{{ unreadCount }}</span>
      </el-tab-pane>
      <el-tab-pane label="系统通知" name="system">
        <span class="badge">{{ systemCount }}</span>
      </el-tab-pane>
      <el-tab-pane label="订单消息" name="order">
        <span class="badge">{{ orderCount }}</span>
      </el-tab-pane>
    </el-tabs>

    <!-- 消息列表 -->
    <div class="message-list">
      <div 
        v-for="message in filteredMessages" 
        :key="message.id" 
        class="message-item"
        :class="{ 'is-unread': !message.isRead }"
        @click="handleMessageClick(message)"
      >
        <div class="message-left">
          <div class="message-icon" :class="getMessageIcon(message.type)">
            <el-icon :size="24">
              <component :is="getMessageIconComponent(message.type)" />
            </el-icon>
          </div>
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="message-title">{{ message.title }}</span>
            <span class="message-time">{{ formatTime(message.createTime) }}</span>
          </div>
          <div class="message-body">
            {{ message.content }}
          </div>
          <div class="message-footer">
            <el-tag :type="getMessageTagType(message.type)" size="small">
              {{ getMessageTypeName(message.type) }}
            </el-tag>
            <span v-if="!message.isRead" class="unread-dot"></span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
    </div>

    <!-- 批量操作 -->
    <div class="batch-operations" v-if="messages.length > 0">
      <el-button @click="markAllAsRead" :disabled="unreadCount === 0">
        全部已读
      </el-button>
      <el-button type="danger" @click="clearMessages" :disabled="messages.length === 0">
        清空消息
      </el-button>
    </div>
  </div>
</template>

<script>
import { Bell, Message, Warning, DocumentChecked } from '@element-plus/icons-vue'

export default {
  name: 'CookerMessage',
  components: {
    Bell,
    Message,
    Warning,
    DocumentChecked
  },
  data() {
    return {
      activeTab: 'all',
      // 模拟消息数据
      messages: [
        {
          id: 1,
          title: '新订单通知',
          content: '您有一个新的订单待处理，订单号：202403050001',
          type: 'order',
          isRead: false,
          createTime: new Date('2024-03-05 10:30:00')
        },
        {
          id: 2,
          title: '系统维护通知',
          content: '系统将于今晚 23:00-01:00 进行维护，请提前安排好时间',
          type: 'system',
          isRead: false,
          createTime: new Date('2024-03-05 09:00:00')
        },
        {
          id: 3,
          title: '订单已完成',
          content: '订单 202403040005 已完成，请确认收款',
          type: 'order',
          isRead: true,
          createTime: new Date('2024-03-04 18:20:00')
        },
        {
          id: 4,
          title: '账户安全提醒',
          content: '您的账户密码强度较弱，建议及时修改',
          type: 'system',
          isRead: true,
          createTime: new Date('2024-03-04 14:00:00')
        },
        {
          id: 5,
          title: '新订单通知',
          content: '您有一个新的订单待处理，订单号：202403040002',
          type: 'order',
          isRead: false,
          createTime: new Date('2024-03-04 11:30:00')
        },
        {
          id: 6,
          title: '活动通知',
          content: '平台推出新活动，完成任务可获得优惠券奖励',
          type: 'system',
          isRead: false,
          createTime: new Date('2024-03-03 16:00:00')
        }
      ]
    }
  },
  computed: {
    // 过滤后的消息列表
    filteredMessages() {
      if (this.activeTab === 'all') {
        return this.messages;
      }
      if (this.activeTab === 'unread') {
        return this.messages.filter(m => !m.isRead);
      }
      return this.messages.filter(m => m.type === this.activeTab);
    },
    // 消息统计
    totalCount() {
      return this.messages.length;
    },
    unreadCount() {
      return this.messages.filter(m => !m.isRead).length;
    },
    systemCount() {
      return this.messages.filter(m => m.type === 'system').length;
    },
    orderCount() {
      return this.messages.filter(m => m.type === 'order').length;
    }
  },
  methods: {
    // 标签页切换
    handleTabClick(tab) {
      console.log('切换到标签页:', tab.props.name);
    },
    // 点击消息
    handleMessageClick(message) {
      // 标记为已读
      if (!message.isRead) {
        message.isRead = true;
      }
      // 显示消息详情（可以弹出对话框或跳转）
      this.$message.success(`查看消息：${message.title}`);
    },
    // 全部标记为已读
    markAllAsRead() {
      this.messages.forEach(m => m.isRead = true);
      this.$message.success('全部标记为已读');
    },
    // 清空消息
    clearMessages() {
      this.$confirm('确定要清空所有消息吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.messages = [];
        this.$message.success('已清空消息');
      }).catch(() => {});
    },
    // 获取消息图标类名
    getMessageIcon(type) {
      const iconMap = {
        system: 'icon-system',
        order: 'icon-order',
        notice: 'icon-notice'
      };
      return iconMap[type] || 'icon-default';
    },
    // 获取消息图标组件
    getMessageIconComponent(type) {
      const componentMap = {
        system: DocumentChecked,
        order: Message,
        notice: Bell
      };
      return componentMap[type] || Bell;
    },
    // 获取消息类型名称
    getMessageTypeName(type) {
      const nameMap = {
        system: '系统通知',
        order: '订单消息',
        notice: '通知'
      };
      return nameMap[type] || '消息';
    },
    // 获取消息标签类型
    getMessageTagType(type) {
      const typeMap = {
        system: 'info',
        order: 'success',
        notice: 'warning'
      };
      return typeMap[type] || 'info';
    },
    // 格式化时间
    formatTime(time) {
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      
      // 1 分钟内
      if (diff < 60000) {
        return '刚刚';
      }
      // 1 小时内
      if (diff < 3600000) {
        return Math.floor(diff / 60000) + '分钟前';
      }
      // 24 小时内
      if (diff < 86400000) {
        return Math.floor(diff / 3600000) + '小时前';
      }
      // 7 天内
      if (diff < 604800000) {
        return Math.floor(diff / 86400000) + '天前';
      }
      // 超过 7 天显示日期
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    }
  }
}
</script>

<style scoped>
.container {
  width: 1000px;
  margin: 0 auto;
}

h3 {
  margin: 20px 0;
  text-align: center;
}

/* 标签页徽章 */
.badge {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}

/* 消息列表 */
.message-list {
  margin-top: 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 20px;
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.message-item.is-unread {
  background-color: #ecf5ff;
  border-color: #d9ecff;
}

.message-left {
  margin-right: 16px;
}

.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.message-icon.icon-system {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-icon.icon-order {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.message-icon.icon-notice {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.message-icon.icon-default {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
}

.message-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background-color: #f56c6c;
  border-radius: 50%;
}

/* 批量操作 */
.batch-operations {
  margin-top: 20px;
  text-align: center;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.batch-operations .el-button {
  margin: 0 10px;
}
</style>