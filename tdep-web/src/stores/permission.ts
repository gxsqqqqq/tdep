import { defineStore } from 'pinia'

import { asyncRoutes, fallbackRoute } from '@/router/routes'
import type { AppRouteRecord, MenuItem } from '@/router/types'
import { filterRoutes, routesToMenus } from '@/permission/menu'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [] as AppRouteRecord[],
    menus: [] as MenuItem[],
    loaded: false
  }),
  actions: {
    generateRoutes(roles: string[], permissions: string[]) {
      const accessibleRoutes = filterRoutes(asyncRoutes, roles, permissions)
      this.routes = [...accessibleRoutes, fallbackRoute]
      this.menus = routesToMenus(accessibleRoutes)
      this.loaded = true
      return this.routes
    },
    reset() {
      this.routes = []
      this.menus = []
      this.loaded = false
    }
  }
})
