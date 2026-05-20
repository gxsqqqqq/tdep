<template>
  <div
    class="base-empty"
    :class="[`base-empty--${size}`]"
    role="status"
    aria-live="polite"
  >
    <div class="base-empty__icon-wrapper">
      <slot name="icon">
        <svg
          class="base-empty__icon"
          viewBox="0 0 48 48"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          aria-hidden="true"
        >
          <rect x="8" y="6" width="32" height="36" rx="3" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M16 18h16M16 26h12M16 34h8" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
        </svg>
      </slot>
    </div>

    <slot name="title">
      <h3 class="base-empty__title">{{ title }}</h3>
    </slot>

    <slot name="description">
      <p v-if="description" class="base-empty__description">{{ description }}</p>
    </slot>

    <div v-if="showAction" class="base-empty__action-wrapper">
      <button
        :class="['base-empty__action', `base-empty__action--${actionType}`]"
        type="button"
        @click="$emit('action')"
      >
        {{ actionText }}
      </button>
    </div>

    <div v-if="$slots.extra" class="base-empty__extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup lang="ts">
export type EmptySize = 'sm' | 'md' | 'lg' | 'xl'

withDefaults(defineProps<{
  icon?: string
  title?: string
  description?: string
  size?: EmptySize
  showAction?: boolean
  actionText?: string
  actionType?: 'primary' | 'secondary'
}>(), {
  icon: 'Document',
  title: '暂无数据',
  description: '这里还没有任何内容',
  size: 'md',
  showAction: true,
  actionText: '立即添加',
  actionType: 'primary',
})

defineEmits<{
  action: []
}>()
</script>

<style scoped>
.base-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
  text-align: center;
}

/* ===== Icon Wrapper ===== */
.base-empty__icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  background-color: var(--color-bg-secondary);
  color: var(--color-text-disabled);
  animation: base-empty-breathe 3s ease-in-out infinite;
}

.base-empty__icon {
  display: block;
}

/* ===== Size Variants ===== */

/* sm */
.base-empty--sm .base-empty__icon-wrapper {
  width: 56px;
  height: 56px;
  margin-bottom: var(--spacing-sm);
}
.base-empty--sm .base-empty__icon {
  width: 24px;
  height: 24px;
}
.base-empty--sm .base-empty__title {
  font-size: var(--font-size-sm);
  margin-bottom: 4px;
}
.base-empty--sm .base-empty__description {
  font-size: var(--font-size-xs);
  margin-bottom: var(--spacing-md);
}
.base-empty--sm .base-empty__action {
  padding: 6px 16px;
  font-size: var(--font-size-xs);
}

/* md (default) */
.base-empty--md .base-empty__icon-wrapper {
  width: 72px;
  height: 72px;
  margin-bottom: var(--spacing-md);
}
.base-empty--md .base-empty__icon {
  width: 32px;
  height: 32px;
}
.base-empty--md .base-empty__title {
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-xs);
}
.base-empty--md .base-empty__description {
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-lg);
}
.base-empty--md .base-empty__action {
  padding: 8px 20px;
  font-size: var(--font-size-sm);
}

/* lg */
.base-empty--lg .base-empty__icon-wrapper {
  width: 96px;
  height: 96px;
  margin-bottom: var(--spacing-lg);
}
.base-empty--lg .base-empty__icon {
  width: 44px;
  height: 44px;
}
.base-empty--lg .base-empty__title {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-sm);
}
.base-empty--lg .base-empty__description {
  font-size: var(--font-size-sm);
  max-width: 400px;
  margin-bottom: var(--spacing-xl);
}
.base-empty--lg .base-empty__action {
  padding: 10px 24px;
  font-size: var(--font-size-base);
}

/* xl */
.base-empty--xl .base-empty__icon-wrapper {
  width: 120px;
  height: 120px;
  margin-bottom: var(--spacing-xl);
}
.base-empty--xl .base-empty__icon {
  width: 56px;
  height: 56px;
}
.base-empty--xl .base-empty__title {
  font-size: var(--font-size-xl);
  margin-bottom: var(--spacing-md);
}
.base-empty--xl .base-empty__description {
  font-size: var(--font-size-base);
  max-width: 480px;
  line-height: 1.7;
  margin-bottom: var(--spacing-xxl);
}
.base-empty--xl .base-empty__action {
  padding: 12px 28px;
  font-size: var(--font-size-base);
}

/* ===== Title ===== */
.base-empty__title {
  margin: 0;
  font-weight: 500;
  color: var(--color-text-primary);
  line-height: 1.5;
}

/* ===== Description ===== */
.base-empty__description {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
  max-width: 340px;
}

/* ===== Action Button ===== */
.base-empty__action-wrapper {
  margin-top: var(--spacing-xs);
}

.base-empty__action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-family: inherit;
  font-weight: 500;
  line-height: 1.5;
  white-space: nowrap;
  cursor: pointer;
  user-select: none;
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  transition: all 0.2s ease;
  outline: none;
}

.base-empty__action:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

.base-empty__action--primary {
  background-color: var(--color-primary);
  color: #ffffff;
  border-color: var(--color-primary);
}
.base-empty__action--primary:hover {
  background-color: var(--color-primary-light);
  border-color: var(--color-primary-light);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.25);
}
.base-empty__action--primary:active {
  background-color: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
}

.base-empty__action--secondary {
  background-color: transparent;
  color: var(--color-primary);
  border-color: var(--color-primary);
}
.base-empty__action--secondary:hover {
  background-color: rgba(24, 144, 255, 0.06);
}
.base-empty__action--secondary:active {
  background-color: rgba(24, 144, 255, 0.12);
}

/* ===== Extra Content ===== */
.base-empty__extra {
  margin-top: var(--spacing-lg);
}

/* ===== Animations ===== */
@keyframes base-empty-breathe {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.04); opacity: 0.85; }
}
</style>
