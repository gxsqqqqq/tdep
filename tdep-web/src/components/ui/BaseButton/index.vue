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
    <span v-if="$slots.icon && !loading && iconPosition === 'left'" class="base-button__icon base-button__icon--left">
      <slot name="icon" />
    </span>
    <span v-if="$slots.default" class="base-button__text">
      <slot />
    </span>
    <span v-if="$slots.icon && !loading && iconPosition === 'right'" class="base-button__icon base-button__icon--right">
      <slot name="icon" />
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export type ButtonVariant =
  | 'primary'
  | 'secondary'
  | 'danger'
  | 'ghost'
  | 'text'
  | 'gold'
  | 'success'
  | 'blockchain'

export type ButtonSize = 'sm' | 'md' | 'lg' | 'xl'

const props = withDefaults(defineProps<{
  variant?: ButtonVariant
  size?: ButtonSize
  loading?: boolean
  disabled?: boolean
  block?: boolean
  nativeType?: 'button' | 'submit' | 'reset'
  iconPosition?: 'left' | 'right'
  rounded?: boolean
}>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
  block: false,
  nativeType: 'button',
  iconPosition: 'right',
  rounded: false,
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
    'base-button--rounded': props.rounded,
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
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
  outline: none;
  position: relative;
}

.base-button:hover:not(:disabled):not(.base-button--disabled) {
  transform: translateY(-1px);
}

.base-button:active:not(:disabled):not(.base-button--disabled) {
  transform: scale(0.98);
}

.base-button:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px var(--color-primary-500, #2563eb), 0 0 0 4px rgba(37, 99, 235, 0.15);
}

.base-button:disabled,
.base-button--disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* ── 尺寸规范 ── */
.base-button--sm {
  height: 32px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 6px;
}

.base-button--md {
  height: 38px;
  padding: 0 16px;
  font-size: 14px;
  border-radius: 8px;
}

.base-button--lg {
  height: 44px;
  padding: 0 20px;
  font-size: 16px;
  border-radius: 10px;
}

.base-button--xl {
  height: 52px;
  padding: 0 24px;
  font-size: 18px;
  border-radius: 12px;
}

/* ── 药丸形状 ── */
.base-button--rounded {
  border-radius: 9999px !important;
}

/* ── Primary - 司法蓝主色 ── */
.base-button--primary {
  background-color: #2563eb;
  color: #ffffff;
  border-color: #2563eb;
}
.base-button--primary:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #1d4ed8;
  border-color: #1d4ed8;
}
.base-button--primary:active:not(:disabled):not(.base-button--disabled) {
  background-color: #1e40af;
  border-color: #1e40af;
}

/* ── Secondary - 中性灰 ── */
.base-button--secondary {
  background-color: #ffffff;
  color: rgba(0, 0, 0, 0.85);
  border-color: #d9d9d9;
}
.base-button--secondary:hover:not(:disabled):not(.base-button--disabled) {
  color: #2563eb;
  border-color: #2563eb;
}
.base-button--secondary:active:not(:disabled):not(.base-button--disabled) {
  color: #1d4ed8;
  border-color: #1d4ed8;
}

/* ── Danger - 危险红 ── */
.base-button--danger {
  background-color: #dc2626;
  color: #ffffff;
  border-color: #dc2626;
}
.base-button--danger:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #b91c1c;
  border-color: #b91c1c;
}
.base-button--danger:active:not(:disabled):not(.base-button--disabled) {
  background-color: #991b1b;
  border-color: #991b1b;
}

/* ── Ghost - 幽灵按钮 ── */
.base-button--ghost {
  background-color: transparent;
  color: #2563eb;
  border-color: transparent;
}
.base-button--ghost:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #eff6ff;
}
.base-button--ghost:active:not(:disabled):not(.base-button--disabled) {
  background-color: #dbeafe;
}

/* ── Text - 文字按钮 ── */
.base-button--text {
  background-color: transparent;
  color: #2563eb;
  border-color: transparent;
  box-shadow: none;
  padding-left: 4px;
  padding-right: 4px;
}
.base-button--text:hover:not(:disabled):not(.base-button--disabled) {
  color: #1d4ed8;
  background-color: transparent;
}
.base-button--text:active:not(:disabled):not(.base-button--disabled) {
  color: #1e40af;
}

/* ── Gold - 法徽金 ⭐ ── */
.base-button--gold {
  background-color: transparent;
  color: #d97706;
  border-color: #d97706;
}
.base-button--gold:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #fef3c7;
  color: #92400e;
  border-color: #f59e0b;
}
.base-button--gold:active:not(:disabled):not(.base-button--disabled) {
  background-color: #fde68a;
  color: #78350f;
  border-color: #d97706;
}

