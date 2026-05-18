<template>
  <section class="page-card">
    <div class="panel-header">
      <h2>通知中心</h2>
      <div>
        <el-tag type="success">WebSocket: {{ notifyStore.socketStatus }}</el-tag>
        <el-button type="primary" link @click="loadMessages">刷新</el-button>
        <el-button type="primary" link @click="readAll">全部已读</el-button>
      </div>
    </div>

    <el-alert :title="`未读通知：${notifyStore.unreadCount}`" type="info" show-icon class="page-alert" />

    <el-table v-loading="loading" :data="messages" stripe>
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column label="类型" width="150">
        <template #default="{ row }">{{ row.notifyType || row.type || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }"><el-tag :type="row.readAt ? 'success' : 'warning'">{{ row.readAt ? '已读' : '未读' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt || row.timestamp) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" :disabled="Boolean(row.readAt)" @click="markRead(row)">已读</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'

import { getNotifyMessages, getUnreadCount, markAllRead, markMessageRead } from '@/api/notify'
import type { NotifyMessageItem } from '@/api/models'
import { useNotifyStore } from '@/stores/notify'
import { formatDateTime } from '@/utils/formatter'

const loading = ref(false)
const serverMessages = ref<NotifyMessageItem[]>([])
const notifyStore = useNotifyStore()

const messages = computed(() => {
  const realtime = notifyStore.messages.map((item) => ({
    messageId: item.messageId,
    type: item.type,
    title: item.title,
    content: item.content,
    timestamp: item.timestamp
  }))
  return [...realtime, ...serverMessages.value]
})

onMounted(loadMessages)

async function loadMessages() {
  loading.value = true
  try {
    const [list, unread] = await Promise.allSettled([
      getNotifyMessages({ pageNo: 1, pageSize: 50 }),
      getUnreadCount()
    ])
    if (list.status === 'fulfilled') {
      serverMessages.value = Array.isArray(list.value) ? list.value : list.value.records || []
    }
    if (unread.status === 'fulfilled') {
      notifyStore.unreadCount = unread.value
    }
  } finally {
    loading.value = false
  }
}

async function markRead(row: NotifyMessageItem) {
  const id = row.id || row.messageId
  if (!id) return
  await markMessageRead(id)
  ElMessage.success('已标记为已读')
  loadMessages()
}

async function readAll() {
  await markAllRead()
  notifyStore.clearUnread()
  ElMessage.success('全部已读')
  loadMessages()
}
</script>
