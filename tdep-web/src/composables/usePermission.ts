import { computed } from 'vue'
import { useAuthStore } from '@/lib/stores/auth'
import { UserRole } from '@/lib/constants/roles'

export function usePermission() {
  const authStore = useAuthStore()

  const userRole = computed(() => authStore.role)
  const permissions = computed(() => authStore.permissions)

  function hasPermission(permission: string | string[]) {
    const permArray = Array.isArray(permission) ? permission : [permission]
    return permArray.some(p => permissions.value.includes(p))
  }

  function hasAnyRole(roles: UserRole | UserRole[]) {
    const roleArray = Array.isArray(roles) ? roles : [roles]
    return roleArray.includes(userRole.value as UserRole)
  }

  function canAccess(resource: string, action: string) {
    const permission = `${resource}:${action}`
    return hasPermission(permission)
  }

  return {
    userRole,
    permissions,
    hasPermission,
    hasAnyRole,
    canAccess
  }
}
