<template>
  <el-card shadow="hover" class="workflow-action-panel">
    <template #header>
      <div class="flex items-center gap-2">
        <span class="text-lg">📋</span>
        <span class="font-semibold text-gray-800">工作流操作</span>
      </div>
    </template>

    <div v-if="workflowStore.isTransitioning" class="flex items-center justify-center py-12">
      <el-icon class="is-loading mr-3" :size="32" color="#409EFF"><Loading /></el-icon>
      <span class="text-gray-500">正在执行操作...</span>
    </div>

    <div v-else-if="availableActions.length === 0" class="empty-state text-center py-8">
      <el-icon :size="48" color="#c0c4cc"><CircleCheck /></el-icon>
      <p class="mt-3 text-gray-400">当前阶段无需操作</p>
    </div>

    <div v-else class="action-content">
      <div v-if="primaryActions.length > 0" class="primary-actions mb-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div
            v-for="action in primaryActions"
            :key="action.transition"
            class="action-card primary-action"
          >
            <el-button
              :type="getButtonType(action)"
              size="large"
              class="w-full !h-auto !py-4"
              :loading="workflowStore.isTransitioning"
              @click="handleAction(action)"
            >
              <div class="flex flex-col items-center gap-2">
                <el-icon :size="24"><component :is="getActionIcon(action.transition)" /></el-icon>
                <span class="font-medium">{{ action.label }}</span>
              </div>
            </el-button>
            <p class="action-description mt-2 text-sm text-gray-500 text-center">{{ action.description }}</p>
          </div>
        </div>
      </div>

      <div v-if="secondaryActions.length > 0" class="secondary-actions">
        <div class="border-t border-gray-200 pt-4">
          <p class="text-xs text-gray-400 mb-3 uppercase tracking-wider">其他操作</p>
          <div class="flex flex-wrap gap-2">
            <el-button
              v-for="action in secondaryActions"
              :key="action.transition"
              :type="getButtonType(action)"
              size="default"
              :loading="workflowStore.isTransitioning"
              @click="handleAction(action)"
            >
              <el-icon class="mr-1"><component :is="getActionIcon(action.transition)" /></el-icon>
              {{ action.label }}
            </el-button>
          </div>
          <div class="mt-3 space-y-1">
            <p
              v-for="action in secondaryActions"
              :key="'desc-' + action.transition"
              class="text-xs text-gray-400 pl-1"
            >
              {{ action.label }}：{{ action.description }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Loading,
  CircleCheck,
  Upload,
  CircleCheckFilled,
  CircleCloseFilled,
  FolderOpened,
  Calendar,
  Gavel,
  DocumentChecked,
  SwitchButton,
} from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import type { CaseTransition } from '@/lib/constants/caseLifecycle'
import type { TransitionRule } from '@/lib/constants/caseLifecycle'

const workflowStore = useCaseWorkflowStore()

const availableActions = computed(() => workflowStore.availableActions)

const primaryActions = computed<TransitionRule[]>(() => {
  const dangerousTransitions: CaseTransition[] = [CaseTransition.REJECT]
  return availableActions.value.filter(
    (action) => !dangerousTransitions.includes(action.transition)
  )
})

const secondaryActions = computed<TransitionRule[]>(() => {
  const dangerousTransitions: CaseTransition[] = [CaseTransition.REJECT]
  return availableActions.value.filter((action) =>
    dangerousTransitions.includes(action.transition)
  )
})

function getActionIcon(transition: CaseTransition) {
  const iconMap: Record<CaseTransition, ReturnType<typeof () => unknown>> = {
    [CaseTransition.SUBMIT]: Upload,
    [CaseTransition.APPROVE]: CircleCheckFilled,
    [CaseTransition.REJECT]: CircleCloseFilled,
    [CaseTransition.START_EVIDENCE]: FolderOpened,
    [CaseTransition.COMPLETE_EVIDENCE]: CircleCheck,
    [CaseTransition.SCHEDULE_TRIAL]: Calendar,
    [CaseTransition.START_TRIAL]: Gavel,
    [CaseTransition.COMPLETE_TRIAL]: DocumentChecked,
    [CaseTransition.ISSUE_JUDGMENT]: DocumentChecked,
    [CaseTransition.CLOSE_CASE]: SwitchButton,
  }
  return iconMap[transition] || CircleCheck
}

function getButtonType(action: TransitionRule): '' | 'primary' | 'danger' | 'warning' | 'success' | 'info' {
  const dangerTransitions = [CaseTransition.REJECT]
  if (dangerTransitions.includes(action.transition)) {
    return 'danger'
  }
  return 'primary'
}

async function handleAction(transition: TransitionRule) {
  if (transition.confirmRequired) {
    try {
      await ElMessageBox.confirm(
        `确定要执行「${transition.label}」操作吗？\n\n${transition.description}`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: getButtonType(transition) === 'danger' ? 'warning' : 'info',
        }
      )
    } catch {
      return
    }
  }

  const success = await workflowStore.executeTransition(transition.transition)
  if (success) {
    ElMessage.success('操作成功')
  } else {
    ElMessage.error(workflowStore.error || '操作失败')
  }
}
</script>

<style scoped>
.workflow-action-panel {
  border-radius: 12px;
}

.action-card {
  @apply rounded-lg;
}

.primary-action .el-button {
  border-radius: 10px;
  transition: all 0.3s ease;
}

.primary-action .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.action-description {
  line-height: 1.4;
}

.empty-state {
  @apply text-gray-400;
}

@media (max-width: 640px) {
  .primary-actions .grid {
    grid-template-columns: 1fr;
  }
}
</style>
