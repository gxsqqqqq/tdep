<template>
  <div class="case-timeline-card">
    <div class="timeline-card-header">
      <div class="flex items-center gap-2">
        <el-icon :size="20" color="#409EFF"><Guide /></el-icon>
        <h3 class="text-base font-bold text-gray-800">案件生命周期</h3>
      </div>
      <div class="flex items-center gap-3">
        <span class="text-xs text-gray-400">整体进度</span>
        <el-progress
          :percentage="progressPercentage"
          :stroke-width="8"
          :show-text="false"
          :color="progressColor"
          style="width: 120px"
        />
        <span class="text-sm font-bold" :style="{ color: progressColor }">{{ progressPercentage }}%</span>
      </div>
    </div>

    <div class="timeline-wrapper">
      <div class="timeline-track">
        <div
          v-for="(step, index) in timelineSteps"
          :key="step.status"
          class="timeline-step"
          :class="stepStateClass(index)"
          @click="selectStep(index)"
        >
          <div class="step-node-wrapper">
            <div class="connector-line connector-left" :class="connectorClass(index, 'left')" />
            <div
              class="step-node"
              :class="nodeClass(index)"
            >
              <template v-if="isCompletedStep(index)">
                <el-icon :size="18" color="#fff"><CircleCheckFilled /></el-icon>
              </template>
              <template v-else-if="isCurrentStep(index)">
                <el-icon :size="18" color="#fff"><component :is="stepIcon(step.icon)" /></el-icon>
              </template>
              <template v-else>
                <span class="step-order">{{ index + 1 }}</span>
              </template>
            </div>
            <div class="connector-line connector-right" :class="connectorClass(index, 'right')" />
          </div>

          <div class="step-label-group">
            <span class="step-name" :class="{ 'font-bold': isCurrentStep(index) }">{{ step.label }}</span>
            <el-tag
              v-if="isCurrentStep(index)"
              size="small"
              type="primary"
              round
              effect="dark"
              class="current-badge"
            >当前阶段</el-tag>
          </div>
        </div>
      </div>
    </div>

    <transition name="slide-fade" mode="out-in">
      <div :key="selectedStepIndex" class="step-detail-panel">
        <div class="detail-panel-inner">
          <div class="detail-status-row">
            <div
              class="detail-dot"
              :class="{
                'dot-completed': isCompletedStep(selectedStepIndex),
                'dot-current': isCurrentStep(selectedStepIndex),
                'dot-pending': !isCompletedStep(selectedStepIndex) && !isCurrentStep(selectedStepIndex),
              }"
            />
            <h4 class="detail-title">{{ selectedStep?.label }}</h4>
            <el-tag
              size="small"
              :type="selectedStepTagType"
              round
              effect="plain"
            >
              {{ selectedStepStateLabel }}
            </el-tag>
          </div>
          <p class="detail-description">{{ selectedStep?.description }}</p>
          <div v-if="isCurrentStep(selectedStepIndex)" class="detail-actions">
            <div class="action-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>{{ workflowStore.currentStateDefinition.description }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  Guide,
  CircleCheckFilled,
  InfoFilled,
  Edit,
  Clock,
  CircleCheck,
  FolderOpened,
  Calendar,
  Gavel,
  DocumentChecked,
  CircleClose,
} from '@element-plus/icons-vue'
import { useCaseWorkflowStore } from '@/lib/stores/caseWorkflow'
import { CASE_STATES, getStatusTimeline, CaseStatus } from '@/lib/constants/caseLifecycle'

const workflowStore = useCaseWorkflowStore()

const timelineSteps = computed(() => getStatusTimeline())

const currentStepIndex = computed(() => workflowStore.currentStepIndex)

const progressPercentage = computed(() => workflowStore.progressPercentage)

const progressColor = computed(() => {
  if (workflowStore.isCompleted) return '#10b981'
  return '#409EFF'
})

const selectedStepIndex = ref(currentStepIndex.value)

const selectedStep = computed(() => timelineSteps.value[selectedStepIndex.value])

const selectedStepTagType = computed<'success' | 'primary' | 'info'>(() => {
  if (isCompletedStep(selectedStepIndex.value)) return 'success'
  if (isCurrentStep(selectedStepIndex.value)) return 'primary'
  return 'info'
})

const selectedStepStateLabel = computed(() => {
  if (isCompletedStep(selectedStepIndex.value)) return '已完成'
  if (isCurrentStep(selectedStepIndex.value)) return '进行中'
  return '未到达'
})

function isCompletedStep(index: number): boolean {
  return index < currentStepIndex.value
}

function isCurrentStep(index: number): boolean {
  return index === currentStepIndex.value
}

function stepStateClass(index: number): string {
  if (isCompletedStep(index)) return 'step--completed'
  if (isCurrentStep(index)) return 'step--current'
  return 'step--pending'
}

