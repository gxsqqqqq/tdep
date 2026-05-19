import type { Router } from 'vue-router'
import { useAuthStore } from '@/lib/stores/auth'

export function setupRouterGuards(router: Router) {
  router.beforeEach((to, _from, next) => {
    const authStore = useAuthStore()
    const isAuthenticated = authStore.isAuthenticated
    const requiresAuth = to.meta.requiresAuth !== false

    if (requiresAuth && !isAuthenticated) {
      next({
        name: 'Login',
        query: { redirect: to.fullPath },
      })
      return
    }

    if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) {
      next({ name: 'Workspace' })
      return
    }

    next()
  })

  router.afterEach((to) => {
    const title = to.meta.title as string | undefined
    if (title) {
      document.title = `${title} - 数字司法平台`
    }
  })
}