/* ── Success - 正义绿 ✅ ── */
.base-button--success {
  background-color: #059669;
  color: #ffffff;
  border-color: #059669;
}
.base-button--success:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #047857;
  border-color: #047857;
}
.base-button--success:active:not(:disabled):not(.base-button--disabled) {
  background-color: #065f46;
  border-color: #065f46;
}

/* ── Blockchain - 区块链紫 🔗 ── */
.base-button--blockchain {
  background-color: transparent;
  color: #7c3aed;
  border-color: #7c3aed;
}
.base-button--blockchain:hover:not(:disabled):not(.base-button--disabled) {
  background-color: #ede9fe;
  color: #6d28d9;
  border-color: #8b5cf6;
}
.base-button--blockchain:active:not(:disabled):not(.base-button--disabled) {
  background-color: #ddd6fe;
  color: #5b21b6;
  border-color: #7c3aed;
}

/* ── Block 全宽 ── */
.base-button--block {
  display: flex;
  width: 100%;
}

/* ── Loading Spinner（司法蓝渐变） ── */
.base-button__spinner {
  display: inline-flex;
  align-items: center;
  color: currentColor;
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

/* ── Icon 图标位置 ── */
.base-button__icon {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}

.base-button__icon--left {
  margin-right: 4px;
}

.base-button__icon--right {
  margin-left: 4px;
}

.base-button__text {
  display: inline-flex;
  align-items: center;
}

/* ══════════════════════════════════════════
   深色模式适配
   ══════════════════════════════════════════ */
:deep(.dark) .base-button--primary {
  background-color: var(--color-primary-600, #1d4ed8);
  border-color: var(--color-primary-600, #1d4ed8);
}
:deep(.dark) .base-button--primary:hover:not(:disabled):not(.base-button--disabled) {
  background-color: var(--color-primary-500, #2563eb);
  border-color: var(--color-primary-500, #2563eb);
}
:deep(.dark) .base-button--primary:active:not(:disabled):not(.base-button--disabled) {
  background-color: var(--color-primary-700, #1e40af);
  border-color: var(--color-primary-700, #1e40af);
}

:deep(.dark) .base-button--secondary {
  background-color: var(--color-bg-secondary, #1f2937);
  color: var(--color-text-primary, #f3f4f6);
  border-color: var(--color-border-primary, #374151);
}
:deep(.dark) .base-button--secondary:hover:not(:disabled):not(.base-button--disabled) {
  color: var(--color-primary-400, #60a5fa);
  border-color: var(--color-primary-400, #60a5fa);
}

:deep(.dark) .base-button--danger {
  background-color: var(--color-danger-600, #b91c1c);
  border-color: var(--color-danger-600, #b91c1c);
}
:deep(.dark) .base-button--danger:hover:not(:disabled):not(.base-button--disabled) {
  background-color: var(--color-danger-500, #dc2626);
  border-color: var(--color-danger-500, #dc2626);
}

:deep(.dark) .base-button--ghost {
  color: var(--color-primary-400, #60a5fa);
}
:deep(.dark) .base-button--ghost:hover:not(:disabled):not(.base-button--disabled) {
  background-color: rgba(37, 99, 235, 0.12);
}

:deep(.dark) .base-button--text {
  color: var(--color-primary-400, #60a5fa);
}
:deep(.dark) .base-button--text:hover:not(:disabled):not(.base-button--disabled) {
  color: var(--color-primary-300, #93c5fd);
}

:deep(.dark) .base-button--gold {
  color: var(--color-gold-400, #fbbf24);
  border-color: var(--color-gold-400, #fbbf24);
}
:deep(.dark) .base-button--gold:hover:not(:disabled):not(.base-button--disabled) {
  background-color: rgba(217, 119, 6, 0.15);
  color: var(--color-gold-300, #fcd34d);
  border-color: var(--color-gold-300, #fcd34d);
}

:deep(.dark) .base-button--success {
  background-color: var(--color-success-600, #047857);
  border-color: var(--color-success-600, #047857);
}
:deep(.dark) .base-button--success:hover:not(:disabled):not(.base-button--disabled) {
  background-color: var(--color-success-500, #059669);
  border-color: var(--color-success-500, #059669);
}

:deep(.dark) .base-button--blockchain {
  color: var(--color-purple-400, #a78bfa);
  border-color: var(--color-purple-400, #a78bfa);
}
:deep(.dark) .base-button--blockchain:hover:not(:disabled):not(.base-button--disabled) {
  background-color: rgba(124, 58, 237, 0.15);
  color: var(--color-purple-300, #c4b5fd);
  border-color: var(--color-purple-300, #c4b5fd);
}
</style>
