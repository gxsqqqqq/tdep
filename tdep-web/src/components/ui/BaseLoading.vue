<template>
  <div
    :class="[
      'base-loading',
      `base-loading--${type}`,
      { 'base-loading--fullscreen': fullscreen }
    ]"
    :role="fullscreen ? 'dialog' : 'status'"
    :aria-label="text || '加载中'"
    :aria-busy="true"
  >
    <div
      v-if="fullscreen"
      class="base-loading__backdrop"
      :class="{ 'base-loading__backdrop--blur': blur }"
    />

    <div class="base-loading__content">
      <!-- Spinner -->
      <template v-if="type === 'spinner'">
        <div class="base-loading-spinner" :class="`base-loading-spinner--${size}`" aria-hidden="true">
          <svg class="base-loading-spinner__ring" viewBox="0 0 48 48">
            <circle
              class="base-loading-spinner__track"
              cx="24"
              cy="24"
              r="20"
              fill="none"
              stroke-width="4"
            />
          </svg>
        </div>
      </template>

      <!-- Skeleton -->
      <template v-else-if="type === 'skeleton'">
        <div class="base-loading-skeleton" aria-hidden="true">
          <div class="base-loading-skeleton__line base-loading-skeleton__line--header" />
          <div class="base-loading-skeleton__line" style="width: 80%" />
          <div class="base-loading-skeleton__line" style="width: 60%" />
          <div class="base-loading-skeleton__line" style="width: 72%" />
          <div class="base-loading-skeleton__line" style="width: 55%" />
          <div v-if="size !== 'sm'" class="base-loading-skeleton__line" style="width: 68%" />
          <div v-if="size === 'lg'" class="base-loading-skeleton__line base-loading-skeleton__line--header" style="width: 40%; margin-top: 20px;" />
          <div v-if="size === 'lg'" class="base-loading-skeleton__line" style="width: 75%" />
          <div v-if="size === 'lg'" class="base-loading-skeleton__line" style="width: 50%" />
        </div>
      </template>

      <!-- Dots -->
      <template v-else-if="type === 'dots'">
        <div class="base-loading-dots" aria-hidden="true">
          <span class="base-loading-dots__dot" />
          <span class="base-loading-dots__dot" />
          <span class="base-loading-dots__dot" />
        </div>
      </template>

      <!-- Bars -->
      <template v-else-if="type === 'bars'">
        <div class="base-loading-bars" aria-hidden="true">
          <span class="base-loading-bars__bar" />
          <span class="base-loading-bars__bar" />
          <span class="base-loading-bars__bar" />
        </div>
      </template>

      <!-- Wave -->
      <template v-else-if="type === 'wave'">
        <div class="base-loading-wave" aria-hidden="true">
          <div class="base-loading-wave__track">
            <div class="base-loading-wave__bar" v-for="i in 5" :key="i" :style="{ animationDelay: `${i * 0.08}s` }" />
          </div>
        </div>
      </template>

      <p v-if="text" class="base-loading__text">{{ text }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
export type LoadingType = 'spinner' | 'skeleton' | 'dots' | 'bars' | 'wave'

withDefaults(defineProps<{
  type?: LoadingType
  text?: string
  size?: 'sm' | 'md' | 'lg'
  fullscreen?: boolean
  blur?: boolean
}>(), {
  type: 'spinner',
  text: '',
  size: 'md',
  fullscreen: false,
  blur: false,
})
</script>

<style scoped>
.base-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
}

/* ===== Fullscreen Mode ===== */
.base-loading--fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: var(--z-modal);
  padding: 0;
}

.base-loading__backdrop {
  position: absolute;
  inset: 0;
  background-color: rgba(15, 23, 42, 0.8);
}

