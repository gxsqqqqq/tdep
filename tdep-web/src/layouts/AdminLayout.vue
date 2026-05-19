<template>
  <div class="admin-layout">
    <AppHeader />
    <div class="admin-container">
      <AppSidebar />
      <main class="admin-main">
        <AppBreadcrumb />
        <router-view />
      </main>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/layout/AppHeader/index.vue'
import AppSidebar from '@/components/layout/AppSidebar/index.vue'
import AppBreadcrumb from '@/components/layout/AppBreadcrumb/index.vue'
import AppFooter from '@/components/layout/AppFooter/index.vue'
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.admin-container {
  display: flex;
  flex: 1;
  margin-top: 64px;
}

.admin-main {
  flex: 1;
  padding: var(--spacing-lg);
  overflow-y: auto;
  background-color: var(--color-bg-secondary);
}
</style>
