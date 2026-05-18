<template>
  <Transition name="task-card">
    <div v-if="task" class="task-card" :class="`task-card--${task.type}`">
      <div class="task-card__pulse" />
      <div class="task-card__body">
        <div class="task-card__header">
          <span class="task-card__icon">{{ task.icon }}</span>
          <span class="task-card__label">当前任务</span>
        </div>
        <h3 class="task-card__title">{{ task.title }}</h3>
        <p v-if="task.description" class="task-card__desc">{{ task.description }}</p>
        <div v-if="task.progress !== undefined" class="task-card__progress">
          <div class="task-card__progress-bar">
            <div class="task-card__progress-fill" :style="{ width: task.progress + '%' }" />
          </div>
          <span class="task-card__progress-text">{{ task.progressText }}</span>
        </div>
        <button
          v-if="task.actionLabel"
          class="task-card__action"
          @click="$emit('action')"
        >
          {{ task.actionLabel }} →
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
export interface TaskInfo {
  type: 'action' | 'warning' | 'info'
  icon: string
  title: string
  description?: string
  progress?: number
  progressText?: string
  actionLabel?: string
}

defineProps<{
  task: TaskInfo | null
}>()

defineEmits<{
  action: []
}>()
</script>

<style scoped>
.task-card {
  position: relative;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  overflow: hidden;
  border: 2px solid transparent;
}

.task-card--action  { background: linear-gradient(135deg, #f0f7ff, #e8f0fe); border-color: #a8c7fa; }
.task-card--warning { background: linear-gradient(135deg, #fff5f5, #fef0f0); border-color: #f5c6c6; }
.task-card--info    { background: linear-gradient(135deg, #f0fff4, #e8fbee); border-color: #a8e6b8; }

.task-card__pulse {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 16px;
  border: 2px solid currentColor;
  opacity: 0;
  animation: task-pulse 2s ease-in-out infinite;
}

.task-card--action .task-card__pulse  { color: #1a73e8; }
.task-card--warning .task-card__pulse { color: #ea4335; }
.task-card--info .task-card__pulse    { color: #34a853; }

.task-card__body {
  position: relative;
  z-index: 1;
}

.task-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.task-card__icon {
  font-size: 20px;
  line-height: 1;
}

.task-card__label {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #5f6368;
}

.task-card__title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #202124;
}

.task-card__desc {
  margin: 0 0 14px;
  font-size: 14px;
  color: #5f6368;
  line-height: 1.6;
}

.task-card__progress {
  margin-bottom: 14px;
}

.task-card__progress-bar {
  height: 8px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 6px;
}

.task-card__progress-fill {
  height: 100%;
  border-radius: 4px;
  background: linear-gradient(90deg, #1a73e8, #4a90d9);
  transition: width 0.5s ease;
}

.task-card__progress-text {
  font-size: 12px;
  color: #9aa0a6;
}

.task-card__action {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  background: #1a73e8;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.task-card__action:hover {
  background: #1557b0;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}

.task-card--warning .task-card__action {
  background: #ea4335;
}

.task-card--warning .task-card__action:hover {
  background: #c5221f;
}

@keyframes task-pulse {
  0%, 100% { opacity: 0; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.01); }
}

.task-card-enter-active {
  transition: all 0.4s ease;
}

.task-card-leave-active {
  transition: all 0.3s ease;
}

.task-card-enter-from {
  opacity: 0;
  transform: translateY(-12px);
}

.task-card-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
