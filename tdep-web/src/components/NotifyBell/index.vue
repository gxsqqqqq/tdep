<template>
  <el-popover placement="bottom-end" width="360" trigger="click" popper-class="notify-popover">
    <template #reference>
      <el-badge :value="notifyStore.unreadCount" :hidden="notifyStore.unreadCount === 0">
        <el-button :icon="Bell" circle />
      </el-badge>
    </template>

    <div class="notify-panel">
      <div class="notify-panel__header">
        <strong>消息通知</strong>
        <el-tag size="small" :type="notifyStore.socketStatus === 'OPEN' ? 'success' : 'info'">
          {{ notifyStore.socketStatus }}
        </el-tag>
      </div>
      <el-empty v-if="notifyStore.messages.length === 0" description="暂无通知" />
      <div v-for="message in notifyStore.messages.slice(0, 5)" :key="message.messageId" class="notify-item">
        <strong>{{ message.title }}</strong>
        <p>{{ message.content }}</p>
        <small>{{ message.timestamp }}</small>
      </div>
      <div v-if="notifyStore.messages.length > 0" style="text-align: center; padding-top: 8px">
        <el-button link type="primary" @click="$router.push('/notifications')">查看全部</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { Bell } from '@element-plus/icons-vue'

import { useNotifyStore } from '@/stores/notify'

const notifyStore = useNotifyStore()
</script>
