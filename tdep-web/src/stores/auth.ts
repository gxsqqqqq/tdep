import { defineStore } from 'pinia'

import { login as loginApi, logout as logoutApi } from '@/api/auth'
import type { LoginRequest } from '@/api/types'
import { usePermissionStore } from '@/stores/permission'
import {
  clearToken,
  getAccessToken,
  getCurrentUser,
  setAccessToken,
  setCurrentUser,
  type CurrentUser
} from '@/utils/token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: getAccessToken(),
    currentUser: getCurrentUser() as CurrentUser | null
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.accessToken),
    roles: (state) => state.currentUser?.roles || [],
    permissions: (state) => state.currentUser?.permissions || []
  },
  actions: {
    async login(payload: LoginRequest) {
      const response = await loginApi(payload)
      this.accessToken = response.accessToken
      const userInfo = response.user
      this.currentUser = {
        userId: userInfo?.id || 0,
        username: userInfo?.username || payload.username,
        realName: userInfo?.nickname,
        roles: userInfo?.roles || [],
        permissions: userInfo?.permissions || []
      }
      setAccessToken(this.accessToken)
      setCurrentUser(this.currentUser)
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.clearAuth()
      }
    },
    clearAuth() {
      this.accessToken = ''
      this.currentUser = null
      clearToken()
      usePermissionStore().reset()
    }
  }
})
