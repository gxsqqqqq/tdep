import type { AppRouteRecord, MenuItem } from '@/router/types'

export function hasRoutePermission(route: AppRouteRecord, roles: string[], permissions: string[]) {
  const meta = route.meta
  if (!meta) return true
  if (roles.includes('ADMIN')) return true
  const roleAllowed = !meta.roles?.length || meta.roles.some((role) => roles.includes(role))
  const permissionAllowed = (!meta.permission || permissions.includes(meta.permission))
    && (!meta.permissions?.length || meta.permissions.some((permission) => permissions.includes(permission)))
  return roleAllowed && permissionAllowed
}

export function filterRoutes(routes: AppRouteRecord[], roles: string[], permissions: string[]): AppRouteRecord[] {
  return routes
    .filter((route) => hasRoutePermission(route, roles, permissions))
    .map((route) => ({
      ...route,
      children: route.children ? filterRoutes(route.children, roles, permissions) : undefined
    }))
}

export function routesToMenus(routes: AppRouteRecord[]): MenuItem[] {
  return routes.flatMap((route) => {
    const children = route.children?.filter((child) => !child.meta?.hidden) || []
    if ((route.path === '/' || route.path === '/admin') && children.length) {
      return children.map(toMenuItem)
    }
    if (route.meta?.hidden) return []
    return [toMenuItem(route)]
  })
}

function toMenuItem(route: AppRouteRecord): MenuItem {
  const path = route.path.startsWith('/') ? route.path : `/${route.path}`
  return {
    path,
    title: route.meta?.title || String(route.name || path),
    icon: route.meta?.icon,
    permission: route.meta?.permission,
    roles: route.meta?.roles,
    children: route.children ? routesToMenus(route.children) : undefined
  }
}
