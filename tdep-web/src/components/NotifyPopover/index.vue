<template>
  <el-popover placement="bottom-end" width="360" trigger="click" popper-class="notify-popover">
    <template #reference>
      <el-badge :value="notifyStore.unreadCount" :hidden="notifyStore.unreadCount === 0">
        <el-button :icon="Bell" circle />
      </el-badge>
    </template>

    <div class="notify-panel">
      <div class="notify-panel__header">
        <strong>通知中心</strong>
        <el-tag size="small" :type="notifyStore.socketStatus === 'OPEN' ? 'success' : 'info'">
          {{ notifyStore.socketStatus }}
        </el-tag>
      </div>
      <el-empty v-if="notifyStore.messages.length === 0" description="暂无实时通知" />
      <div v-for="message in notifyStore.messages.slice(0, 6)" :key="message.messageId" class="notify-item">
        <strong>{{ message.title }}</strong>
        <p>{{ message.content }}</p>
        <small>{{ message.timestamp }}</small>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { Bell } from '@element-plus/icons-vue'

import { useNotifyStore } from '@/stores/notify'

const notifyStore = useNotifyStore()
</script>
