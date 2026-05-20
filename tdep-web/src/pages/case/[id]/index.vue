<template>
  <div class="case-detail-page">
    <div v-if="workflowStore.isLoading" class="flex items-center justify-center py-20">
      <div class="text-center">
        <el-icon class="is-loading animate-spin" :size="64" color="#409EFF"><Loading /></el-icon>
        <p class="mt-4 text-gray-500 text-lg font-medium">正在加载案件信息...</p>
        <p class="mt-2 text-gray-400 text-sm">从服务器获取最新数据</p>
      </div>
    </div>

    <div v-else-if="workflowStore.error && !workflowStore.currentCase" class="text-center py-20">
      <div class="max-w-md mx-auto">
        <div class="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <el-icon :size="48" color="#F56C6C"><CircleCloseFilled /></el-icon>
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">加载失败</h3>
        <p class="text-red-500 mb-6">{{ workflowStore.error }}</p>
        <div class="space-x-4">
          <el-button type="primary" @click="retryLoad">
            <el-icon><Refresh /></el-icon>
            重新加载
          </el-button>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </div>
    </div>

    <template v-else-if="workflowStore.currentCase">
      <CaseDetailHeader :case-info="workflowStore.currentCase" />

      <div class="max-w-[1400px] mx-auto px-6 pb-8 space-y-6">
        <CaseTimeline />

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-6">
            <WorkflowActionPanel />
            <EvidenceUploadPanel />
            <EvidenceChainViewer />
            <TrialRecordsPanel />
            <DocumentsPanel />
          </div>

          <div class="space-y-6">
            <AIGuidePanel />
            <CurrentTodoPanel />
            <OperationLogsPanel />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Loading, CircleCloseFilled, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'

import CaseDetailHeader from './components/CaseDetailHeader.vue'
import CaseTimeline from './components/CaseTimeline.vue'
import WorkflowActionPanel from './components/WorkflowActionPanel.vue'
import EvidenceUploadPanel from './components/EvidenceUploadPanel.vue'
import EvidenceChainViewer from './components/EvidenceChainViewer.vue'
import AIGuidePanel from './components/AIGuidePanel.vue'
import CurrentTodoPanel from './components/CurrentTodoPanel.vue'
import TrialRecordsPanel from './components/TrialRecordsPanel.vue'
import DocumentsPanel from './components/DocumentsPanel.vue'
import OperationLogsPanel from './components/OperationLogsPanel.vue'

const route = useRoute()
const workflowStore = useCaseWorkflowStore()

async function loadCaseData(caseId: string) {
  try {
    await workflowStore.fetchCaseDetail(caseId)
    ElMessage.success('案件信息加载成功')
  } catch (err) {
    console.error('加载案件失败:', err)
  }
}

function retryLoad() {
  const caseId = route.params.id as string
  if (caseId) {
    loadCaseData(caseId)
  }
}

onMounted(() => {
  const caseId = route.params.id as string
  if (caseId) {
    loadCaseData(caseId)
  }
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    loadCaseData(newId as string)
  }
})
</script>

<style scoped>
.case-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

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
