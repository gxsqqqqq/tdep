<template>
  <div class="document-detail-page min-h-screen bg-gray-50">
    <div v-if="documentStore.isLoading" class="flex items-center justify-center py-20">
      <div class="text-center">
        <el-icon class="is-loading animate-spin" :size="64" :color="tokens.colors.judicial[600]"><Loading /></el-icon>
        <p class="mt-4 text-lg font-medium" :style="{ color: tokens.colors.neutral[500] }">正在加载文书信息...</p>
        <p class="mt-2 text-sm" :style="{ color: tokens.colors.neutral[400] }">从服务器获取最新数据</p>
      </div>
    </div>

    <div v-else-if="documentStore.error && !documentStore.currentDocument" class="text-center py-20">
      <div class="max-w-md mx-auto">
        <div class="w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6" :style="{ backgroundColor: tokens.colors.danger[100] }">
          <el-icon :size="48" :color="tokens.colors.danger[600]"><CircleCloseFilled /></el-icon>
        </div>
        <h3 class="text-xl font-semibold mb-2" :style="{ color: tokens.colors.neutral[800] }">加载失败</h3>
        <p class="mb-6" :style="{ color: tokens.colors.danger[500] }">{{ documentStore.error }}</p>
        <div class="space-x-4">
          <el-button type="primary" @click="retryLoad">
            <el-icon><Refresh /></el-icon>
            重新加载
          </el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </div>
    </div>

    <template v-else-if="documentStore.currentDocument">
      <DocumentHeader />

      <div class="max-w-[1400px] mx-auto px-4 sm:px-6 pb-8 space-y-6">
        <DocumentTimeline />

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-6">
            <PdfPreviewPanel />
          </div>

          <div class="space-y-6">
            <ElectronicSealPanel />
            <AuditLogPanel />
          </div>
        </div>

        <VersionHistoryPanel />

        <DownloadCenter />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Loading, CircleCloseFilled, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useDocumentWorkflowStore } from '@/lib/stores/documentWorkflow'
import { JudicialDesignTokens } from '@/design-system/judicial-design-tokens'

import DocumentHeader from './components/DocumentHeader.vue'
import DocumentTimeline from './components/DocumentTimeline.vue'
import PdfPreviewPanel from './components/PdfPreviewPanel.vue'
import ElectronicSealPanel from './components/ElectronicSealPanel.vue'
import AuditLogPanel from './components/AuditLogPanel.vue'
import VersionHistoryPanel from './components/VersionHistoryPanel.vue'
import DownloadCenter from './components/DownloadCenter.vue'

const route = useRoute()
const documentStore = useDocumentWorkflowStore()
const tokens = JudicialDesignTokens

async function loadDocumentData(docId: string) {
  try {
    await documentStore.fetchDocumentDetail(docId)
    ElMessage.success('文书信息加载成功')
  } catch (err) {
    console.error('加载文书失败:', err)
  }
}

function retryLoad() {
  const docId = route.params.id as string
  if (docId) {
    loadDocumentData(docId)
  }
}

onMounted(() => {
  const docId = route.params.id as string
  if (docId) {
    loadDocumentData(docId)
  }
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    loadDocumentData(newId as string)
  }
})
</script>

<style scoped>
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}
</style>
