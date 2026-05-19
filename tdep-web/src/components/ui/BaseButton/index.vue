<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    :aria-busy="loading"
    :aria-disabled="disabled || loading"
    :type="nativeType"
    @click="handleClick"
  >
    <span v-if="loading" class="base-button__spinner" aria-hidden="true">
      <svg class="base-button__spin-icon" viewBox="0 0 24 24" fill="none">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-opacity="0.25" />
        <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="3" stroke-linecap="round" />
      </svg>
    </span>
    <span v-if="$slots.icon && !loading" class="base-button__icon">
      <slot name="icon" />
    </span>
    <span class="base-button__text">
      <slot />
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'ghost' | 'text'
export type ButtonSize = 'sm' | 'md' | 'lg'

const props = withDefaults(defineProps<{
  variant?: ButtonVariant
  size?: ButtonSize
  loading?: boolean
  disabled?: boolean
  block?: boolean
  nativeType?: 'button' | 'submit' | 'reset'
}>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  block: false,
  nativeType: 'button',
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const buttonClasses = computed(() => [
  'base-button',
  `base-button--${props.variant}`,
  `base-button--${props.size}`,
  {
    'base-button--block': props.block,
    'base-button--loading': props.loading,
    'base-button--disabled': props.disabled,
  },
])

function handleClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-family: inherit;
  font-weight: 500;
  line-height: 1.5;
  white-space: nowrap;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  outline: none;
  position: relative;
}

.base-button:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}

.base-button:disabled,
.base-button--disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.base-button--sm {
  height: 32px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 4px;
}

.base-button--md {
  height: 38px;
  padding: 0 16px;
  font-size: 14px;
  border-radius: 6px;
}

.base-button--lg {
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  border-radius: 8px;
}

.base-button--primary {
  background-color: #1890ff;
  color: #ffffff;
  border-color: #1890ff;
}
.base-button--primary:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
.base-button--primary:active:not(:disabled):not(.base-button--disabled) {
  background-color: #096dd9;
  border-color: #096dd9;
}

.base-button--secondary {
  background-color: #ffffff;
  color: rgba(0, 0, 0, 0.85);
  border-color: #d9d9d9;
}
.base-button--secondary:hover:not(:disabled):not(.base-button--disabled) {
  color: #1890ff;
  border-color: #1890ff;
}
.base-button--secondary:active:not(:disabled):not(.base-button--disabled) {
  color: #096dd9;
  border-color: #096dd9;
}

.base-button--danger {
  background-color: #ff4d4f;
  color: #ffffff;
  border-color: #ff4d4f;
}
.base-button--danger:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #ff7875;
  border-color: #ff7875;
}
.base-button--danger:active:not(:disabled):not(.base-button--disabled) {
  background-color: #cf1322;
  border-color: #cf1322;
}

.base-button--ghost {
  background-color: transparent;
  color: #1890ff;
  border-color: transparent;
}
.base-button--ghost:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #e6f7ff;
}
.base-button--ghost:active:not(:disabled):not(.base-button--disabled) {
  background-color: #bae7ff;
}

.base-button--text {
  background-color: transparent;
  color: #1890ff;
  border-color: transparent;
  box-shadow: none;
  padding-left: 4px;
  padding-right: 4px;
}
.base-button--text:hover:not(:disabled):not(.base-button--disabled) {
  color: #40a9ff;
  background-color: transparent;
}
.base-button--text:active:not(:disabled):not(.base-button--disabled) {
  color: #096dd9;
}

.base-button--block {
  display: flex;
  width: 100%;
}

.base-button__spinner {
  display: inline-flex;
  align-items: center;
}

.base-button__spin-icon {
  width: 16px;
  height: 16px;
  animation: base-button-spin 0.8s linear infinite;
}

@keyframes base-button-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.base-button__icon {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}

.base-button__text {
  display: inline-flex;
  align-items: center;
}
</style>
