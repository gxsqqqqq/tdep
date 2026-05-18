<template>
  <div class="process-timeline">
    <div
      v-for="(step, index) in steps"
      :key="index"
      class="process-step"
      :class="{
        'process-step--done': index < current,
        'process-step--active': index === current,
        'process-step--pending': index > current
      }"
      @click="$emit('step-click', index)"
    >
      <div class="process-step__dot">
        <span v-if="index < current">✓</span>
        <span v-else>{{ index + 1 }}</span>
      </div>
      <div class="process-step__label">{{ step }}</div>
      <div v-if="index < steps.length - 1" class="process-step__line" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  steps: string[]
  current: number
}>()

defineEmits<{
  'step-click': [index: number]
}>()
</script>

<style scoped>
.process-timeline {
  display: flex;
  align-items: flex-start;
  padding: 20px 0;
  overflow-x: auto;
}

.process-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  min-width: 80px;
  position: relative;
  cursor: pointer;
}

.process-step__dot {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  border: 3px solid #e0e4e8;
  background: white;
  color: #9aa0a6;
  transition: all 0.3s ease;
  z-index: 1;
}

.process-step--done .process-step__dot {
  border-color: #34a853;
  background: #34a853;
  color: white;
}

.process-step--active .process-step__dot {
  border-color: #1a73e8;
  background: #1a73e8;
  color: white;
  box-shadow: 0 0 0 6px rgba(26, 115, 232, 0.15);
  animation: step-glow 2s ease-in-out infinite;
}

.process-step__label {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #9aa0a6;
  text-align: center;
  white-space: nowrap;
}

.process-step--done .process-step__label {
  color: #34a853;
  font-weight: 600;
}

.process-step--active .process-step__label {
  color: #1a73e8;
  font-weight: 700;
}

.process-step__line {
  position: absolute;
  top: 18px;
  left: calc(50% + 20px);
  right: calc(-50% + 20px);
  height: 3px;
  background: #e0e4e8;
  transition: background 0.3s ease;
}

.process-step--done .process-step__line {
  background: #34a853;
}

@keyframes step-glow {
  0%, 100% { box-shadow: 0 0 0 6px rgba(26, 115, 232, 0.15); }
  50% { box-shadow: 0 0 0 10px rgba(26, 115, 232, 0.08); }
}

/* Dark mode */
:root.dark .process-step__dot {
  background: #1e293b;
  border-color: #334155;
  color: #64748b;
}

:root.dark .process-step--active .process-step__dot {
  background: #1a73e8;
  border-color: #1a73e8;
}

:root.dark .process-step__line {
  background: #334155;
}
</style>
