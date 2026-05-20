<template>
  <div id="app-root" class="app-container">
    <router-view v-slot="{ Component, route }" class="page-wrapper">
      <transition
        :name="transitionName"
        mode="out-in"
        :appear="true"
        @before-leave="onBeforeLeave"
        @after-enter="onAfterEnter"
        @after-leave="onAfterLeave"
      >
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>

    <div class="global-toast-container" />

    <BaseLoading
      v-if="isGlobalLoading"
      fullscreen
      type="spinner"
      text="正在加载..."
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import BaseLoading from '@/components/ui/BaseLoading.vue'
import { useDarkMode } from '@/composables/useDarkMode'

const route = useRoute()
const { init: initDarkMode } = useDarkMode()

onMounted(() => {
  initDarkMode()
})

const transitionName = computed(() => {
  const depth = (route.meta?.transitionDepth as number) || 0

  if (depth <= 1) return 'fade'

  if (depth === 2) return 'slide-left'

  return 'slide-right'
})

const isGlobalLoading = ref(false)

function onBeforeLeave(el: Element) {
  el.classList.add('page-leaving')
}

function onAfterEnter(el: Element) {
  el.classList.add('page-entered')
  setTimeout(() => el.classList.remove('page-entered'), 300)
}

function onAfterLeave(el: Element) {
  el.classList.remove('page-leaving')
}
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-left-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-right-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 1, 1);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.page-leaving {
  pointer-events: none;
}

.page-entered {
  animation: page-arrive 0.3s ease-out;
}

@keyframes page-arrive {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.app-container {
  min-height: 100vh;
  background-color: var(--color-bg-primary);
  transition: background-color 0.3s ease;
}

.page-wrapper {
  position: relative;
  min-height: 100vh;
}

.global-toast-container {
  position: fixed;
  top: 80px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 8px;
  pointer-events: none;
}
</style>
