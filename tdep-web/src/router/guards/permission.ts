import type { NavigationGuard } from 'vue-router'
import { usePermission } from '@/composables/usePermission'

export const permissionGuard: NavigationGuard = (to, _from, next) => {
  const { hasPermission } = usePermission()
  const requiredPermissions = to.meta.permissions as string[] | undefined

  if (requiredPermissions?.length) {
    const hasAllPermissions = requiredPermissions.every((perm) =>
      hasPermission(perm)
    )

    if (!hasAllPermissions) {
      next({ name: 'Workspace' })
      return
    }
  }

  next()
}
