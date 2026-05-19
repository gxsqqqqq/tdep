import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginRequest, LoginResponse } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.roles?.[0] || null)
  const isAdmin = computed(() => user.value?.roles?.includes('ADMIN') || false)

  // Actions
  async function login(credentials: LoginRequest): Promise<LoginResponse> {
    loading.value = true
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
      })
      
      const data: LoginResponse = await response.json()
      
      if (data.code === 0) {
        token.value = data.data.accessToken
        user.value = data.data.user
        localStorage.setItem('token', data.data.accessToken)
        localStorage.setItem('user', JSON.stringify(data.data.user))
      }
      
      return data
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function restoreSession() {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      token.value = savedToken
      try {
        user.value = JSON.parse(savedUser)
      } catch {
        logout()
      }
    }
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    userRole,
    isAdmin,
    login,
    logout,
    restoreSession,
  }
})
