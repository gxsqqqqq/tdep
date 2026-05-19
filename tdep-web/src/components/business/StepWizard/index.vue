<template>
  <div class="step-wizard">
    <nav class="step-wizard__steps" role="tablist" :aria-label="'步骤向导'">
      <button
        v-for="(step, index) in steps"
        :key="step.key"
        type="button"
        role="tab"
        :class="stepButtonClasses(step, index)"
        :aria-selected="currentStep === index"
        :aria-disabled="!canNavigateTo(index)"
        :tabindex="currentStep === index ? 0 : -1"
        @click="goToStep(index)"
      >
        <span class="step-wizard__step-indicator">
          <span v-if="getStepStatus(index) === 'completed'" class="step-wizard__step-check" aria-hidden="true">✓</span>
          <span v-else class="step-wizard__step-number">{{ index + 1 }}</span>
        </span>
        <span class="step-wizard__step-info">
          <span class="step-wizard__step-title">{{ step.title }}</span>
          <span v-if="step.subtitle" class="step-wizard__step-subtitle">{{ step.subtitle }}</span>
        </span>
      </button>
    </nav>

    <div class="step-wizard__progress" role="progressbar" :aria-valuenow="progressPercent" :aria-valuemin="0" :aria-valuemax="100">
      <div class="step-wizard__progress-bar" :style="{ width: `${progressPercent}%` }" />
    </div>

    <div class="step-wizard__content">
      <slot :name="`step-${steps[currentStep]?.key}`" :step="currentStepData" :index="currentStep">
        <div class="step-wizard__panel" role="tabpanel">
          <slot name="default" :step="currentStepData" :index="currentStep" />
        </div>
      </slot>
    </div>

    <div class="step-wizard__footer">
      <div class="step-wizard__footer-left">
        <BaseButton variant="secondary" :disabled="currentStep === 0" @click="prevStep">
          上一步
        </BaseButton>
      </div>
      <div class="step-wizard__footer-center">
        <span class="step-wizard__step-counter">
          第 {{ currentStep + 1 }} 步，共 {{ steps.length }} 步
        </span>
      </div>
      <div class="step-wizard__footer-right">
        <BaseButton
          v-if="currentStep < steps.length - 1"
          variant="primary"
          :loading="loading"
          @click="nextStep"
        >
          下一步
        </BaseButton>
        <BaseButton
          v-else
          variant="primary"
          :loading="loading"
          @click="handleComplete"
        >
          {{ finishText }}
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import BaseButton from '@/components/ui/BaseButton/index.vue'

export interface WizardStep {
  key: string
  title: string
  subtitle?: string
  optional?: boolean
  validate?: () => Promise<boolean> | boolean
}

type StepStatus = 'pending' | 'active' | 'completed' | 'error'

