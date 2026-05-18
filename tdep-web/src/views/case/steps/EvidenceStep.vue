<template>
  <div>
    <div class="card" style="padding: 24px; margin-bottom: 16px">
      <h3 style="margin-top: 0">上传证据</h3>
      <FileUploader
        :case-id="caseId"
        :uploading="evidenceStore.uploading"
        @upload="handleUpload"
      />
    </div>

    <div class="card" style="padding: 24px">
      <h3 style="margin-top: 0">已提交证据 ({{ evidenceStore.evidences.length }})</h3>
      <div v-loading="evidenceStore.loading" class="evidence-grid">
        <EvidenceCard
          v-for="item in evidenceStore.evidences"
          :key="item.id"
          :evidence="item"
          @preview="handlePreview"
          @download="handleDownload"
          @verify="handleVerify"
        />
        <el-empty v-if="!evidenceStore.loading && evidenceStore.evidences.length === 0" description="暂未上传证据" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

import EvidenceCard from '@/components/EvidenceCard/index.vue'
import FileUploader from '@/components/FileUploader/index.vue'
import type { EvidenceItem } from '@/api/models'
import { getEvidenceDownloadUrl } from '@/api/evidence'
import { useEvidenceStore } from '@/stores/evidence'
import { useAgentStore } from '@/stores/agent'

const route = useRoute()
const evidenceStore = useEvidenceStore()
const agentStore = useAgentStore()
const caseId = computed(() => route.params.id as string)

onMounted(async () => {
  if (caseId.value) {
    await evidenceStore.loadEvidences(caseId.value)
    agentStore.updateEvidenceCount(evidenceStore.evidences.length)
  }
})

async function handleUpload(file: File, description: string) {
  await evidenceStore.uploadEvidence(file, caseId.value, description)
  agentStore.updateEvidenceCount(evidenceStore.evidences.length)
  ElMessage.success('证据上传成功')
}

async function handlePreview(evidence: EvidenceItem) {
  const result = await getEvidenceDownloadUrl(evidence.id)
  const url = result.downloadUrl || result.url
  if (url) window.open(url, '_blank')
}

async function handleDownload(evidence: EvidenceItem) {
  const result = await getEvidenceDownloadUrl(evidence.id)
  const url = result.downloadUrl || result.url
  if (url) window.open(url, '_blank')
}

async function handleVerify(evidence: EvidenceItem) {
  await evidenceStore.verifyEvidence(evidence.id)
  ElMessage.success('证据校验通过')
}
</script>