function nodeClass(index: number): string {
  const base = 'step-node'
  if (isCompletedStep(index)) return `${base} node--completed`
  if (isCurrentStep(index)) return `${base} node--current`
  return `${base} node--pending`
}

function connectorClass(index: number, side: 'left' | 'right'): string {
  const base = `conn-${side}`
  if (isCompletedStep(index)) return `${base} conn--completed`
  if (isCurrentStep(index)) {
    return side === 'left' ? `${base} conn--completed` : `${base} conn--current`
  }
  return `${base} conn--pending`
}

function stepIcon(iconName: string) {
  const iconMap: Record<string, any> = {
    Edit,
    Clock,
    CircleCheck,
    FolderOpened,
    Calendar,
    Gavel,
    DocumentChecked,
    CircleClose,
  }
  return iconMap[iconName] || Edit
}

function selectStep(index: number) {
  selectedStepIndex.value = index
}
</script>

<style scoped>
.case-timeline-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.timeline-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-bottom: 1px solid #f0f2f5;
}

.timeline-wrapper {
  padding: 32px 24px 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.timeline-wrapper::-webkit-scrollbar {
  height: 4px;
}

.timeline-wrapper::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 2px;
}

.timeline-track {
  display: flex;
  align-items: flex-start;
  min-width: max-content;
  position: relative;
}

.timeline-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  min-width: 100px;
  max-width: 150px;
  cursor: pointer;
  position: relative;
  transition: transform 0.25s ease;
}

.timeline-step:hover {
  transform: translateY(-2px);
}

.step-node-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  position: relative;
  z-index: 2;
}

.step-node {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 3;
}

.node--completed {
  background: linear-gradient(135deg, #10b981, #059669);
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.35);
}

.node--current {
  background: linear-gradient(135deg, #409EFF, #337ecc);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
  animation: pulse-ring 2s ease-in-out infinite;
}

.node--pending {
  background: #fff;
  border: 2.5px dashed #dcdfe6;
  color: #c0c4cc;
}

@keyframes pulse-ring {
  0% { box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4), 0 0 0 0 rgba(64, 158, 255, 0.3); }
  50% { box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4), 0 0 0 10px rgba(64, 158, 255, 0.05); }
  100% { box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4), 0 0 0 0 rgba(64, 158, 255, 0.3); }
}

.step-order {
  font-size: 13px;
  font-weight: 700;
}

.connector-line {
  height: 3px;
  flex: 1;
  border-radius: 2px;
  transition: background-color 0.4s ease, opacity 0.4s ease;
  position: relative;
  top: 0;
}

.connector-left {
  margin-right: 6px;
}

.connector-right {
  margin-left: 6px;
}

.conn--completed {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.conn--current {
  background: linear-gradient(90deg, #409EFF, #79bbff);
  position: relative;
}

.conn--current::after {
  content: '';
  position: absolute;
  top: -3px;
  left: 0;
  right: 0;
  bottom: -3px;
  background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.15), transparent);
  animation: shimmer 2s ease-in-out infinite;
}

@keyframes shimmer {
  0% { opacity: 0; transform: translateX(-100%); }
  50% { opacity: 1; }
  100% { opacity: 0; transform: translateX(100%); }
}

.conn--pending {
  background: #e4e7ed;
  opacity: 0.6;
}

.step-label-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  margin-top: 12px;
}

.step-name {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
  transition: color 0.25s ease;
  line-height: 1.3;
}

.step--completed .step-name {
  color: #10b981;
}

.step--current .step-name {
  color: #409EFF;
}

.current-badge {
  font-size: 10px;
  animation: badge-pulse 2s ease-in-out infinite;
}

@keyframes badge-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.step-detail-panel {
  margin: 0 24px 20px;
  border-top: 1px solid #f0f2f5;
  padding-top: 18px;
}

.detail-panel-inner {
  padding: 16px 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border: 1px solid #e8ecf1;
}

.detail-status-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.detail-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-completed { background: #10b981; }

.dot-current {
  background: #409EFF;
  animation: dot-blink 1.5s ease-in-out infinite;
}

.dot-pending { background: #dcdfe6; }

@keyframes dot-blink {
  0%, 100% { opacity: 1; box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2); }
  50% { opacity: 0.7; box-shadow: 0 0 0 6px rgba(64, 158, 255, 0.08); }
}

.detail-title {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}

.detail-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
  margin: 0 0 8px;
}

.detail-actions {
  margin-top: 10px;
}

.action-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.06);
  padding: 8px 12px;
  border-radius: 8px;
  border-left: 3px solid #409EFF;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 768px) {
  .timeline-step {
    min-width: 72px;
    max-width: 90px;
  }

  .step-node {
    width: 36px;
    height: 36px;
  }

  .step-name {
    font-size: 11px;
  }

  .timeline-card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
