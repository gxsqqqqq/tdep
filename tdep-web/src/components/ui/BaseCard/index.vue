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
      <div
        v-if="$slots.header || title"
        class="base-card__header"
        :class="{ 'base-card__header--gradient': headerGradient }"
      >
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

export type CardVariant =
  | 'bordered'
  | 'shadow'
  | 'flat'
  | 'elevated'
  | 'judicial'
  | 'gold'
  | 'blockchain'
  | 'audit'

export type CardPadding = 'none' | 'sm' | 'md' | 'lg'

const props = withDefaults(defineProps<{
  variant?: CardVariant
  padding?: CardPadding
  hoverable?: boolean
  interactive?: boolean
  loading?: boolean
  title?: string
  bodyStyle?: Record<string, string>
  glow?: boolean
  headerGradient?: boolean
  compact?: boolean
}>(), {
  variant: 'bordered',
  padding: 'md',
  hoverable: false,
  interactive: false,
  loading: false,
  glow: false,
  headerGradient: false,
  compact: false,
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
    'base-card--glow': props.glow,
    'base-card--compact': props.compact,
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
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ══════════════════════════════════════════
   Variant 样式
   ══════════════════════════════════════════ */

/* ── Bordered - 带边框（默认） ── */
.base-card--bordered {
  border: 1px solid #e2e8f0;
}

/* ── Shadow - 阴影卡片 ── */
.base-card--shadow {
  border: none;
  box-shadow: var(--shadow-sm, 0 1px 2px rgba(0, 0, 0, 0.05));
}

/* ── Flat - 扁平无边框 ── */
.base-card--flat {
  border: none;
  background-color: #f8fafc;
}

/* ── Elevated - 高悬浮阴影 ── */
.base-card--elevated {
  border: none;
  box-shadow: var(--shadow-md, 0 4px 6px rgba(0, 0, 0, 0.1));
}

/* ── Judicial - 司法卡片 ⭐ ── */
.base-card--judicial {
  background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%);
  color: white;
  border: none;
  box-shadow: var(--shadow-judicial, 0 8px 24px rgba(37, 99, 235, 0.25));
}
.base-card--judicial .base-card__title {
  color: white;
  font-weight: 700;
}
.base-card--judicial .base-card__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 16px;
  margin-bottom: 16px;
}
.base-card--judicial .base-card__body {
  color: rgba(255, 255, 255, 0.9);
}

/* ── Gold - 金色签章卡片 ⭐ ── */
.base-card--gold {
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border: 1px solid #fbbf24;
  box-shadow: var(--shadow-seal, 0 4px 16px rgba(251, 191, 36, 0.2));
}
.base-card--gold .base-card__title {
  color: #92400e;
  font-weight: 600;
}

/* ── Blockchain - 区块链卡片 ⭐ ── */
.base-card--blockchain {
  background: linear-gradient(135deg, #1e1b4b 0%, #0f172a 100%);
  color: white;
  border: 1px solid rgba(139, 92, 246, 0.3);
  box-shadow: var(--shadow-blockchain, 0 8px 24px rgba(124, 58, 237, 0.15));
}
.base-card--blockchain .base-card__title {
  color: #c4b5fd;
  font-weight: 600;
}
.base-card--blockchain .base-card__body {
  color: rgba(255, 255, 255, 0.85);
}

/* ── Audit - 审计日志卡片 ── */
.base-card--audit {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  box-shadow: none;
}
.base-card--audit .base-card__title {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
}

/* ══════════════════════════════════════════
   发光效果（仅 judicial/gold/blockchain 生效）
   ══════════════════════════════════════════ */
.base-card--glow.base-card--judicial {
  animation: glow-judicial 2s ease-in-out infinite alternate;
}

.base-card--glow.base-card--gold {
  animation: glow-gold 2.5s ease-in-out infinite alternate;
}

.base-card--glow.base-card--blockchain {
  animation: glow-blockchain 2s ease-in-out infinite alternate;
}

@keyframes glow-judicial {
  from { box-shadow: 0 8px 24px rgba(37, 99, 235, 0.25); }
  to { box-shadow: 0 8px 32px rgba(37, 99, 235, 0.45), 0 0 60px rgba(37, 99, 235, 0.15); }
}

@keyframes glow-gold {
  from { box-shadow: 0 4px 16px rgba(251, 191, 36, 0.2); }
  to { box-shadow: 0 4px 24px rgba(251, 191, 36, 0.4), 0 0 40px rgba(251, 191, 36, 0.12); }
}

@keyframes glow-blockchain {
  from { box-shadow: 0 8px 24px rgba(124, 58, 237, 0.15); }
  to { box-shadow: 0 8px 32px rgba(124, 58, 237, 0.35), 0 0 50px rgba(124, 58, 237, 0.1); }
}

/* ══════════════════════════════════════════
   交互增强
   ══════════════════════════════════════════ */

/* Hoverable - 上浮效果 */
.base-card--hoverable:hover {
  box-shadow: var(--shadow-lg, 0 10px 15px rgba(0, 0, 0, 0.1));
  transform: translateY(-2px);
}

/* Interactive - 可点击样式 */
.base-card--interactive {
  cursor: pointer;
}
.base-card--interactive:hover {
  border-color: #2563eb;
}
.base-card--interactive:focus-visible {
  outline: none;
  box-shadow: 0 0 0 2px var(--color-primary-500, #2563eb), 0 0 0 5px rgba(37, 99, 235, 0.12);
}
.base-card--interactive:active {
  transform: scale(0.98);
}

/* ══════════════════════════════════════════
   紧凑模式
   ══════════════════════════════════════════ */
.base-card--compact .base-card__header {
  padding: 12px 16px 0 !important;
  min-height: auto !important;
}
.base-card--compact .base-card__body {
  padding-top: 12px !important;
}
.base-card--compact .base-card__footer {
  padding: 8px 16px 12px !important;
}
.base-card--compact .base-card__skeleton {
  padding: 12px !important;
}

/* ══════════════════════════════════════════
   内边距规范
   ══════════════════════════════════════════ */
.base-card--padding-none .base-card__body { padding: 0; }
.base-card--padding-sm .base-card__body { padding: 12px; }
.base-card--padding-md .base-card__body { padding: 20px; }
.base-card--padding-lg .base-card__body { padding: 28px; }

/* ══════════════════════════════════════════
   内部结构
   ══════════════════════════════════════════ */
.base-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 0;
  min-height: 48px;
}

.base-card__header--gradient {
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.08) 0%, transparent 100%);
  margin: 0 -20px 0;
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 20px;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid rgba(37, 99, 235, 0.08);
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

