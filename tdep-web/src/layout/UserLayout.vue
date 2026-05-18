<template>
  <div class="user-layout">
    <header class="user-header">
      <div class="user-header__left">
        <router-link to="/my-cases" class="user-brand">
          <span class="user-brand__mark">T</span>
          <span class="user-brand__name">TDEP</span>
        </router-link>
      </div>
      <div class="user-header__right">
        <NotifyBell />
        <UserAvatar />
      </div>
    </header>
    <main class="user-main">
      <RouterView />
    </main>
    <AgentFloat />
  </div>
</template>

<script setup lang="ts">
import AgentFloat from '@/components/AgentFloat/index.vue'
import NotifyBell from '@/components/NotifyBell/index.vue'
import UserAvatar from '@/components/UserAvatar/index.vue'
import { useWebSocket } from '@/hooks/useWebSocket'

useWebSocket()
</script>
