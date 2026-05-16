import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    darkMode: false
  }),
  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    toggleDarkMode() {
      this.darkMode = !this.darkMode
      document.documentElement.classList.toggle('dark', this.darkMode)
    }
  }
})
