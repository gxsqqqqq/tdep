<template>
  <div class="header-left">
    <el-button :icon="appStore.sidebarCollapsed ? Expand : Fold" text @click="appStore.toggleSidebar()" />
    <div>
      <strong>{{ route.meta.title || '工作台' }}</strong>
      <small>Trusted Digital Evidence Platform</small>
    </div>
  </div>

  <div class="header-actions">
    <NotifyPopover />
    <el-button :icon="appStore.darkMode ? Sunny : Moon" circle @click="appStore.toggleDarkMode()" />
    <el-dropdown @command="handleCommand">
      <button class="user-entry">
        <el-avatar :size="30">{{ usernameInitial }}</el-avatar>
        <span>{{ authStore.currentUser?.realName || authStore.currentUser?.username || '用户' }}</span>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">用户中心</el-dropdown-item>
          <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { Expand, Fold, Moon, Sunny } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import NotifyPopover from '@/components/NotifyPopover/index.vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const usernameInitial = computed(() => (authStore.currentUser?.username || 'T').slice(0, 1).toUpperCase())

async function handleCommand(command: string) {
  if (command === 'logout') {
    await authStore.logout()
    router.replace('/login')
  }
}
</script>
