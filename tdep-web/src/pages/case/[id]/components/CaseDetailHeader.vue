<template>
  <div class="case-detail-header" :class="statusGradientClass">
    <div class="max-w-[1400px] mx-auto px-6 py-8">
      <div class="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4 mb-5">
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-3 flex-wrap mb-3">
            <span class="header-case-no font-mono text-sm text-white/80 bg-white/10 px-3 py-1 rounded-md backdrop-blur-sm">
              {{ caseInfo?.caseNumber || '--' }}
            </span>
            <el-tag
              :type="statusTagType"
              size="large"
              round
              effect="dark"
              class="header-status-tag !border-white/30"
            >
              {{ statusLabel }}
            </el-tag>
          </div>
          <h1 class="header-title text-white text-xl md:text-2xl font-bold leading-snug truncate">
            {{ caseInfo?.title || '未命名案件' }}
          </h1>
        </div>
        <div v-if="!isCompleted" class="flex-shrink-0 hidden sm:block">
          <div class="header-progress-ring relative w-16 h-16">
            <svg class="w-full h-full -rotate-90" viewBox="0 0 36 36">
              <circle cx="18" cy="18" r="15.5" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="3" />
              <circle
                cx="18"
                cy="18"
                r="15.5"
                fill="none"
                stroke="white"
                stroke-width="3"
                :stroke-dasharray="`${progressPercentage}, 100`"
                stroke-linecap="round"
                class="transition-all duration-700 ease-out"
              />
            </svg>
            <span class="absolute inset-0 flex items-center justify-center text-xs font-bold text-white">
              {{ progressPercentage }}%
            </span>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4 mt-6 pt-5 border-t border-white/15">
        <div class="meta-item">
          <span class="meta-icon"><el-icon><User /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">原告</span>
            <span class="meta-value truncate">{{ caseInfo?.plaintiff || '--' }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><UserFilled /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">被告</span>
            <span class="meta-value truncate">{{ caseInfo?.defendant || '--' }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><Avatar /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">审判法官</span>
            <span class="meta-value truncate">{{ caseInfo?.judge || '待分配' }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><Stamp /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">书记员</span>
            <span class="meta-value truncate">{{ caseInfo?.clerk || '待分配' }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><Clock /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">创建时间</span>
            <span class="meta-value">{{ formatDateTime(caseInfo?.createdAt) }}</span>
          </div>
        </div>
        <div class="meta-item col-span-2 sm:col-span-1">
          <span class="meta-icon"><el-icon><RefreshRight /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">更新时间</span>
            <span class="meta-value">{{ formatDateTime(caseInfo?.updatedAt) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="header-decoration-blob header-blob-1"></div>
    <div class="header-decoration-blob header-blob-2"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { User, UserFilled, Avatar, Stamp, Clock, RefreshRight } from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import type { CaseInfo } from '@/lib/stores/caseWorkflow'
import { CASE_STATES, CaseStatus } from '@/lib/constants/caseLifecycle'

const props = defineProps<{
  caseInfo: CaseInfo | null
}>()

const workflowStore = useCaseWorkflowStore()

const currentStatus = computed(() => props.caseInfo?.status ?? workflowStore.currentStatus)

const stateDefinition = computed(() => CASE_STATES[currentStatus.value] ?? CASE_STATES[CaseStatus.DRAFT])

const statusLabel = computed(() => stateDefinition.value.label)

const isCompleted = computed(() => stateDefinition.value.isTerminal)

const progressPercentage = computed(() => workflowStore.progressPercentage)

const statusTagType = computed<'success' | 'warning' | 'danger' | 'info' | ''>(() => {
  const map: Partial<Record<CaseStatus, 'success' | 'warning' | 'danger' | 'info' | ''>> = {
    [CaseStatus.DRAFT]: 'info',
    [CaseStatus.PENDING_REVIEW]: 'warning',
    [CaseStatus.ACCEPTED]: '',
    [CaseStatus.EVIDENCE_CLOSING]: '',
    [CaseStatus.TRIAL_SCHEDULED]: '',
    [CaseStatus.IN_TRIAL]: 'warning',
    [CaseStatus.JUDGED]: 'success',
    [CaseStatus.CLOSED]: 'success',
    [CaseStatus.REJECTED]: 'danger',
  }
  return map[currentStatus.value] ?? ''
})

const statusGradientClass = computed(() => {
  const gradientMap: Partial<Record<CaseStatus, string>> = {
    [CaseStatus.DRAFT]: 'gradient-draft',
    [CaseStatus.PENDING_REVIEW]: 'gradient-pending-review',
    [CaseStatus.ACCEPTED]: 'gradient-accepted',
    [CaseStatus.EVIDENCE_CLOSING]: 'gradient-evidence-closing',
    [CaseStatus.TRIAL_SCHEDULED]: 'gradient-trial-scheduled',
    [CaseStatus.IN_TRIAL]: 'gradient-in-trial',
    [CaseStatus.JUDGED]: 'gradient-judged',
    [CaseStatus.CLOSED]: 'gradient-closed',
    [CaseStatus.REJECTED]: 'gradient-rejected',
  }
  return gradientMap[currentStatus.value] ?? 'gradient-default'
})

function formatDateTime(dateStr?: string): string {
  if (!dateStr) return '--'
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}
</script>

<style scoped>
.case-detail-header {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.gradient-default       { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.gradient-draft         { background: linear-gradient(135deg, #9ca3af 0%, #6b7280 50%, #4b5563 100%); }
.gradient-pending-review{ background: linear-gradient(135deg, #f59e0b 0%, #d97706 50%, #b45309 100%); }
.gradient-accepted      { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 50%, #1d4ed8 100%); }
.gradient-evidence-closing { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 50%, #4338ca 100%); }
.gradient-trial-scheduled { background: linear-gradient(135deg, #a855f7 0%, #9333ea 50%, #7e22ce 100%); }
.gradient-in-trial      { background: linear-gradient(135deg, #f97316 0%, #ea580c 50%, #c2410c 100%); }
.gradient-judged        { background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%); }
.gradient-closed        { background: linear-gradient(135deg, #475569 0%, #334155 50%, #1e293b 100%); }
.gradient-rejected      { background: linear-gradient(135deg, #ef4444 0%, #dc2626 50%, #b91c1c 100%); }

.header-title {
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-status-tag {
  letter-spacing: 0.5px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(8px);
  transition: background 0.25s ease;
}

.meta-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.meta-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  font-size: 15px;
  flex-shrink: 0;
}

.meta-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.meta-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.65);
  font-weight: 500;
  line-height: 1.2;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.meta-value {
  font-size: 13px;
  color: white;
  font-weight: 600;
  line-height: 1.3;
  margin-top: 2px;
}

.header-decoration-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.25;
  pointer-events: none;
}

.header-blob-1 {
  width: 280px;
  height: 280px;
  background: white;
  top: -80px;
  right: -40px;
}

.header-blob-2 {
  width: 200px;
  height: 200px;
  background: white;
  bottom: -60px;
  left: 20%;
}

@media (max-width: 1024px) {
  .header-blob-1 { width: 180px; height: 180px; top: -40px; right: -20px; }
  .header-blob-2 { width: 140px; height: 140px; bottom: -30px; left: 10%; }
}
</style>
