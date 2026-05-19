<template>
  <div :class="timelineClasses" role="list" :aria-label="'流程时间轴'">
    <div
      v-for="(item, index) in items"
      :key="item.id"
      :class="itemClasses(item, index)"
      role="listitem"
      @click="handleItemClick(item, index)"
    >
      <div class="timeline__node-wrapper">
        <div v-if="index > 0" :class="lineClasses(index)" aria-hidden="true" />
        <div :class="nodeClasses(item)" :aria-current="isActive(item) ? 'step' : undefined">
          <svg
            v-if="item.status === 'completed'"
            class="timeline__check-icon"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M20 6L9 17l-5-5" />
          </svg>
          <component :is="item.icon" v-else-if="item.icon && isActive(item)" />
          <span v-else class="timeline__step-number">{{ index + 1 }}</span>
        </div>
      </div>

      <div class="timeline__content">
        <div class="timeline__header">
          <h4 class="timeline__title">{{ item.title }}</h4>
          <span v-if="item.timestamp" class="timeline__timestamp">{{ item.timestamp }}</span>
        </div>
        <p v-if="item.description" class="timeline__description">{{ item.description }}</p>
        <div v-if="item.content || $slots[`content-${item.id}`]" class="timeline__extra">
          <slot :name="`content-${item.id}`" :item="item">
            {{ item.content }}
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'

export interface TimelineItem {
  id: string
  title: string
  description?: string
  timestamp?: string
  status: 'completed' | 'active' | 'pending' | 'skipped'
  icon?: Component
  content?: any
}

const props = withDefaults(defineProps<{
  items: TimelineItem[]
  activeIndex?: number
  orientation?: 'vertical' | 'horizontal'
}>(), {
  activeIndex: -1,
  orientation: 'vertical',
})

const emit = defineEmits<{
  'select': [item: TimelineItem, index: number]
}>()

const timelineClasses = computed(() => [
  'timeline',
  `timeline--${props.orientation}`,
])

function isActive(item: TimelineItem): boolean {
  return item.status === 'active'
}

function isCompleted(item: TimelineItem): boolean {
  return item.status === 'completed'
}

function isPending(item: TimelineItem): boolean {
  return item.status === 'pending' || item.status === 'skipped'
}

function itemClasses(item: TimelineItem, index: number): string[] {
  return [
    'timeline__item',
    `timeline__item--${item.status}`,
    {
      'timeline__item--active': isActive(item),
      'timeline__item--clickable': !!emit.select,
    },
  ]
}

function lineClasses(index: number): string[] {
  const prevItem = props.items[index - 1]
  const isPrevCompleted = prevItem?.status === 'completed'
  return [
    'timeline__line',
    { 'timeline__line--completed': isPrevCompleted },
  ]
}

function nodeClasses(item: TimelineItem): string[] {
  return [
    'timeline__node',
    `timeline__node--${item.status}`,
    {
      'timeline__node--active': isActive(item),
      'timeline__node--with-icon': !!(item.icon && isActive(item)),
    },
  ]
}

function handleItemClick(item: TimelineItem, index: number) {
  emit('select', item, index)
}
</script>

<style scoped>
.timeline {
  --timeline-color-completed: #1890ff;
  --timeline-color-active: #1890ff;
  --timeline-color-pending: #d9d9d9;
  --timeline-color-skipped: #e8e8e8;
  --timeline-line-color: #e8e8e8;
}

.timeline--vertical {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.timeline--horizontal {
  display: flex;
  align-items: flex-start;
  gap: 0;
  overflow-x: auto;
  padding-bottom: 16px;
}

.timeline__item {
  position: relative;
  display: flex;
  gap: 16px;
}

.timeline--vertical .timeline__item {
  padding-bottom: 32px;
}
.timeline--vertical .timeline__item:last-child {
  padding-bottom: 0;
}

.timeline--horizontal .timeline__item {
  flex-direction: column;
  align-items: center;
  min-width: 160px;
  flex: 1;
  cursor: pointer;
}
.timeline--horizontal .timeline__item:hover .timeline__node {
  transform: scale(1.1);
}

.timeline__node-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  z-index: 1;
}

.timeline--vertical .timeline__node-wrapper {
  flex-direction: column;
  width: 28px;
}

.timeline--horizontal .timeline__node-wrapper {
  margin-bottom: 12px;
}

.timeline__line {
  position: absolute;
  background-color: var(--timeline-line-color);
  transition: background-color 0.3s ease;
}

.timeline--vertical .timeline__line {
  left: 50%;
  top: 100%;
  transform: translateX(-50%);
  width: 2px;
  height: calc(100% + 6px);
}
.timeline--vertical .timeline__line--completed {
  background-color: var(--timeline-color-completed);
}

.timeline--horizontal .timeline__line {
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  height: 2px;
  width: calc(100% - 10px);
}
.timeline--horizontal .timeline__line--completed {
  background-color: var(--timeline-color-completed);
}

.timeline__node {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid var(--timeline-color-pending);
  background: #ffffff;
  font-size: 12px;
  font-weight: 600;
  color: #999999;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  box-sizing: border-box;
}

.timeline__node--completed {
  border-color: var(--timeline-color-completed);
  background-color: var(--timeline-color-completed);
  color: #ffffff;
}

.timeline__node--active {
  border-color: var(--timeline-color-active);
  background-color: #e6f7ff;
  color: var(--timeline-color-active);
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.15);
  animation: timeline-pulse 2s ease-in-out infinite;
}

.timeline__node--pending,
.timeline__node--skipped {
  border-color: var(--timeline-color-pending);
  background-color: #fafafa;
  color: #bfbfbf;
}

.timeline__node--skipped {
  border-style: dashed;
  opacity: 0.5;
}

.timeline__node--with-icon {
  font-size: 14px;
}

.timeline__check-icon {
  width: 14px;
  height: 14px;
}

.timeline__step-number {
  font-size: 12px;
  font-weight: 600;
}

.timeline__content {
  flex: 1;
  min-width: 0;
}

.timeline--horizontal .timeline__content {
  text-align: center;
}

.timeline__header {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.timeline--horizontal .timeline__header {
  justify-content: center;
}

.timeline__title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.5;
  color: #1a1a1a;
}

.timeline__item--active .timeline__title {
  color: #1890ff;
}

.timeline__item--skipped .timeline__title {
  text-decoration: line-through;
  opacity: 0.5;
}

.timeline__timestamp {
  font-size: 12px;
  color: #999999;
  white-space: nowrap;
}

.timeline__description {
  margin: 4px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: #666666;
}

.timeline__extra {
  margin-top: 8px;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
  color: #666666;
  border-left: 3px solid #1890ff;
}

@keyframes timeline-pulse {
  0%, 100% { box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.15); }
  50% { box-shadow: 0 0 0 8px rgba(24, 144, 255, 0.08); }
}

@media (max-width: 768px) {
  .timeline--horizontal {
    flex-direction: column;
  }
  .timeline--horizontal .timeline__item {
    flex-direction: row;
    min-width: unset;
    width: 100%;
    padding-bottom: 24px;
  }
  .timeline--horizontal .timeline__node-wrapper {
    margin-bottom: 0;
    flex-direction: column;
  }
  .timeline--horizontal .timeline__line {
    left: 50%;
    top: 100%;
    transform: translateX(-50%);
    width: 2px;
    height: calc(100% + 6px);
  }
  .timeline--horizontal .timeline__content {
    text-align: left;
  }
  .timeline--horizontal .timeline__header {
    justify-content: flex-start;
  }
}
</style>
