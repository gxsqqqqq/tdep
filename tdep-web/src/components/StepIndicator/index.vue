<template>
  <div class="step-indicator card">
    <template v-for="(step, index) in steps" :key="index">
      <div
        class="step-indicator__item"
        :class="{
          'step-indicator__item--active': index === active,
          'step-indicator__item--done': index < active,
          'step-indicator__item--disabled': index > active && !clickable
        }"
        @click="handleClick(index)"
      >
        <div class="step-indicator__icon">
          <el-icon v-if="index < active"><Check /></el-icon>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <span class="step-indicator__title">{{ step.title }}</span>
      </div>
      <div v-if="index < steps.length - 1" class="step-indicator__connector" :class="{ 'step-indicator__connector--done': index < active }" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { Check } from '@element-plus/icons-vue'

defineProps<{
  active: number
  steps: Array<{ title: string; icon?: string }>
  clickable?: boolean
}>()

const emit = defineEmits<{
  'step-click': [index: number]
}>()

function handleClick(index: number) {
  emit('step-click', index)
}
</script>
