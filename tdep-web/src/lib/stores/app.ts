import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@/lib/utils/storage'

export interface Notification {
  id: string
  title: string
  content: string
  type: 'info' | 'success' | 'warning' | 'error'
  read: boolean
  createdAt: string
  link?: string
}

export interface AppState {
  theme: 'light' | 'dark'
  sidebarCollapsed: boolean
  notifications: Notification[]
  unreadCount: number
}

export const useAppStore = defineStore('app', () => {
  const theme = ref<'light' | 'dark'>(
    (storage.getTheme() as 'light' | 'dark') || 'light'
  )
  const sidebarCollapsed = ref(false)
  const notifications = ref<Notification[]>([])
  const unreadCount = computed(() =>
    notifications.value.filter((n) => !n.read).length
  )

  function toggleTheme(): void {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    storage.setTheme(theme.value)
    applyTheme(theme.value)
  }

  function setTheme(t: 'light' | 'dark'): void {
    theme.value = t
    storage.setTheme(t)
    applyTheme(t)
  }

  function applyTheme(t: 'light' | 'dark'): void {
    document.documentElement.setAttribute('data-theme', t)
    if (t === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  function toggleSidebar(): void {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setSidebarCollapsed(collapsed: boolean): void {
    sidebarCollapsed.value = collapsed
  }

  function addNotification(notification: Omit<Notification, 'id' | 'createdAt' | 'read'>): Notification {
    const newNotification: Notification = {
      ...notification,
      id: `notify_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`,
      read: false,
      createdAt: new Date().toISOString(),
    }
    notifications.value.unshift(newNotification)
    return newNotification
  }

  function markAsRead(id: string): void {
    const notification = notifications.value.find((n) => n.id === id)
    if (notification) {
      notification.read = true
    }
  }

  function markAllAsRead(): void {
    notifications.value.forEach((n) => {
      n.read = true
    })
  }

  function removeNotification(id: string): void {
    notifications.value = notifications.value.filter((n) => n.id !== id)
  }

  function clearNotifications(): void {
    notifications.value = []
  }

  function initTheme(): void {
    applyTheme(theme.value)
  }

  return {
    theme,
    sidebarCollapsed,
    notifications,
    unreadCount,
    toggleTheme,
    setTheme,
    applyTheme,
    toggleSidebar,
    setSidebarCollapsed,
    addNotification,
    markAsRead,
    markAllAsRead,
    removeNotification,
    clearNotifications,
    initTheme,
  }
})
