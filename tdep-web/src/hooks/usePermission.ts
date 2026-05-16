import { computed } from 'vue'

import { useAuthStore } from '@/stores/auth'

export function usePermission() {
  const authStore = useAuthStore()

  const isAdmin = computed(() => authStore.roles.includes('ADMIN'))
  const hasPermission = (permission: string) => isAdmin.value || authStore.permissions.includes(permission)
  const hasRole = (role: string) => isAdmin.value || authStore.roles.includes(role)

  return { isAdmin, hasPermission, hasRole }
}