const props = withDefaults(defineProps<{
  steps: WizardStep[]
  modelValue?: number
  loading?: boolean
  finishText?: string
  linear?: boolean
}>(), {
  modelValue: 0,
  loading: false,
  finishText: '完成',
  linear: true,
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
  'change': [step: number]
  'next': []
  'prev': []
  'complete': []
  'step-error': [step: number, errors: string[]]
}>()

const currentStep = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const currentStepData = computed(() => props.steps[currentStep.value] ?? null)

const progressPercent = computed(() => {
  if (props.steps.length <= 1) return 100
  return Math.round((currentStep.value / (props.steps.length - 1)) * 100)
})

function getStepStatus(index: number): StepStatus {
  if (index < currentStep.value) return 'completed'
  if (index === currentStep.value) return 'active'
  return 'pending'
}

function canNavigateTo(index: number): boolean {
  if (!props.linear) return true
  return index <= currentStep.value
}

function stepButtonClasses(step: WizardStep, index: number): string[] {
  const status = getStepStatus(index)
  return [
    'step-wizard__step-btn',
    `step-wizard__step-btn--${status}`,
    {
      'step-wizard__step-btn--disabled': !canNavigateTo(index),
      'step-wizard__step-btn--optional': step.optional,
    },
  ]
}

async function nextStep() {
  const step = props.steps[currentStep.value]
  if (step?.validate) {
    try {
      const valid = await step.validate()
      if (!valid) return
    } catch {
      emit('step-error', currentStep.value, ['验证失败'])
      return
    }
  }
  if (currentStep.value < props.steps.length - 1) {
    currentStep.value++
    emit('change', currentStep.value)
    emit('next')
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--
    emit('change', currentStep.value)
    emit('prev')
  }
}

function goToStep(index: number) {
  if (canNavigateTo(index) && index !== currentStep.value) {
    currentStep.value = index
    emit('change', index)
  }
}

async function handleComplete() {
  const step = props.steps[currentStep.value]
  if (step?.validate) {
    try {
      const valid = await step.validate()
      if (!valid) return
    } catch {
      emit('step-error', currentStep.value, ['验证失败'])
      return
    }
  }
  emit('complete')
}

defineExpose({ nextStep, prevStep, goToStep, currentStep })
</script>

<style scoped>
.step-wizard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.step-wizard__steps {
  display: flex;
  align-items: flex-start;
  gap: 0;
  overflow-x: auto;
  padding-bottom: 4px;
}

.step-wizard__step-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: transparent;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.25s ease;
  position: relative;
  flex: 1;
  justify-content: flex-start;
  opacity: 0.45;
}
.step-wizard__step-btn:not(:last-child)::after {
  content: '';
  position: absolute;
  right: -12px;
  top: 50%;
  transform: translateY(-50%) rotate(-45deg);
  width: 18px;
  height: 18px;
  border-right: 2px solid #e8e8e8;
  border-bottom: 2px solid #e8e8e8;
  background: #ffffff;
  z-index: 1;
}
.step-wizard__step-btn--completed:not(:last-child)::after {
  border-right-color: #1890ff;
  border-bottom-color: #1890ff;
}
.step-wizard__step-btn--active {
  opacity: 1;
}
.step-wizard__step-btn--completed {
  opacity: 0.75;
}
.step-wizard__step-btn:hover:not(.step-wizard__step-btn--disabled) {
  opacity: 0.85;
}
.step-wizard__step-btn:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
  border-radius: 4px;
}
.step-wizard__step-btn--disabled {
  cursor: not-allowed;
}

.step-wizard__step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: 2px solid #d9d9d9;
  background: #ffffff;
  font-size: 12px;
  font-weight: 600;
  color: #999999;
  flex-shrink: 0;
  transition: all 0.25s ease;
  z-index: 2;
}

.step-wizard__step-btn--active .step-wizard__step-indicator {
  border-color: #1890ff;
  background: #1890ff;
  color: #ffffff;
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.15);
}

.step-wizard__step-btn--completed .step-wizard__step-indicator {
  border-color: #52c41a;
  background: #52c41a;
  color: #ffffff;
}

.step-wizard__step-check {
  font-size: 13px;
}

.step-wizard__step-number {
  font-size: 12px;
}

.step-wizard__step-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.step-wizard__step-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.step-wizard__step-btn--active .step-wizard__step-title {
  color: #1890ff;
}

.step-wizard__step-btn--completed .step-wizard__step-title {
  color: #1a1a1a;
}

.step-wizard__step-subtitle {
  font-size: 11px;
  color: #999999;
}

.step-wizard__progress {
  height: 3px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.step-wizard__progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #40a9ff);
  border-radius: 2px;
  transition: width 0.35s ease;
}

.step-wizard__content {
  min-height: 200px;
}

.step-wizard__panel {
  animation: step-fade-in 0.25s ease;
}

@keyframes step-fade-in {
  from { opacity: 0; transform: translateX(12px); }
  to { opacity: 1; transform: translateX(0); }
}

.step-wizard__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  gap: 16px;
}

.step-wizard__footer-left,
.step-wizard__footer-right {
  flex-shrink: 0;
}

.step-wizard__footer-center {
  flex: 1;
  text-align: center;
}

.step-wizard__step-counter {
  font-size: 13px;
  color: #999999;
}

@media (max-width: 640px) {
  .step-wizard__steps {
    flex-direction: column;
    gap: 4px;
  }
  .step-wizard__step-btn {
    padding: 10px 12px;
  }
  .step-wizard__step-btn::after {
    display: none;
  }
  .step-wizard__footer {
    flex-direction: column;
    gap: 12px;
  }
  .step-wizard__footer-center {
    order: -1;
  }
}
</style>
