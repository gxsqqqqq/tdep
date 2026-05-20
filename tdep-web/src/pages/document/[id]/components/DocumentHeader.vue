<template>
  <div class="document-detail-header" :class="statusGradientClass">
    <div class="max-w-[1400px] mx-auto px-6 py-8">
      <div class="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-4 mb-5">
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-3 flex-wrap mb-3">
            <span class="header-doc-no font-mono text-sm text-white/80 bg-white/10 px-3 py-1 rounded-md backdrop-blur-sm cursor-pointer hover:bg-white/20 transition-colors" @click="copyDocumentNumber">
              {{ document?.documentNumber || '--' }}
              <el-icon class="ml-1 text-xs"><CopyDocument /></el-icon>
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
            {{ document?.title || '未命名文书' }}
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

      <div class="grid grid-cols-2 sm:grid-cols-3 gap-4 mt-6 pt-5 border-t border-white/15">
        <div class="meta-item">
          <span class="meta-icon"><el-icon><Document /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">文书类型</span>
            <span class="meta-value truncate">{{ documentTypeLabel }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><FolderOpened /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">关联案件</span>
            <router-link
              v-if="document?.caseId"
              :to="`/cases/${document.caseId}`"
              class="meta-value truncate hover:text-blue-200 transition-colors underline decoration-white/30 hover:decoration-blue-200"
            >
              查看案件 →
            </router-link>
            <span v-else class="meta-value truncate">--</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><User /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">创建者</span>
            <span class="meta-value truncate">{{ document?.author || '--' }}</span>
          </div>
        </div>
        <div class="meta-item">
          <span class="meta-icon"><el-icon><Clock /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">创建时间</span>
            <span class="meta-value">{{ formatDateTime(document?.createdAt) }}</span>
          </div>
        </div>
        <div class="meta-item col-span-2 sm:col-span-1">
          <span class="meta-icon"><el-icon><RefreshRight /></el-icon></span>
          <div class="meta-content">
            <span class="meta-label">更新时间</span>
            <span class="meta-value">{{ formatDateTime(document?.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <div v-if="availableActions.length > 0" class="action-bar mt-6 pt-5 border-t border-white/15">
        <div class="flex flex-wrap gap-3">
          <template v-for="action in availableActions" :key="action.transition">
            <el-button
              :type="getActionButtonType(action)"
              :loading="isProcessing"
              round
              size="large"
              class="action-btn"
              @click="handleAction(action)"
            >
              <el-icon class="mr-1"><component :is="getActionIcon(action.transition)" /></el-icon>
              {{ action.label }}
            </el-button>
          </template>
        </div>
      </div>
    </div>

    <div class="header-decoration-blob header-blob-1"></div>
    <div class="header-decoration-blob header-blob-2"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  CopyDocument,
  Document,
  FolderOpened,
  User,
  Clock,
  RefreshRight,
  MagicStick,
  EditPen,
  Stamp,
  Download,
  View,
} from '@element-plus/icons-vue'
import { useDocumentWorkflowStore, type DocumentInfo } from '@/lib/stores/documentWorkflow'
import type { DocumentTransitionRule } from '@/lib/constants/documentLifecycle'
import { DocumentStatus, DocumentTransition } from '@/lib/constants/documentLifecycle'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps<{
  document: DocumentInfo | null
}>()

const emit = defineEmits<{
  (e: 'action', transition: DocumentTransition): void
}>()

const workflowStore = useDocumentWorkflowStore()

const currentStatus = computed(() => props.document?.status ?? workflowStore.currentStatus)

const isCompleted = computed(() => workflowStore.isCompleted)

const progressPercentage = computed(() => workflowStore.progressPercentage)

const availableActions = computed<DocumentTransitionRule[]>(() => workflowStore.availableActions)

const statusLabel = computed(() => workflowStore.currentStateDefinition.label)

const statusTagType = computed<'success' | 'warning' | 'danger' | 'info' | ''>(() => {
  const map: Partial<Record<DocumentStatus, 'success' | 'warning' | 'danger' | 'info' | ''>> = {
    [DocumentStatus.DRAFT]: 'info',
    [DocumentStatus.GENERATED]: '',
    [DocumentStatus.SIGNED]: 'success',
    [DocumentStatus.ARCHIVED]: 'success',
  }
  return map[currentStatus.value] ?? ''
})

const statusGradientClass = computed(() => {
  const gradientMap: Partial<Record<DocumentStatus, string>> = {
    [DocumentStatus.DRAFT]: 'gradient-draft',
    [DocumentStatus.GENERATED]: 'gradient-generated',
    [DocumentStatus.SIGNED]: 'gradient-signed',
    [DocumentStatus.ARCHIVED]: 'gradient-archived',
  }
  return gradientMap[currentStatus.value] ?? 'gradient-default'
})

const documentTypeLabel = computed(() => {
  const typeMap: Record<string, string> = {
    indictment: '起诉状',
    defense: '答辩状',
    evidence_list: '证据清单',
    court_record: '庭审记录',
    judgement: '判决书',
    settlement: '调解书',
    other: '其他文书',
  }
  if (!props.document?.documentType) return '--'
  return typeMap[props.document.documentType] || props.document.documentType
})

const isProcessing = computed(() => workflowStore.isProcessing)

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

function copyDocumentNumber(): void {
  if (props.document?.documentNumber) {
    navigator.clipboard.writeText(props.document.documentNumber).then(() => {
      ElMessage.success('文书编号已复制到剪贴板')
    })
  }
}

function getActionButtonType(action: DocumentTransitionRule): 'primary' | 'warning' | 'success' | 'info' | 'default' {
  const warningActions = [DocumentTransition.REVIEW]
  const successActions = [DocumentTransition.ARCHIVE]
  if (warningActions.includes(action.transition)) return 'warning'
  if (successActions.includes(action.transition)) return 'success'
  return 'primary'
}

function getActionIcon(transition: DocumentTransition) {
  const iconMap: Record<DocumentTransition, any> = {
    [DocumentTransition.GENERATE]: MagicStick,
    [DocumentTransition.REVIEW]: EditPen,
    [DocumentTransition.SIGN]: Stamp,
    [DocumentTransition.ARCHIVE]: Download,
  }
  return iconMap[transition] || Document
}

async function handleAction(action: DocumentTransitionRule): Promise<void> {
  if (action.confirmRequired) {
    try {
      await ElMessageBox.confirm(
        action.description || `确定要执行"${action.label}"操作吗？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
    } catch {
      return
    }
  }

  emit('action', action.transition)
}
</script>

<style scoped>
.document-detail-header {
  position: relative;
  overflow: hidden;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.gradient-default   { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.gradient-draft     { background: linear-gradient(135deg, #9ca3af 0%, #6b7280 50%, #4b5563 100%); }
.gradient-generated { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 50%, #1d4ed8 100%); }
.gradient-signed    { background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%); }
.gradient-archived  { background: linear-gradient(135deg, #475569 0%, #334155 50%, #1e293b 100%); }

.header-title {
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-status-tag {
  letter-spacing: 0.5px;
}

.header-doc-no {
  letter-spacing: 0.3px;
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

.action-btn {
  backdrop-filter: blur(4px);
  border-color: rgba(255, 255, 255, 0.3) !important;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
