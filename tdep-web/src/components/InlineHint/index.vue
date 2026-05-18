<template>
  <Transition name="hint-slide">
    <div v-if="visible" class="inline-hint" :class="`inline-hint--${type}`">
      <div class="inline-hint__icon">{{ typeIcon }}</div>
      <div class="inline-hint__body">
        <div v-if="title" class="inline-hint__title">{{ title }}</div>
        <div class="inline-hint__content">
          <slot>{{ content }}</slot>
        </div>
      </div>
      <div v-if="actionLabel" class="inline-hint__action">
        <button class="inline-hint__btn" @click="$emit('action')">
          {{ actionLabel }} →
        </button>
      </div>
      <button v-if="dismissible" class="inline-hint__close" @click="handleDismiss">✕</button>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = withDefaults(defineProps<{
  type?: 'hint' | 'warning' | 'success' | 'info' | 'action'
  title?: string
  content?: string
  actionLabel?: string
  dismissible?: boolean
  dismissKey?: string
}>(), {
  type: 'info',
  dismissible: false
})

const emit = defineEmits<{
  action: []
  dismiss: []
}>()

const visible = ref(true)

const typeIcon = computed(() => {
  const icons: Record<string, string> = {
    hint: '💡',
    warning: '⚠️',
    success: '✅',
    info: 'ℹ️',
    action: '⚡'
  }
  return icons[props.type] || '💡'
})

function handleDismiss() {
  visible.value = false
  emit('dismiss')
  if (props.dismissKey) {
    try {
      const dismissed = JSON.parse(localStorage.getItem('agent_dismissed') || '[]')
      dismissed.push(props.dismissKey)
      localStorage.setItem('agent_dismissed', JSON.stringify(dismissed))
    } catch { /* ignore */ }
  }
}
</script>

<style scoped>
.inline-hint {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 12px;
  border-left: 4px solid #e0e4e8;
  background: #f8f9fa;
  margin-bottom: 16px;
}

.inline-hint--hint    { border-left-color: #fbbc04; background: #fffdf0; }
.inline-hint--warning { border-left-color: #ea4335; background: #fff5f5; }
.inline-hint--success { border-left-color: #34a853; background: #f0fff4; }
.inline-hint--info    { border-left-color: #1a73e8; background: #f0f7ff; }
.inline-hint--action  { border-left-color: #1a73e8; background: #f0f7ff; }

.inline-hint__icon {
  font-size: 20px;
  line-height: 1;
  flex-shrink: 0;
  margin-top: 2px;
}

.inline-hint__body {
  flex: 1;
  min-width: 0;
}

.inline-hint__title {
  font-weight: 600;
  font-size: 14px;
  color: #202124;
  margin-bottom: 4px;
}

.inline-hint__content {
  font-size: 13px;
  color: #5f6368;
  line-height: 1.6;
}

.inline-hint__action {
  flex-shrink: 0;
  align-self: center;
}

.inline-hint__btn {
  background: none;
  border: none;
  color: #1a73e8;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: background 0.15s;
}

.inline-hint__btn:hover {
  background: rgba(26, 115, 232, 0.08);
}

.inline-hint__close {
  background: none;
  border: none;
  color: #9aa0a6;
  cursor: pointer;
  font-size: 12px;
  padding: 4px;
  flex-shrink: 0;
  line-height: 1;
}

.inline-hint__close:hover {
  color: #5f6368;
}

.hint-slide-enter-active {
  transition: all 0.3s ease;
}

.hint-slide-leave-active {
  transition: all 0.2s ease;
}

.hint-slide-enter-from {
  opacity: 0;
  transform: translateY(-8px);
  max-height: 0;
}

.hint-slide-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
