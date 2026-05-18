<template>
  <div class="case-dashboard" v-loading="caseStore.detailLoading">
    <!-- Back button -->
    <button class="back-btn" @click="$router.push('/my-cases')">
      ← 返回我的案件
    </button>

    <template v-if="caseStore.currentCase">
      <!-- Status Hero -->
      <CaseStatusHero
        :case-title="caseStore.currentCase.caseTitle"
        :case-no="caseStore.currentCase.caseNo"
        :status="caseStore.currentCase.status"
        :court-name="caseStore.currentCase.courtName"
        :created-at="formatDateTime(caseStore.currentCase.createdAt)"
      />

      <!-- Process Timeline -->
      <div class="card" style="padding: 20px 24px; margin-bottom: 20px">
        <ProcessTimeline
          :steps="stepNames"
          :current="agentStore.context.currentStep"
        />
      </div>

      <!-- Current Task Card -->
      <CurrentTaskCard
        :task="currentTask"
        @action="handleTaskAction"
      />

      <!-- Agent Inline Hint -->
      <InlineHint
        v-if="agentHint"
        :type="agentHint.type"
        :title="agentHint.title"
        :content="agentHint.content"
        :action-label="agentHint.actionLabel"
        @action="handleHintAction(agentHint)"
      />

      <!-- Quick Actions -->
      <QuickActionGrid
        :actions="quickActions"
        @select="handleQuickAction"
      />

      <!-- Recent Activity -->
      <div class="card recent-activity">
        <div class="recent-activity__title">最近动态</div>
        <div v-if="recentActivities.length === 0" class="recent-activity__empty">
          暂无动态记录
        </div>
        <div
          v-for="(activity, idx) in recentActivities"
          :key="idx"
          class="recent-activity__item"
        >
          <span class="recent-activity__dot" />
          <span class="recent-activity__time">{{ activity.time }}</span>
          <span class="recent-activity__text">{{ activity.text }}</span>
        </div>
      </div>
    </template>

    <div v-else-if="!caseStore.detailLoading" class="card" style="padding: 48px; text-align: center">
      <p style="color: #9aa0a6">案件信息加载失败</p>
      <button class="back-btn" @click="$router.push('/my-cases')">返回我的案件</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import CaseStatusHero from '@/components/CaseStatusHero/index.vue'
import ProcessTimeline from '@/components/ProcessTimeline/index.vue'
import CurrentTaskCard from '@/components/CurrentTaskCard/index.vue'
import type { TaskInfo } from '@/components/CurrentTaskCard/index.vue'
import InlineHint from '@/components/InlineHint/index.vue'
import QuickActionGrid from '@/components/QuickActionGrid/index.vue'
import type { QuickAction } from '@/components/QuickActionGrid/index.vue'
import { useCaseStore } from '@/stores/case'
import { useEvidenceStore } from '@/stores/evidence'
import { useAgentStore } from '@/stores/agent'
import { formatDateTime } from '@/utils/formatter'
import { caseStatusNames } from '@/utils/status'

const route = useRoute()
const router = useRouter()
const caseStore = useCaseStore()
const evidenceStore = useEvidenceStore()
const agentStore = useAgentStore()

const stepNames = ['提交立案', '身份材料', '上传证据', '确认文书', '等待审理', '案件进度']

// ── Current Task ──

const currentTask = computed<TaskInfo | null>(() => {
  const c = caseStore.currentCase
  if (!c) return null

  const status = c.status

  if (status === 'DRAFT') {
    return {
      type: 'action',
      icon: '📝',
      title: '请填写立案信息',
      description: '您的案件尚未提交，请填写完整的立案信息后提交申请。',
      actionLabel: '填写立案信息'
    }
  }

  if (status === 'SUBMITTED' || status === 'FILING_REVIEW') {
    return {
      type: 'info',
      icon: '⏳',
      title: '立案审核中',
      description: '您的立案申请已提交，正在等待法院审核。审核结果将通过消息通知您。'
    }
  }

  if (status === 'FILING_REJECTED') {
    return {
      type: 'warning',
      icon: '❌',
      title: '立案被驳回',
      description: '您的立案申请未通过审核，请根据驳回原因修改后重新提交。',
      actionLabel: '重新提交'
    }
  }

  if (status === 'EVIDENCE_SUBMITTING') {
    const count = evidenceStore.evidences.length
    return {
      type: 'action',
      icon: '📎',
      title: '请上传证据材料',
      description: count === 0
        ? '您还没有上传任何证据，请尽快上传与案件相关的材料。'
        : `已上传 ${count} 份证据。建议补充更多材料以增强证据链完整性。`,
      progress: Math.min(count * 33, 100),
      progressText: `已上传 ${count} 份`,
      actionLabel: '上传证据'
    }
  }

  if (status === 'SCHEDULED') {
    return {
      type: 'info',
      icon: '📋',
      title: '案件已排期',
      description: '您的案件已安排庭审，请关注文书和通知中的开庭时间。'
    }
  }

  if (status === 'IN_TRIAL') {
    return {
      type: 'info',
      icon: '⚖️',
      title: '案件审理中',
      description: '您的案件正在审理中，请耐心等待。有新进展时会第一时间通知您。'
    }
  }

  if (status === 'JUDGED' || status === 'EFFECTIVE') {
    return {
      type: 'action',
      icon: '📜',
      title: '判决已下达',
      description: '法院已作出判决，请查看相关文书。',
      actionLabel: '查看文书'
    }
  }

  if (status === 'CLOSED') {
    return {
      type: 'info',
      icon: '✅',
      title: '案件已结案',
      description: '此案件已全部完结。您可以在文书页面查看和下载所有文书。'
    }
  }

  return null
})

