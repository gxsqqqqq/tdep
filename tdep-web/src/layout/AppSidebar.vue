<template>
  <div class="brand" :class="{ collapsed: appStore.sidebarCollapsed }">
    <span class="brand-mark">T</span>
    <div v-if="!appStore.sidebarCollapsed" class="brand-copy">
      <strong>TDEP</strong>
      <small>可信数字证据平台</small>
    </div>
  </div>

  <el-menu
    router
    class="nav-menu"
    :collapse="appStore.sidebarCollapsed"
    :default-active="activePath"
    background-color="#0b1f3a"
    text-color="#b8c7dd"
    active-text-color="#ffffff"
  >
    <el-menu-item v-for="item in permissionStore.menus" :key="item.path" :index="item.path">
      <el-icon><component :is="item.icon || 'Menu'" /></el-icon>
      <span>{{ item.title }}</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import { useAppStore } from '@/stores/app'
import { usePermissionStore } from '@/stores/permission'

const route = useRoute()
const appStore = useAppStore()
const permissionStore = usePermissionStore()
const activePath = computed(() => `/${route.path.split('/').filter(Boolean).slice(0, 2).join('/')}`)
</script>
