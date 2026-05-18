<template>
  <el-container class="app-shell">
    <el-aside class="app-sidebar" :width="appStore.sidebarCollapsed ? '72px' : '248px'">
      <AppSidebar />
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <AppHeader />
      </el-header>
      <div class="app-toolbar">
        <AppBreadcrumb />
        <AppTabs />
      </div>
      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import '@/styles/admin.css'

import AppBreadcrumb from '@/components/AppBreadcrumb/index.vue'
import AppTabs from '@/components/AppTabs/index.vue'
import { useWebSocket } from '@/hooks/useWebSocket'
import { useAppStore } from '@/stores/app'

import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'

const appStore = useAppStore()
useWebSocket()
</script>
