import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const users = ref<User[]>([])
  const currentUser = ref<User | null>(null)
  const loading = ref(false)

  async function fetchUsers(params?: Record<string, unknown>) {
    loading.value = true
    try {
      const response = await fetch('/api/users', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
      const data = await response.json()
      if (data.success) {
        users.value = data.data.list
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchUserById(id: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/users/${id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
      const data = await response.json()
      if (data.success) {
        currentUser.value = data.data
      }
    } finally {
      loading.value = false
    }
  }

  return {
    users,
    currentUser,
    loading,
    fetchUsers,
    fetchUserById,
  }
})
