import type { NavigationGuard } from 'vue-router'
import { useAuthStore } from '@/lib/stores/auth'
import { UserRole } from '@/lib/constants/roles'

export const roleGuard: NavigationGuard = (to, _from, next) => {
  const authStore = useAuthStore()
  const requiredRoles = to.meta.roles as UserRole[] | undefined

  if (requiredRoles?.length) {
    const userRole = authStore.role as UserRole
    const hasRequiredRole = requiredRoles.includes(userRole)

    if (!hasRequiredRole) {
      next({ name: 'Workspace' })
      return
    }
  }

  next()
}
