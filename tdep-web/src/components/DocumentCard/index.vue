<template>
  <div class="document-card card">
    <div class="document-card__header">
      <span class="document-card__icon">📜</span>
      <span class="document-card__title">{{ document.title }}</span>
      <el-tag :type="statusType(document.status)" size="small">{{ getDocumentStatusName(document.status) }}</el-tag>
    </div>
    <div class="document-card__meta">
      <span>编号: {{ document.documentNo }}</span>
      <span>类型: {{ document.documentType }}</span>
      <span v-if="document.generatedAt">生成: {{ formatDateTime(document.generatedAt) }}</span>
      <span v-if="document.signedAt">签章: {{ formatDateTime(document.signedAt) }}</span>
    </div>
    <div class="document-card__actions">
      <el-button size="small" type="primary" @click="$emit('preview', document)">PDF 预览</el-button>
      <el-button size="small" @click="$emit('download', document)">下载</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DocumentItem } from '@/api/models'
import { formatDateTime } from '@/utils/formatter'
import { getDocumentStatusName, statusType } from '@/utils/status'

defineProps<{
  document: DocumentItem
}>()

defineEmits<{
  preview: [doc: DocumentItem]
  download: [doc: DocumentItem]
}>()
</script>
