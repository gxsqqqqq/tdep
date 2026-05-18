<template>
  <div class="timeline-log">
    <el-empty v-if="!processes || processes.length === 0" description="暂无流程记录" />
    <div v-for="(item, index) in processes" :key="index" class="timeline-log__item" :class="{ 'timeline-log__item--done': index > 0 }">
      <div class="timeline-log__dot" />
      <div class="timeline-log__time">{{ formatDateTime(item.timestamp) }}</div>
      <div class="timeline-log__content">{{ item.action }}</div>
      <div class="timeline-log__meta">
        <span v-if="item.operator">操作人: {{ item.operator }}</span>
        <span v-if="item.status"> | 状态: {{ getCaseStatusName(item.status) }}</span>
      </div>
      <div v-if="item.comment" class="timeline-log__meta">意见: {{ item.comment }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatDateTime } from '@/utils/formatter'
import { getCaseStatusName } from '@/utils/status'

defineProps<{
  processes: Array<{
    timestamp?: string
    action?: string
    operator?: string
    status?: string
    comment?: string
  }>
}>()
</script>
