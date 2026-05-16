import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import { usePermissionStore } from '@/stores/permission'
import { useTabsStore } from '@/stores/tabs'

import { constantRoutes } from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes as RouteRecordRaw[]
})

const whiteList = ['/login', '/403', '/404']

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  const permissionStore = usePermissionStore()

  document.title = to.meta.title ? `${String(to.meta.title)} - TDEP` : 'Trusted Digital Evidence Platform'

  if (whiteList.includes(to.path)) {
    if (to.path === '/login' && authStore.isAuthenticated) {
      return '/dashboard'
    }
    return true
  }

  if (!authStore.isAuthenticated) {
    return `/login?redirect=${encodeURIComponent(to.fullPath)}`
  }

  if (!permissionStore.loaded) {
    const routes = permissionStore.generateRoutes(authStore.roles, authStore.permissions)
    routes.forEach((route) => router.addRoute(route as RouteRecordRaw))
    return to.fullPath
  }

  const requiredPermission = to.meta.permission as string | undefined
  const requiredPermissions = to.meta.permissions as string[] | undefined
  const requiredRoles = to.meta.roles as string[] | undefined
  const hasPermission = !requiredPermission || authStore.permissions.includes(requiredPermission) || authStore.roles.includes('ADMIN')
  const hasAnyPermission = !requiredPermissions?.length || requiredPermissions.some((permission) => authStore.permissions.includes(permission)) || authStore.roles.includes('ADMIN')
  const hasRole = !requiredRoles?.length || requiredRoles.some((role) => authStore.roles.includes(role)) || authStore.roles.includes('ADMIN')
  if (!hasPermission || !hasAnyPermission || !hasRole) {
    return '/403'
  }

  return true
})

router.afterEach((to) => {
  const tabsStore = useTabsStore()
  tabsStore.add(to)
})

export default router
