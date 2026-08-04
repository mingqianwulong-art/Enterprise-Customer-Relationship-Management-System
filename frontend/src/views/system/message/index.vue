<template>
  <div class="app-container">
    <div class="page-header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-radio-group v-model="filterType" @change="loadMessages">
          <el-radio-button :label="''">全部</el-radio-button>
          <el-radio-button :label="1">跟进提醒</el-radio-button>
          <el-radio-button :label="2">商机预警</el-radio-button>
          <el-radio-button :label="3">系统通知</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="handleMarkAllRead" :disabled="!unreadCount">
          <el-icon><Check /></el-icon>全部已读
        </el-button>
      </div>
    </div>

    <el-card v-loading="loading">
      <div class="message-summary">
        <span>共 {{ messages.length }} 条消息</span>
        <el-tag type="danger" v-if="unreadCount > 0" size="small">{{ unreadCount }} 条未读</el-tag>
        <el-tag type="success" v-else size="small">全部已读</el-tag>
      </div>

      <el-divider />

      <div class="message-list" v-if="filteredMessages.length">
        <div
          v-for="msg in filteredMessages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.isRead === 0 }"
          @click="handleClickMessage(msg)"
        >
          <div class="message-icon">
            <el-badge :is-dot="msg.isRead === 0">
              <el-icon :size="24" :color="typeColor(msg.type)">
                <Bell v-if="msg.type === 1" />
                <Warning v-else-if="msg.type === 2" />
                <ChatDotRound v-else />
              </el-icon>
            </el-badge>
          </div>
          <div class="message-body">
            <div class="message-title-row">
              <span class="message-title">{{ msg.title }}</span>
              <el-tag size="small" :type="typeTagType(msg.type)">{{ typeText(msg.type) }}</el-tag>
            </div>
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ msg.createTime }}</div>
          </div>
          <div class="message-action" v-if="msg.isRead === 0">
            <el-button text type="primary" @click.stop="handleMarkRead(msg)">标为已读</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Bell, Warning, ChatDotRound } from '@element-plus/icons-vue'
import { getMessageList, markMessageRead, markAllMessagesRead } from '@/api/message'

const loading = ref(false)
const messages = ref<any[]>([])
const filterType = ref<number | ''>('')
const unreadCount = ref(0)

const filteredMessages = computed(() => {
  if (filterType.value === '') return messages.value
  return messages.value.filter((m) => m.type === filterType.value)
})

function typeText(type: number) {
  const map: Record<number, string> = { 1: '跟进提醒', 2: '商机预警', 3: '系统通知' }
  return map[type] || '其他'
}

function typeTagType(type: number): 'info' | 'warning' | 'success' {
  if (type === 1) return 'success'
  if (type === 2) return 'warning'
  return 'info'
}

function typeColor(type: number) {
  if (type === 1) return '#67c23a'
  if (type === 2) return '#e6a23c'
  return '#909399'
}

async function loadMessages() {
  loading.value = true
  try {
    const res: any = await getMessageList()
    messages.value = res.data || []
    unreadCount.value = messages.value.filter((m) => m.isRead === 0).length
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

async function handleMarkRead(msg: any) {
  try {
    await markMessageRead(msg.id)
    msg.isRead = 1
    unreadCount.value = messages.value.filter((m) => m.isRead === 0).length
    ElMessage.success('已标记已读')
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function handleMarkAllRead() {
  try {
    await markAllMessagesRead()
    messages.value.forEach((m) => (m.isRead = 1))
    unreadCount.value = 0
    ElMessage.success('全部已读')
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

function handleClickMessage(msg: any) {
  if (msg.isRead === 0) {
    handleMarkRead(msg)
  }
}

onMounted(() => loadMessages())
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}
.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.message-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}
.message-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.message-item:hover {
  background: #f5f7fa;
}
.message-item.unread {
  background: #ecf5ff;
}
.message-item.unread:hover {
  background: #e0eeff;
}
.message-icon {
  flex-shrink: 0;
  padding-top: 2px;
}
.message-body {
  flex: 1;
  min-width: 0;
}
.message-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.message-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.message-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 6px;
  word-break: break-all;
}
.message-time {
  font-size: 12px;
  color: #c0c4cc;
}
.message-action {
  flex-shrink: 0;
}
</style>
