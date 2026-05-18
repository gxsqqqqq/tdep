<template>
  <div class="case-card card" @click="$emit('click')">
    <div class="case-card__header">
      <span class="case-card__no">{{ caseData.caseNo }}</span>
      <el-tag :type="statusType(caseData.status)" size="small">{{ getCaseStatusName(caseData.status) }}</el-tag>
    </div>
    <h3 class="case-card__title">{{ caseData.caseTitle }}</h3>
    <p class="case-card__cause">{{ caseData.causeAction || '-' }}</p>
    <div class="case-progress">
      <template v-for="i in totalSteps" :key="i">
        <div
          class="case-progress__dot"
          :class="{
            'case-progress__dot--done': i - 1 < currentStep,
            'case-progress__dot--active': i - 1 === currentStep
          }"
        />
        <div v-if="i < totalSteps" class="case-progress__line" :class="{ 'case-progress__line--done': i - 1 < currentStep }" />
      </template>
    </div>
    <div class="case-card__footer">
      <span class="case-card__time">{{ formatDateTime(caseData.createdAt) }}</span>
      <span class="case-card__link">查看详情 →</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CaseItem } from '@/api/models'
import { formatDateTime } from '@/utils/formatter'
import { getCaseStatusName, statusType } from '@/utils/status'

const props = defineProps<{
  caseData: CaseItem
  currentStep: number
  totalSteps?: number
}>()

defineEmits<{
  click: []
}>()

const totalSteps = props.totalSteps || 5
</script>
