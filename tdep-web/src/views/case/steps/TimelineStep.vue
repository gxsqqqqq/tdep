<template>
  <div class="card" style="padding: 24px">
    <h3 style="margin-top: 0">案件进度</h3>
    <TimelineLog :processes="processes" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import TimelineLog from '@/components/TimelineLog/index.vue'
import { useCaseStore } from '@/stores/case'

const caseStore = useCaseStore()

const processes = computed(() => {
  const detail = caseStore.currentCase
  if (!detail?.processes || detail.processes.length === 0) {
    return [{
      timestamp: detail?.createdAt,
      action: '案件已创建',
      status: detail?.status
    }]
  }
  return detail.processes
})
</script>
