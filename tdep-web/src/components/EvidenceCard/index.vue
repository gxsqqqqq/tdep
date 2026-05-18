<template>
  <div class="evidence-card card">
    <div class="evidence-card__header">
      <span class="evidence-card__icon">{{ fileIcon }}</span>
      <span class="evidence-card__name">{{ evidence.fileName }}</span>
      <el-tag :type="statusType(evidence.status)" size="small">{{ getEvidenceStatusName(evidence.status) }}</el-tag>
    </div>
    <div class="evidence-card__meta">
      <span>{{ formatFileSize(evidence.fileSize) }}</span>
      <span>{{ formatDateTime(evidence.uploadedAt) }}</span>
    </div>
    <div v-if="evidence.hash?.hashValue" class="evidence-card__hash">
      {{ evidence.hash.hashAlgorithm || 'SHA256' }}: {{ evidence.hash.hashValue }}
    </div>
    <div class="evidence-card__actions">
      <el-button size="small" @click="$emit('preview', evidence)">预览</el-button>
      <el-button size="small" @click="$emit('download', evidence)">下载</el-button>
      <el-button size="small" @click="$emit('verify', evidence)">校验</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { EvidenceItem } from '@/api/models'
import { formatDateTime } from '@/utils/formatter'
import { formatFileSize, getEvidenceStatusName, getFileIcon, statusType } from '@/utils/status'

const props = defineProps<{
  evidence: EvidenceItem
}>()

defineEmits<{
  preview: [evidence: EvidenceItem]
  download: [evidence: EvidenceItem]
  verify: [evidence: EvidenceItem]
}>()

const fileIcon = computed(() => getFileIcon(props.evidence.fileExt))
</script>
