<template>
  <div class="quick-actions">
    <div class="quick-actions__title">快捷操作</div>
    <div class="quick-actions__grid">
      <button
        v-for="action in actions"
        :key="action.id"
        class="quick-action"
        :class="{ 'quick-action--highlight': action.highlight }"
        @click="$emit('select', action)"
      >
        <span class="quick-action__icon">{{ action.icon }}</span>
        <span class="quick-action__label">{{ action.label }}</span>
        <span v-if="action.badge" class="quick-action__badge">{{ action.badge }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface QuickAction {
  id: string
  icon: string
  label: string
  route?: string
  badge?: string | number
  highlight?: boolean
}

defineProps<{
  actions: QuickAction[]
}>()

defineEmits<{
  select: [action: QuickAction]
}>()
</script>

<style scoped>
.quick-actions {
  margin-bottom: 20px;
}

.quick-actions__title {
  font-size: 14px;
  font-weight: 600;
  color: #5f6368;
  margin-bottom: 12px;
}

.quick-actions__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  border-radius: 12px;
  border: 1px solid #e0e4e8;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.quick-action:hover {
  border-color: #1a73e8;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.1);
  transform: translateY(-2px);
}

.quick-action--highlight {
  border-color: #1a73e8;
  background: #f0f7ff;
}

.quick-action__icon {
  font-size: 28px;
  line-height: 1;
}

.quick-action__label {
  font-size: 13px;
  font-weight: 500;
  color: #202124;
}

.quick-action__badge {
  position: absolute;
  top: 8px;
  right: 8px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  background: #ea4335;
  color: white;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}

:root.dark .quick-action {
  background: #1e293b;
  border-color: #334155;
}

:root.dark .quick-action:hover {
  border-color: #1a73e8;
}

:root.dark .quick-action__label {
  color: #e2e8f0;
}
</style>
