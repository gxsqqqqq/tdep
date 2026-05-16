<template>
  <div class="app-tabs">
    <el-tag
      v-for="tab in tabsStore.tabs"
      :key="tab.path"
      :closable="tab.path !== '/dashboard'"
      :effect="route.fullPath === tab.path ? 'dark' : 'plain'"
      @click="router.push(tab.path)"
      @close="closeTab(tab.path)"
    >
      {{ tab.title }}
    </el-tag>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()

function closeTab(path: string) {
  tabsStore.remove(path)
  if (route.fullPath === path) {
    router.push(tabsStore.tabs[tabsStore.tabs.length - 1]?.path || '/dashboard')
  }
}
</script>
