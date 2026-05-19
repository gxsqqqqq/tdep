<template>
  <div
    :class="cardClasses"
    :role="interactive ? 'button' : undefined"
    :tabindex="interactive ? 0 : undefined"
    :aria-busy="loading"
    @click="handleClick"
    @keydown.enter="handleClick"
  >
    <div v-if="loading" class="base-card__skeleton" aria-hidden="true">
      <div class="base-card__skeleton-line base-card__skeleton-line--header" />
      <div class="base-card__skeleton-line" style="width: 80%" />
      <div class="base-card__skeleton-line" style="width: 60%" />
      <div class="base-card__skeleton-line" style="width: 70%" />
    </div>
    <div v-else class="base-card__content">
      <div v-if="$slots.header || title" class="base-card__header">
        <slot name="header">
          <h3 v-if="title" class="base-card__title">{{ title }}</h3>
        </slot>
        <div v-if="$slots.extra" class="base-card__extra">
          <slot name="extra" />
        </div>
      </div>
      <div class="base-card__body" :style="bodyStyle">
        <slot />
      </div>
      <div v-if="$slots.footer" class="base-card__footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export type CardVariant = 'bordered' | 'shadow' | 'flat' | 'elevated'
export type CardPadding = 'none' | 'sm' | 'md' | 'lg'

const props = withDefaults(defineProps<{
  variant?: CardVariant
  padding?: CardPadding
  hoverable?: boolean
  interactive?: boolean
  loading?: boolean
  title?: string
  bodyStyle?: Record<string, string>
}>(), {
  variant: 'bordered',
  padding: 'md',
  hoverable: false,
  interactive: false,
  loading: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent | KeyboardEvent]
}>()

const cardClasses = computed(() => [
  'base-card',
  `base-card--${props.variant}`,
  `base-card--padding-${props.padding}`,
  {
    'base-card--hoverable': props.hoverable,
    'base-card--interactive': props.interactive,
    'base-card--loading': props.loading,
  },
])

function handleClick(event: MouseEvent | KeyboardEvent) {
  if (props.interactive && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.base-card {
  position: relative;
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.25s ease;
}

.base-card--bordered {
  border: 1px solid #e8e8e8;
}

.base-card--shadow {
  border: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.base-card--flat {
  border: none;
  background-color: #fafafa;
}

.base-card--elevated {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.base-card--hoverable:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.base-card--interactive {
  cursor: pointer;
}
.base-card--interactive:hover {
  border-color: #1890ff;
}
.base-card--interactive:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}

.base-card--padding-none .base-card__body { padding: 0; }
.base-card--padding-sm .base-card__body { padding: 12px; }
.base-card--padding-md .base-card__body { padding: 20px; }
.base-card--padding-lg .base-card__body { padding: 28px; }

.base-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 0;
  min-height: 48px;
}

.base-card__title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.5;
  color: #1a1a1a;
}

.base-card__extra {
  flex-shrink: 0;
  margin-left: auto;
}

.base-card__body {
  padding-top: 16px;
}

.base-card__footer {
  padding: 12px 20px 20px;
  border-top: 1px solid #f0f0f0;
}

.base-card__skeleton {
  padding: 20px;
}

.base-card__skeleton-line {
  height: 14px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-shimmer 1.5s ease-in-out infinite;
  border-radius: 4px;
  margin-bottom: 10px;
}

.base-card__skeleton-line--header {
  height: 18px;
  width: 40%;
  margin-bottom: 16px;
}

@keyframes skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
