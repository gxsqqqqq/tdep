<template>
  <div class="form-guide" :class="{ 'form-guide--complete': isComplete }">
    <div class="form-guide__header">
      <span class="form-guide__icon">{{ isComplete ? '✅' : '📋' }}</span>
      <span class="form-guide__text">
        {{ isComplete ? '所有必填项已填写' : `已完成 ${filledCount}/${totalRequired} 项必填信息` }}
      </span>
    </div>
    <div v-if="!isComplete" class="form-guide__progress">
      <div class="form-guide__bar">
        <div class="form-guide__fill" :style="{ width: progressPercent + '%' }" />
      </div>
    </div>
    <div v-if="missingItems.length > 0" class="form-guide__missing">
      <div class="form-guide__missing-label">还需要填写：</div>
      <div v-for="item in missingItems" :key="item" class="form-guide__missing-item">
        <span class="form-guide__missing-dot">○</span>
        {{ item }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  fields: Array<{ key: string; label: string; required?: boolean }>
  formData: Record<string, unknown>
}>()

const requiredFields = computed(() => props.fields.filter((f) => f.required !== false))

const totalRequired = computed(() => requiredFields.value.length)

const filledCount = computed(() => {
  return requiredFields.value.filter((f) => {
    const val = props.formData[f.key]
    if (val === undefined || val === null) return false
    if (typeof val === 'string') return val.trim().length > 0
    if (Array.isArray(val)) return val.length > 0
    return true
  }).length
})

const isComplete = computed(() => filledCount.value === totalRequired.value)

const progressPercent = computed(() => {
  if (totalRequired.value === 0) return 100
  return Math.round((filledCount.value / totalRequired.value) * 100)
})

const missingItems = computed(() => {
  return requiredFields.value
    .filter((f) => {
      const val = props.formData[f.key]
      if (val === undefined || val === null) return true
      if (typeof val === 'string') return val.trim().length === 0
      if (Array.isArray(val)) return val.length === 0
      return false
    })
    .map((f) => f.label)
})

defineEmits<{
  (e: 'update:missing', fields: string[]): void
}>()
</script>

<style scoped>
.form-guide {
  padding: 16px 20px;
  border-radius: 12px;
  background: #f0f7ff;
  border: 1px solid #d4e4f7;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.form-guide--complete {
  background: #f0fff4;
  border-color: #c8edd4;
}

.form-guide__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.form-guide__icon {
  font-size: 18px;
  line-height: 1;
}

.form-guide__text {
  font-size: 14px;
  font-weight: 600;
  color: #202124;
}

.form-guide__progress {
  margin-bottom: 12px;
}

.form-guide__bar {
  height: 6px;
  border-radius: 3px;
  background: #e0e4e8;
  overflow: hidden;
}

.form-guide__fill {
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #1a73e8, #4a90d9);
  transition: width 0.4s ease;
}

.form-guide__missing {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.form-guide__missing-label {
  font-size: 12px;
  color: #9aa0a6;
}

.form-guide__missing-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #5f6368;
  padding: 3px 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.6);
}

.form-guide__missing-dot {
  color: #ea4335;
  font-size: 10px;
}
</style>
