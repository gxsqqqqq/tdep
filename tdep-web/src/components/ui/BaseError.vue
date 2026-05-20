<template>
  <div
    class="base-error"
    :class="[`base-error--${severity}`]"
    role="alert"
    aria-live="assertive"
  >
    <div class="base-error__body">
      <div class="base-error__icon-area">
        <slot name="icon">
          <div :class="['base-error__icon-wrapper', `base-error__icon-wrapper--${severity}`]" aria-hidden="true">
            <svg v-if="severity === 'info'" class="base-error__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 16v-4M12 8h.01"/>
            </svg>
            <svg v-else-if="severity === 'warning'" class="base-error__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
              <path d="M12 9v4M12 17h.01"/>
            </svg>
            <svg v-else-if="severity === 'error'" class="base-error__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <path d="M15 9l-6 6M9 9l6 6"/>
            </svg>
            <svg v-else-if="severity === 'fatal'" class="base-error__icon base-error__icon--fatal" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 9v4M12 17h.01"/>
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
              <line x1="12" y1="22" x2="12" y2="22" stroke-dasharray="4"/>
            </svg>
          </div>
        </slot>

        <div class="base-error__main">
          <div class="base-error__header">
            <slot name="title">
              <h3 class="base-error__title">{{ title }}</h3>
            </slot>
            <span v-if="code !== undefined && code !== null && code !== ''" class="base-error__code">{{ code }}</span>
          </div>

          <slot name="message">
            <p class="base-error__message">{{ message }}</p>
          </slot>

          <!-- Details Panel -->
          <div
            v-if="showDetails"
            class="base-error__details"
          >
            <button
              type="button"
              class="base-error__details-toggle"
              @click="toggleDetails"
              :aria-expanded="isDetailsVisible"
              aria-controls="error-details-content"
            >
              <span>{{ isDetailsVisible ? '收起详情' : '查看详情' }}</span>
              <svg
                class="base-error__details-chevron"
                :class="{ 'base-error__details-chevron--open': isDetailsVisible }"
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <polyline points="6 9 12 15 18 9"/>
              </svg>
            </button>
            <transition name="base-error-details">
              <div
                v-show="isDetailsVisible"
                id="error-details-content"
                class="base-error__details-panel"
              >
                <pre class="base-error__details-text"><slot name="details">错误详细信息将在此处显示</slot></pre>
                <button
                  type="button"
                  class="base-error__copy-btn"
                  @click="copyDetails"
                  title="复制错误信息"
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                    <path d="M5 15H4a2 2 0 01-2-2V4a2 2 0 012-2h9a2 2 0 012 2v1"/>
                  </svg>
                  {{ copied ? '已复制' : '复制' }}
                </button>
              </div>
            </transition>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="base-error__actions">
        <slot name="actions">
          <button
            v-if="showRetry"
            type="button"
            :class="['base-error__retry', `base-error__retry--${severity}`]"
            @click="$emit('retry')"
            :aria-label="'重试' + (title || '')"
          >
            <svg
              class="base-error__retry-icon"
              :class="{ 'base-error__retry-icon--spinning': isRetrying }"
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
            >
              <polyline points="23 4 23 10 17 10"/>
              <path d="M20.49 15a9 9 0 11-2.12-9.36L23 10"/>
            </svg>
            重试
          </button>
        </slot>
      </div>
    </div>

    <div v-if="$slots.extra" class="base-error__extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

export type ErrorSeverity = 'info' | 'warning' | 'error' | 'fatal'

const props = withDefaults(defineProps<{
  title?: string
  message?: string
  severity?: ErrorSeverity
  showRetry?: boolean
  showDetails?: boolean
  code?: string | number
  icon?: string
  isRetrying?: boolean
}>(), {
  title: '出错了',
  message: '发生了未知错误',
  severity: 'error',
  showRetry: true,
  showDetails: false,
  code: '',
  isRetrying: false,
})

const emit = defineEmits<{
  retry: []
  'show-details': []
  'hide-details': []
}>()

const isDetailsVisible = ref(false)
const copied = ref(false)

function toggleDetails() {
  isDetailsVisible.value = !isDetailsVisible.value
  if (isDetailsVisible.value) {
    emit('show-details')
  } else {
    emit('hide-details')
  }
}

async function copyDetails() {
  try {
    const textContent = props.message + (props.code ? ` [${props.code}]` : '')
    await navigator.clipboard.writeText(textContent)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch {
    const textArea = document.createElement('textarea')
    textArea.value = props.message + (props.code ? ` [${props.code}]` : '')
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  }
}
</script>

<style scoped>
.base-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) var(--spacing-lg);
}

/* ===== Body ===== */
.base-error__body {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 480px;
  width: 100%;
  text-align: center;
}

