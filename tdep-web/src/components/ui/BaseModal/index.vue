<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="modelValue"
        class="base-modal-overlay"
        :class="{ 'base-modal-overlay--visible': modelValue }"
        role="dialog"
        :aria-modal="true"
        :aria-labelledby="titleId"
        :aria-label="!title ? '对话框' : undefined"
        @click.self="handleOverlayClick"
        @keydown.escape="handleEscape"
      >
        <div
          ref="dialogRef"
          :class="modalClasses"
          role="document"
          tabindex="-1"
        >
          <div v-if="$slots.header || title || showClose" class="base-modal__header">
            <div class="base-modal__header-content">
              <slot name="header">
                <h3 v-if="title" :id="titleId" class="base-modal__title">{{ title }}</h3>
              </slot>
            </div>
            <button
              v-if="showClose"
              class="base-modal__close"
              :aria-label="'关闭'"
              @click="handleClose"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                <path d="M18 6L6 18M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="base-modal__body" :style="bodyStyle">
            <slot />
          </div>

          <div v-if="$slots.footer" class="base-modal__footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted, nextTick } from 'vue'

export type ModalSize = 'sm' | 'md' | 'lg' | 'xl' | 'fullscreen'

const props = withDefaults(defineProps<{
  modelValue: boolean
  title?: string
  size?: ModalSize
  closable?: boolean
  maskClosable?: boolean
  escClosable?: boolean
  bodyStyle?: Record<string, string>
  destroyOnClose?: boolean
}>(), {
  size: 'md',
  closable: true,
  maskClosable: true,
  escClosable: true,
  destroyOnClose: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
  confirm: []
  cancel: []
}>()

const dialogRef = ref<HTMLElement | null>(null)
const titleId = computed(() => props.title ? `modal-title-${Math.random().toString(36).slice(2, 8)}` : '')

const modalClasses = computed(() => [
  'base-modal',
  `base-modal--${props.size}`,
])

const showClose = computed(() => props.closable)

watch(() => props.modelValue, (val) => {
  if (val) {
    nextTick(() => {
      dialogRef.value?.focus()
      document.body.style.overflow = 'hidden'
    })
  } else {
    document.body.style.overflow = ''
  }
})

function handleClose() {
  emit('update:modelValue', false)
  emit('close')
}

function handleOverlayClick() {
  if (props.maskClosable) {
    handleClose()
  }
}

function handleEscape() {
  if (props.escClosable) {
    handleClose()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscape)
  document.body.style.overflow = ''
})
</script>

<style scoped>
.base-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.45);
  padding: 16px;
}

.base-modal {
  position: relative;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 32px);
  outline: none;
}

.base-modal--sm { width: 420px; }
.base-modal--md { width: 560px; }
.base-modal--lg { width: 720px; }
.base-modal--xl { width: 900px; }

.base-modal--fullscreen {
  width: 100%;
  max-height: 100vh;
  height: 100vh;
  border-radius: 0;
  margin: -16px;
}

.base-modal__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 20px 24px 0;
  flex-shrink: 0;
}

.base-modal__header-content {
  flex: 1;
  min-width: 0;
}

.base-modal__title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.5;
  color: #1a1a1a;
}

.base-modal__close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #999999;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
  margin-left: 16px;
  margin-top: -4px;
}
.base-modal__close:hover {
  background-color: #f5f5f5;
  color: #666666;
}
.base-modal__close:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}

.base-modal__body {
  padding: 20px 24px;
  overflow-y: auto;
  flex: 1;
}

.base-modal__footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 12px 24px 20px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.25s ease;
}

.modal-enter-active .base-modal,
.modal-leave-active .base-modal {
  transition: all 0.25s cubic-bezier(0.23, 1, 0.32, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .base-modal {
  transform: scale(0.92) translateY(16px);
  opacity: 0;
}

.modal-leave-to .base-modal {
  transform: scale(0.95) translateY(-8px);
  opacity: 0;
}
</style>