// ── Agent Hint ──

const agentHint = computed(() => {
  const msg = agentStore.topMessage
  if (!msg) return null
  return {
    type: msg.type,
    title: msg.title,
    content: msg.content,
    actionLabel: msg.action?.label,
    route: msg.action?.route
  }
})

// ── Quick Actions ──

const quickActions = computed<QuickAction[]>(() => {
  const c = caseStore.currentCase
  if (!c) return []

  const actions: QuickAction[] = []

  actions.push({
    id: 'evidence',
    icon: '📎',
    label: '证据材料',
    route: `/case/${c.id}/evidence`,
    badge: evidenceStore.evidences.length > 0 ? evidenceStore.evidences.length : undefined,
    highlight: c.status === 'EVIDENCE_SUBMITTING'
  })

  actions.push({
    id: 'documents',
    icon: '📜',
    label: '查看文书',
    route: `/case/${c.id}/documents`,
    highlight: ['SCHEDULED', 'JUDGED', 'EFFECTIVE'].includes(c.status)
  })

  actions.push({
    id: 'timeline',
    icon: '📊',
    label: '案件时间线',
    route: `/case/${c.id}/timeline`
  })

  actions.push({
    id: 'info',
    icon: '📋',
    label: '立案信息',
    route: `/case/${c.id}/filing`
  })

  return actions
})

// ── Recent Activity ──

const recentActivities = computed(() => {
  const c = caseStore.currentCase
  if (!c?.processes || c.processes.length === 0) {
    if (c?.createdAt) {
      return [{
        time: formatDateTime(c.createdAt),
        text: '案件已创建'
      }]
    }
    return []
  }

  return (c.processes as Record<string, unknown>[])
    .slice(0, 5)
    .map((p) => ({
      time: formatDateTime(p.timestamp as string),
      text: (p.action as string) || '状态变更'
    }))
})

// ── Handlers ──

function handleTaskAction() {
  const c = caseStore.currentCase
  if (!c) return

  if (c.status === 'DRAFT' || c.status === 'FILING_REJECTED') {
    router.push('/case/create')
  } else if (c.status === 'EVIDENCE_SUBMITTING') {
    router.push(`/case/${c.id}/evidence`)
  } else if (c.status === 'JUDGED' || c.status === 'EFFECTIVE') {
    router.push(`/case/${c.id}/documents`)
  }
}

function handleHintAction(hint: { route?: string }) {
  if (hint.route) {
    router.push(hint.route)
  }
}

function handleQuickAction(action: QuickAction) {
  if (action.route) {
    router.push(action.route)
  }
}

// ── Lifecycle ──

async function loadCaseData() {
  const id = route.params.id as string
  if (!id) return

  await caseStore.loadCaseDetail(id)
  await evidenceStore.loadEvidences(id)

  if (caseStore.currentCase) {
    agentStore.initCaseContext(caseStore.currentCase, evidenceStore.evidences)
  }
}

onMounted(loadCaseData)

watch(() => route.params.id, (newId) => {
  if (newId) loadCaseData()
})
</script>

<style scoped>
.case-dashboard {
  max-width: 800px;
  margin: 0 auto;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: #5f6368;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 0;
  margin-bottom: 16px;
  transition: color 0.15s;
}

.back-btn:hover {
  color: #1a73e8;
}

.card {
  border-radius: 16px;
  background: white;
  border: 1px solid #e0e4e8;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.recent-activity {
  padding: 20px 24px;
}

.recent-activity__title {
  font-size: 14px;
  font-weight: 600;
  color: #5f6368;
  margin-bottom: 16px;
}

.recent-activity__empty {
  color: #9aa0a6;
  font-size: 13px;
  text-align: center;
  padding: 16px 0;
}

.recent-activity__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f1f3f4;
  font-size: 13px;
}

.recent-activity__item:last-child {
  border-bottom: none;
}

.recent-activity__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #1a73e8;
  flex-shrink: 0;
}

.recent-activity__time {
  color: #9aa0a6;
  white-space: nowrap;
  font-size: 12px;
}

.recent-activity__text {
  color: #202124;
}

:root.dark .card {
  background: #1e293b;
  border-color: #334155;
}

:root.dark .back-btn {
  color: #94a3b8;
}

:root.dark .back-btn:hover {
  color: #60a5fa;
}

:root.dark .recent-activity__text {
  color: #e2e8f0;
}
</style>