.base-loading__backdrop--blur {
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.base-loading--fullscreen .base-loading__content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

/* ===== Content Wrapper ===== */
.base-loading__content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.base-loading__text {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  white-space: nowrap;
}

/* ============================================
   Type 1: Spinner (conic-gradient ring)
   ============================================ */
.base-loading-spinner {
  position: relative;
  will-change: transform;
}

.base-loading-spinner--sm { width: 28px; height: 28px; }
.base-loading-spinner--md { width: 40px; height: 40px; }
.base-loading-spinner--lg { width: 56px; height: 56px; }

.base-loading-spinner__ring {
  width: 100%;
  height: 100%;
  animation: spinner-rotate 1s linear infinite;
}

.base-loading-spinner__track {
  stroke: var(--color-bg-secondary);
  stroke-dasharray: 8 100;
  animation: spinner-conic 1.2s ease-in-out infinite;
  transition: stroke 0.3s ease;
}

@keyframes spinner-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes spinner-conic {
  0% {
    stroke-dasharray: 4 120;
    stroke-dashoffset: 0;
    stroke: var(--color-primary-light);
  }
  25% {
    stroke-dasharray: 30 120;
    stroke-dashoffset: -30;
    stroke: var(--color-primary);
  }
  50% {
    stroke-dasharray: 80 120;
    stroke-dashoffset: -80;
    stroke: var(--color-primary-dark);
  }
  75% {
    stroke-dasharray: 30 120;
    stroke-dashoffset: -110;
    stroke: var(--color-primary);
  }
  100% {
    stroke-dasharray: 4 120;
    stroke-dashoffset: -125;
    stroke: var(--color-primary-light);
  }
}

/* ============================================
   Type 2: Skeleton (shimmer lines)
   ============================================ */
.base-loading-skeleton {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  max-width: 400px;
}

.base-loading-skeleton__line {
  height: 14px;
  border-radius: var(--radius-sm);
  background: linear-gradient(
    90deg,
    var(--color-bg-secondary) 25%,
    var(--color-border-light) 37%,
    var(--color-bg-secondary) 63%
  );
  background-size: 400% 100%;
  animation: skeleton-shimmer 1.6s ease-in-out infinite;
  will-change: background-position;
}

.base-loading-skeleton__line--header {
  height: 18px;
  width: 36% !important;
  margin-bottom: 4px;
}

@keyframes skeleton-shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: -100% 0; }
}

/* ============================================
   Type 3: Dots (bouncing dots)
   ============================================ */
.base-loading-dots {
  display: flex;
  align-items: center;
  gap: 8px;
}

.base-loading-dots__dot {
  width: 10px;
  height: 10px;
  border-radius: var(--radius-full);
  background-color: var(--color-primary);
  will-change: transform, opacity;
}

.base-loading-dots__dot:nth-child(1) {
  animation: dot-bounce 1.4s cubic-bezier(0.45, 0.05, 0.55, 0.95) infinite 0s;
}
.base-loading-dots__dot:nth-child(2) {
  animation: dot-bounce 1.4s cubic-bezier(0.45, 0.05, 0.55, 0.95) infinite 0.16s;
}
.base-loading-dots__dot:nth-child(3) {
  animation: dot-bounce 1.4s cubic-bezier(0.45, 0.05, 0.55, 0.95) infinite 0.32s;
}

@keyframes dot-bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* ============================================
   Type 4: Bars (bouncing bars)
   ============================================ */
.base-loading-bars {
  display: flex;
  align-items: center;
  gap: 5px;
  height: 32px;
}

.base-loading-bars__bar {
  width: 5px;
  height: 100%;
  border-radius: 3px;
  background-color: var(--color-primary);
  will-change: transform, opacity;
}

.base-loading-bars__bar:nth-child(1) {
  animation: bar-bounce 1.2s cubic-bezier(0.34, 1.56, 0.64, 1) infinite 0s;
}
.base-loading-bars__bar:nth-child(2) {
  animation: bar-bounce 1.2s cubic-bezier(0.34, 1.56, 0.64, 1) infinite 0.12s;
}
.base-loading-bars__bar:nth-child(3) {
  animation: bar-bounce 1.2s cubic-bezier(0.34, 1.56, 0.64, 1) infinite 0.24s;
}

@keyframes bar-bounce {
  0%, 100% {
    transform: scaleY(0.4);
    opacity: 0.5;
  }
  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

/* ============================================
   Type 5: Wave (waveform bars)
   ============================================ */
.base-loading-wave {
  display: flex;
  align-items: center;
}

.base-loading-wave__track {
  display: flex;
  align-items: flex-end;
  gap: 3px;
  height: 28px;
}

.base-loading-wave__bar {
  width: 4px;
  border-radius: 2px;
  background: linear-gradient(to top, var(--color-primary-dark), var(--color-primary));
  animation: wave-motion 1s ease-in-out infinite;
  will-change: height;
}

@keyframes wave-motion {
  0%, 100% {
    height: 8px;
    opacity: 0.5;
  }
  50% {
    height: 28px;
    opacity: 1;
  }
}
</style>
