import type { App, DirectiveBinding } from 'vue'

import { useAuthStore } from '@/stores/auth'

function checkPermission(permission: string) {
  const authStore = useAuthStore()
  return authStore.roles.includes('ADMIN') || authStore.permissions.includes(permission)
}

export function setupPermissionDirective(app: App) {
  app.directive('permission', {
    mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
      if (binding.value && !checkPermission(binding.value)) {
        el.parentNode?.removeChild(el)
      }
    }
  })
}