/* ===== Icon Area ===== */
.base-error__icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  margin-bottom: var(--spacing-md);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.base-error__icon {
  display: block;
  transition: transform 0.3s ease;
}

.base-error__icon--fatal {
  animation: fatal-pulse 2s ease-in-out infinite;
}

/* Severity: info */
.base-error__icon-wrapper--info {
  width: 64px;
  height: 64px;
  background-color: rgba(24, 144, 255, 0.08);
  color: var(--color-info);
}

/* Severity: warning */
.base-error__icon-wrapper--warning {
  width: 68px;
  height: 68px;
  background-color: rgba(250, 173, 20, 0.1);
  color: var(--color-warning);
}

/* Severity: error */
.base-error__icon-wrapper--error {
  width: 72px;
  height: 72px;
  background-color: rgba(255, 77, 79, 0.08);
  color: var(--color-error);
}

/* Severity: fatal */
.base-error__icon-wrapper--fatal {
  width: 88px;
  height: 88px;
  background-color: rgba(207, 19, 34, 0.1);
  color: #cf1322;
}

.base-error--fatal .base-error__icon-wrapper--fatal .base-error__icon {
  width: 44px;
  height: 44px;
}

@keyframes fatal-pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.06); }
}

/* ===== Main Content ===== */
.base-error__main {
  width: 100%;
}

.base-error__header {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xs);
}

.base-error__title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 600;
  line-height: 1.5;
  color: var(--color-text-primary);
}

.base-error--fatal .base-error__title {
  font-size: var(--font-size-xl);
}

.base-error__code {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  font-size: var(--font-size-xs);
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-weight: 600;
  border-radius: var(--radius-sm);
  background-color: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  letter-spacing: 0.02em;
}

.base-error__message {
  margin: 0;
  font-size: var(--font-size-sm);
  line-height: 1.6;
  color: var(--color-text-secondary);
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

/* ===== Details Panel ===== */
.base-error__details {
  margin-top: var(--spacing-md);
}

.base-error__details-toggle {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  padding: 6px 0;
  font-family: inherit;
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  cursor: pointer;
  transition: color 0.2s ease;
  outline: none;
}

.base-error__details-toggle:hover {
  color: var(--color-primary-light);
}

.base-error__details-toggle:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
  border-radius: 2px;
}

.base-error__details-chevron {
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.base-error__details-chevron--open {
  transform: rotate(180deg);
}

.base-error__details-panel {
  margin-top: var(--spacing-sm);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  background-color: var(--color-bg-secondary);
  position: relative;
}

.base-error__details-text {
  margin: 0;
  padding: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: var(--font-size-xs);
  line-height: 1.7;
  color: var(--color-text-disabled);
  white-space: pre-wrap;
  word-break: break-all;
  user-select: all;
}

.base-error__copy-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: var(--spacing-sm);
  padding: 4px 10px;
  background: none;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-family: inherit;
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  outline: none;
}

.base-error__copy-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background-color: rgba(24, 144, 255, 0.04);
}

.base-error__copy-btn:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

/* Details Transition */
.base-error-details-enter-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.base-error-details-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 1, 1);
}
.base-error-details-enter-from,
.base-error-details-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Actions ===== */
.base-error__actions {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: center;
  gap: var(--spacing-sm);
}

.base-error__retry {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  font-family: inherit;
  font-size: var(--font-size-sm);
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

.base-error__retry:focus-visible {
  outline: 2px solid currentColor;
  outline-offset: 2px;
}

/* Retry by severity */
.base-error__retry--info {
  background-color: var(--color-info);
  color: #ffffff;
  border-color: var(--color-info);
}
.base-error__retry--info:hover:not(:disabled) {
  background-color: var(--color-primary-light);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.base-error__retry--warning {
  background-color: var(--color-warning);
  color: #fff;
  border-color: var(--color-warning);
}
.base-error__retry--warning:hover:not(:disabled) {
  background-color: #ffc53d;
  box-shadow: 0 4px 12px rgba(250, 173, 20, 0.3);
}

.base-error__retry--error {
  background-color: var(--color-error);
  color: #ffffff;
  border-color: var(--color-error);
}
.base-error__retry--error:hover:not(:disabled) {
  background-color: #ff7875;
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.base-error__retry--fatal {
  background-color: #cf1322;
  color: #ffffff;
  border-color: #cf1322;
}
.base-error__retry--fatal:hover:not(:disabled) {
  background-color: #f5222d;
  box-shadow: 0 4px 12px rgba(207, 19, 34, 0.35);
}

/* Retry Icon Spin */
.base-error__retry-icon {
  transition: transform 0.3s ease;
}

.base-error__retry-icon--spinning {
  animation: retry-spin 0.8s linear infinite;
  transform-origin: center;
}

@keyframes retry-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ===== Extra ===== */
.base-error__extra {
  margin-top: var(--spacing-lg);
}
</style>
