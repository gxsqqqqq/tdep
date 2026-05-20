<template>
  <button
    class="theme-toggle"
    :class="{ 'theme-toggle--dark': isDark }"
    :title="tooltipText"
    @click="toggle"
    aria-label="切换主题"
    type="button"
  >
    <Transition name="theme-icon" mode="out-in">
      <svg
        v-if="isDark"
        key="moon"
        class="theme-toggle__icon"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
      </svg>
      <svg
        v-else
        key="sun"
        class="theme-toggle__icon"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <circle cx="12" cy="12" r="5" />
        <line x1="12" y1="1" x2="12" y2="3" />
        <line x1="12" y1="21" x2="12" y2="23" />
        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
        <line x1="1" y1="12" x2="3" y2="12" />
        <line x1="21" y1="12" x2="23" y2="12" />
        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
      </svg>
    </Transition>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useDarkMode } from '@/composables/useDarkMode'

const { isDark, toggle, theme } = useDarkMode()

const tooltipText = computed(() => {
  if (isDark.value) return '当前: 深色模式 (点击切换浅色)'
  return '当前: 浅色模式 (点击切换深色)'
})
</script>

<style scoped>
.theme-toggle {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  padding: 0;
  border: 1px solid var(--color-border-primary);
  border-radius: 50%;
  background-color: var(--color-bg-secondary);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.theme-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15),
              0 4px 12px rgba(0, 0, 0, 0.1);
}

.theme-toggle:focus-visible {
  outline: 2px solid var(--color-primary-500);
  outline-offset: 2px;
}

.theme-toggle--dark {
  border-color: var(--color-border-strong);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.theme-toggle--dark:hover {
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.25),
              0 0 20px rgba(59, 130, 246, 0.3),
              0 4px 12px rgba(0, 0, 0, 0.3);
}

.theme-toggle__icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-toggle:hover .theme-toggle__icon {
  transform: rotate(15deg);
}

/* 图标过渡动画 */
.theme-icon-enter-active,
.theme-icon-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-icon-enter-from {
  opacity: 0;
  transform: scale(0.5) rotate(-90deg);
}

.theme-icon-leave-to {
  opacity: 0;
  transform: scale(0.5) rotate(90deg);
}
</style>
