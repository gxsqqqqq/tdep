<template>
  <div class="card" style="padding: 24px">
    <h3 style="margin-top: 0">法律文书</h3>

    <el-alert
      title="文书列表接口待后端提供"
      description="当前仅展示本次会话中生成的文书。当文书列表接口就绪后将自动加载案件关联的全部文书。"
      type="info"
      show-icon
      style="margin-bottom: 16px"
    />

    <div v-loading="documentStore.loading">
      <div v-for="doc in documentStore.documents" :key="doc.id" style="margin-bottom: 12px">
        <DocumentCard
          :document="doc"
          @preview="handlePreview"
          @download="handleDownload"
        />
      </div>
      <el-empty v-if="documentStore.documents.length === 0" description="暂无可查看的文书" />
    </div>
  </div>
</template>

<script setup lang="ts">
import DocumentCard from '@/components/DocumentCard/index.vue'
import type { DocumentItem } from '@/api/models'
import { useDocumentStore } from '@/stores/document'

const documentStore = useDocumentStore()

function handlePreview(doc: DocumentItem) {
  documentStore.previewDocument(doc.id)
}

function handleDownload(doc: DocumentItem) {
  documentStore.downloadDocument(doc.id)
}
</script>
