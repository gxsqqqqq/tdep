<template>
  <div class="error-boundary">
    <slot v-if="!hasError" />
    <div v-else class="error-boundary__fallback">
      <div class="error-boundary__icon">⚠️</div>
      <h3 class="error-boundary__title">页面加载出错</h3>
      <p class="error-boundary__message">{{ errorMessage }}</p>
      <button class="error-boundary__retry" @click="$emit('retry')">
        🔄 重新加载
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'

defineEmits<{
  retry: []
}>()

const hasError = ref(false)
const errorMessage = ref('')

onErrorCaptured((err: unknown) => {
  hasError.value = true
  errorMessage.value = err instanceof Error ? err.message : '未知错误'
  return false
})
</script>

<style scoped>
.error-boundary__fallback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.error-boundary__icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-boundary__title {
  margin: 0 0 8px;
  font-size: 18px;
  color: var(--text-primary);
}

.error-boundary__message {
  margin: 0 0 20px;
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 400px;
}

.error-boundary__retry {
  padding: 10px 24px;
  border: 1px solid var(--color-primary);
  border-radius: 8px;
  background: var(--color-primary);
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.error-boundary__retry:hover {
  opacity: 0.9;
}
</style>
