<template>
  <el-dropdown @command="handleCommand">
    <div class="user-avatar-menu">
      <el-avatar :size="32">{{ usernameInitial }}</el-avatar>
      <span class="user-avatar-menu__name">{{ displayName }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-if="isAdmin" command="admin">管理后台</el-dropdown-item>
        <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const usernameInitial = computed(() => (authStore.currentUser?.username || 'T').slice(0, 1).toUpperCase())
const displayName = computed(() => authStore.currentUser?.realName || authStore.currentUser?.username || '用户')
const isAdmin = computed(() => authStore.roles.includes('ADMIN'))

async function handleCommand(command: string) {
  if (command === 'logout') {
    await authStore.logout()
    router.replace('/login')
  } else if (command === 'admin') {
    router.push('/admin/dashboard')
  }
}
</script>
