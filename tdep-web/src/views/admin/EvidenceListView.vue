<template>
  <section class="page-card">
    <div class="filter-bar evidence-filter">
      <el-input v-model="query.keyword" clearable placeholder="文件名关键词" />
      <el-input v-model="query.caseId" clearable placeholder="案件ID" />
      <el-select v-model="query.status" clearable placeholder="状态">
        <el-option label="已上传" value="UPLOADED" />
        <el-option label="已固化" value="SEALED" />
        <el-option label="已冻结" value="FROZEN" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="loadEvidence">查询</el-button>
    </div>

    <el-table v-loading="loading" :data="records" stripe>
      <el-table-column prop="evidenceNo" label="证据编号" min-width="170" />
      <el-table-column prop="fileName" label="文件名" min-width="220" show-overflow-tooltip />
      <el-table-column prop="caseId" label="案件ID" width="100" />
      <el-table-column prop="fileSize" label="大小" width="110">
        <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="uploadedAt" label="上传时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.uploadedAt) }}</template>
      </el-table-column>
      <el-table-column label="Hash" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">{{ row.hash?.hashValue || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="download(row.id)">预览</el-button>
          <PermissionButton permission="evidence:verify" link type="primary" @click="verify(row.id)">
            校验
          </PermissionButton>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.pageNo"
      v-model:page-size="pagination.pageSize"
      class="table-pagination"
      layout="total, sizes, prev, pager, next"
      :total="pagination.total"
      @change="loadEvidence"
    />
  </section>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'

import { getEvidenceDownloadUrl, getEvidenceList, verifyEvidence } from '@/api/evidence'
import type { EvidenceItem } from '@/api/models'
import PermissionButton from '@/components/PermissionButton/index.vue'
import { formatDateTime } from '@/utils/formatter'
import { formatFileSize, statusType } from '@/utils/status'

const loading = ref(false)
const records = ref<EvidenceItem[]>([])
const query = reactive<{ keyword: string; caseId: string; status: string }>({ keyword: '', caseId: '', status: '' })
const pagination = reactive({ pageNo: 1, pageSize: 10, total: 0 })

onMounted(loadEvidence)

async function loadEvidence() {
  loading.value = true
  try {
    const page = await getEvidenceList({ ...query, pageNo: pagination.pageNo, pageSize: pagination.pageSize })
    records.value = page.records || []
    pagination.total = page.total || 0
  } finally {
    loading.value = false
  }
}

async function verify(id: number) {
  await verifyEvidence(id)
  ElMessage.success('证据校验已通过')
  loadEvidence()
}

async function download(id: number) {
  const result = await getEvidenceDownloadUrl(id)
  const url = result.downloadUrl || result.url
  if (url) window.open(url, '_blank')
}
</script>