/* ══════════════════════════════════════════
   Skeleton 骨架屏（司法蓝渐变 + 脉冲效果）
   ══════════════════════════════════════════ */
.base-card__skeleton {
  padding: 20px;
}

.base-card__skeleton-line {
  height: 14px;
  background: linear-gradient(
    90deg,
    #eff6ff 0%,
    #dbeafe 40%,
    #bfdbfe 50%,
    #dbeafe 60%,
    #eff6ff 100%
  );
  background-size: 200% 100%;
  animation:
    skeleton-shimmer 1.5s ease-in-out infinite,
    skeleton-pulse 2s ease-in-out infinite alternate;
  border-radius: 4px;
  margin-bottom: 10px;
}

.base-card__skeleton-line--header {
  height: 22px;
  width: 40%;
  margin-bottom: 16px;
  border-radius: 6px;
}

@keyframes skeleton-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@keyframes skeleton-pulse {
  from { opacity: 0.6; }
  to { opacity: 1; }
}

/* ══════════════════════════════════════════
   深色模式适配
   ══════════════════════════════════════════ */
:deep(.dark) .base-card--bordered {
  background-color: var(--color-bg-secondary, #111827);
  border-color: var(--color-border-primary, #1f2937);
}

:deep(.dark) .base-card--shadow {
  background-color: var(--color-bg-secondary, #111827);
  box-shadow: var(--shadow-md, 0 4px 6px rgba(0, 0, 0, 0.3));
}

:deep(.dark) .base-card--flat {
  background-color: var(--color-bg-secondary, #0d1117);
}

:deep(.dark) .base-card--elevated {
  background-color: var(--color-bg-elevated, #161b22);
  box-shadow: var(--shadow-lg, 0 10px 15px rgba(0, 0, 0, 0.35));
}

:deep(.dark) .base-card--judicial {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 100%);
  box-shadow: 0 8px 32px rgba(37, 99, 235, 0.3);
}

:deep(.dark) .base-card--gold {
  background: linear-gradient(135deg, #292524 0%, #1c1917 100%);
  border-color: var(--color-gold-500, #f59e0b);
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.15);
}
:deep(.dark) .base-card--gold .base-card__title {
  color: var(--color-gold-300, #fcd34d);
}

:deep(.dark) .base-card--blockchain {
  background: linear-gradient(135deg, #0c0a1d 0%, #070510 100%);
  border-color: rgba(139, 92, 246, 0.4);
  box-shadow: 0 8px 28px rgba(124, 58, 237, 0.25);
}

:deep(.dark) .base-card--audit {
  background-color: #0d1117;
  border-color: #21262d;
}
:deep(.dark) .base-card--audit .base-card__title {
  color: #94a3b8;
}

:deep(.dark) .base-card__title {
  color: var(--color-text-primary, #f3f4f6);
}

:deep(.dark) .base-card__footer {
  border-top-color: var(--color-border-primary, #1f2937);
}

:deep(.dark) .base-card--hoverable:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.4);
}

:deep(.dark) .base-card--interactive:hover {
  border-color: var(--color-primary-400, #60a5fa);
}

:deep(.dark) .base-card--interactive:focus-visible {
  box-shadow: 0 0 0 2px var(--color-primary-400, #60a5fa), 0 0 0 5px rgba(96, 165, 250, 0.12);
}

:deep(.dark) .base-card__header--gradient {
  background: linear-gradient(180deg, rgba(96, 165, 250, 0.06) 0%, transparent 100%);
  border-bottom-color: rgba(96, 165, 250, 0.06);
}

:deep(.dark) .base-card__skeleton-line {
  background: linear-gradient(
    90deg,
    rgba(30, 41, 59, 0.8) 0%,
    rgba(51, 65, 85, 0.9) 40%,
    rgba(71, 85, 105, 1) 50%,
    rgba(51, 65, 85, 0.9) 60%,
    rgba(30, 41, 59, 0.8) 100%
  );
  background-size: 200% 100%;
}
</style>
