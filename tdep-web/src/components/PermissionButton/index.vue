<template>
  <el-button v-if="visible" v-bind="$attrs">
    <slot />
  </el-button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import { useAuthStore } from '@/stores/auth'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  permission?: string
  permissions?: string[]
  roles?: string[]
}>()

const authStore = useAuthStore()

const visible = computed(() => {
  if (authStore.roles.includes('ADMIN')) return true
  const roleAllowed = !props.roles?.length || props.roles.some((role) => authStore.roles.includes(role))
  const permissionAllowed = (!props.permission || authStore.permissions.includes(props.permission))
    && (!props.permissions?.length || props.permissions.some((permission) => authStore.permissions.includes(permission)))
  return roleAllowed && permissionAllowed
})
</script>
