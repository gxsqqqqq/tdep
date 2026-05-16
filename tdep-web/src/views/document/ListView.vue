<template>
  <section class="page-card">
    <div class="filter-bar document-filter">
      <el-input v-model="form.caseId" placeholder="案件ID" />
      <el-select v-model="form.documentType" placeholder="文书类型">
        <el-option label="判决书" value="JUDGEMENT" />
        <el-option label="起诉状" value="COMPLAINT" />
        <el-option label="开庭通知书" value="TRIAL_NOTICE" />
        <el-option label="举证通知书" value="EVIDENCE_NOTICE" />
      </el-select>
      <el-input v-model="form.caseStatus" placeholder="案件状态" />
      <PermissionButton permission="document:generate" type="primary" @click="generate">
        生成文书
      </PermissionButton>
    </div>

    <el-alert
      title="document-service 当前提供生成、预览、下载、签章接口，尚未提供文书分页列表接口；本页展示当前会话生成结果。"
      type="info"
      show-icon
      class="page-alert"
    />

    <el-table v-loading="loading" :data="documents" stripe>
      <el-table-column prop="documentNo" label="文书编号" min-width="220" />
      <el-table-column prop="title" label="标题" min-width="160" />
      <el-table-column prop="documentType" label="类型" width="140" />
      <el-table-column prop="status" label="状态" width="130">
        <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="generatedAt" label="生成时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.generatedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="preview(row.id)">PDF预览</el-button>
          <el-button link type="primary" @click="download(row.id)">下载</el-button>
          <PermissionButton permission="document:sign" link type="primary" @click="sign(row.id)">
            签章
          </PermissionButton>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'

import { downloadDocument, generateDocument, previewDocumentPdf, signDocument } from '@/api/document'
import type { DocumentItem } from '@/api/models'
import PermissionButton from '@/components/PermissionButton/index.vue'
import { formatDateTime } from '@/utils/formatter'
import { statusType } from '@/utils/status'

const loading = ref(false)
const documents = ref<DocumentItem[]>([])
const form = reactive({
  caseId: '',
  documentType: 'JUDGEMENT',
  caseStatus: 'JUDGEMENT_PENDING',
  title: '',
  exportPdf: true
})

async function generate() {
  loading.value = true
  try {
    const document = await generateDocument({
      ...form,
      caseId: Number(form.caseId),
      title: form.title || undefined,
      context: { caseId: form.caseId, generatedBy: 'tdep-web' }
    })
    documents.value.unshift(document)
    ElMessage.success('文书已生成')
  } finally {
    loading.value = false
  }
}

async function preview(id: number) {
  const result = await previewDocumentPdf(id)
  const url = result.downloadUrl || result.url
  if (url) window.open(url, '_blank')
}

async function download(id: number) {
  const result = await downloadDocument(id)
  const url = result.downloadUrl || result.url
  if (url) window.open(url, '_blank')
}

async function sign(id: number) {
  const document = await signDocument({ documentId: id })
  const index = documents.value.findIndex((item) => item.id === id)
  if (index >= 0) documents.value[index] = document
  ElMessage.success('文书已签章')
}
</script>
