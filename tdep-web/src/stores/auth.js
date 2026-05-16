import { defineStore } from 'pinia'

// 认证状态仓库，后续对接登录接口后统一维护令牌和用户信息。
export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: '',
    currentUser: null,
    permissions: []
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.accessToken)
  },
  actions: {
    setToken(token) {
      this.accessToken = token
    },
    clearAuth() {
      this.accessToken = ''
      this.currentUser = null
      this.permissions = []
    }
  }
})
