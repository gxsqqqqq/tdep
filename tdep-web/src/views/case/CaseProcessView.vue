<template>
  <div v-loading="caseStore.detailLoading">
    <div class="page-title">
      <h1>
        <el-button text @click="$router.push('/my-cases')">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        {{ caseStore.currentCase?.caseTitle || '案件详情' }}
      </h1>
      <el-tag v-if="caseStore.currentCase" :type="statusType(caseStore.currentCase.status)">
        {{ getCaseStatusName(caseStore.currentCase.status) }}
      </el-tag>
    </div>

    <div v-if="caseStore.currentCase" style="margin-bottom: 16px; color: var(--text-secondary); font-size: 13px">
      {{ caseStore.currentCase.caseNo }}
      <template v-if="caseStore.currentCase.courtName"> | {{ caseStore.currentCase.courtName }}</template>
    </div>

    <StepIndicator
      :active="caseStore.currentStep"
      :steps="steps"
      @step-click="onStepClick"
    />

    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue'
import { onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import StepIndicator from '@/components/StepIndicator/index.vue'
import { useCaseStore } from '@/stores/case'
import { getCaseStatusName, statusType } from '@/utils/status'

const route = useRoute()
const router = useRouter()
const caseStore = useCaseStore()

const steps = [
  { title: '立案信息' },
  { title: '缴费' },
  { title: '举证' },
  { title: '文书' },
  { title: '进度' }
]

const stepRoutes = ['CaseFiling', 'CasePayment', 'CaseEvidence', 'CaseDocuments', 'CaseTimeline']

function onStepClick(index: number) {
  if (index <= caseStore.currentStep) {
    const caseId = route.params.id
    router.push({ name: stepRoutes[index], params: { id: caseId } })
  }
}

onMounted(() => {
  const id = route.params.id
  if (id) {
    caseStore.loadCaseDetail(id as string)
  }
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    caseStore.loadCaseDetail(newId as string)
  }
})
</script>
