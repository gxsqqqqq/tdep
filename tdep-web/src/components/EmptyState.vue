<template>
  <div class="empty-state" role="status" aria-live="polite">
    <div class="empty-state__icon">{{ icon }}</div>
    <h3 class="empty-state__title">{{ title }}</h3>
    <p v-if="description" class="empty-state__desc">{{ description }}</p>
    <button
      v-if="actionText"
      class="empty-state__action"
      type="button"
      @click="$emit('action')"
    >
      {{ actionText }}
    </button>
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon?: string
  title: string
  description?: string
  actionText?: string
}>()

defineEmits<{
  action: []
}>()
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  text-align: center;
}

.empty-state__icon {
  font-size: 56px;
  line-height: 1;
  margin-bottom: 16px;
}

.empty-state__title {
  margin: 0 0 8px;
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.empty-state__desc {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--text-secondary);
  max-width: 360px;
  line-height: 1.6;
}

.empty-state__action {
  padding: 10px 24px;
  border: 1px solid var(--color-primary);
  border-radius: 8px;
  background: var(--color-primary);
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.empty-state__action:hover {
  background: var(--color-primary-hover);
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.25);
}
</style>
