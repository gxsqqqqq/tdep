<template>
  <div>
    <div class="page-title">
      <h1>消息通知</h1>
      <el-button type="primary" link @click="readAll">全部已读</el-button>
    </div>

    <div v-loading="loading" class="card" style="padding: 0">
      <div class="notification-list">
        <div
          v-for="item in messages"
          :key="item.id || item.messageId"
          class="notification-item"
          :class="{ 'notification-item--unread': !item.readAt }"
          @click="markRead(item)"
        >
          <div class="notification-item__dot" :class="{ 'notification-item__dot--read': item.readAt }" />
          <div class="notification-item__body">
            <div class="notification-item__title">{{ item.title }}</div>
            <div class="notification-item__content">{{ item.content }}</div>
            <div class="notification-item__time">{{ formatDateTime(item.createdAt || item.timestamp) }}</div>
          </div>
        </div>
        <el-empty v-if="!loading && messages.length === 0" description="暂无通知" />
      </div>
    </div>
  </div>
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

interface DisplayMessage {
  id?: number
  messageId?: number
  type?: string
  title: string
  content: string
  readAt?: string
  createdAt?: string
  timestamp?: string
}

const messages = computed<DisplayMessage[]>(() => {
  const realtime: DisplayMessage[] = notifyStore.messages.map((item) => ({
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
  if (!id || row.readAt) return
  await markMessageRead(id)
  loadMessages()
}

async function readAll() {
  await markAllRead()
  notifyStore.clearUnread()
  ElMessage.success('全部已读')
  loadMessages()
}
</script>
