import { defineStore } from 'pinia'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface TabItem {
  path: string
  title: string
}

export const useTabsStore = defineStore('tabs', {
  state: () => ({
    tabs: [{ path: '/dashboard', title: '工作台' }] as TabItem[]
  }),
  actions: {
    add(route: RouteLocationNormalizedLoaded) {
      if (route.meta.hidden) return
      const path = route.fullPath
      if (!this.tabs.some((tab) => tab.path === path)) {
        this.tabs.push({ path, title: String(route.meta.title || route.name || path) })
      }
    },
    remove(path: string) {
      this.tabs = this.tabs.filter((tab) => tab.path !== path || tab.path === '/dashboard')
    }
  }
})
