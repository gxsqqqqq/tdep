import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Evidence } from '@/types/evidence'

export const useEvidenceStore = defineStore('evidence', () => {
  const evidences = ref<Evidence[]>([])
  const currentEvidence = ref<Evidence | null>(null)
  const loading = ref(false)

  async function fetchEvidences(caseId: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/cases/${caseId}/evidences`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
      const data = await response.json()
      if (data.success) {
        evidences.value = data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function uploadEvidence(caseId: string, file: File) {
    loading.value = true
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch(`/api/cases/${caseId}/evidences/upload`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: formData,
      })

      const data = await response.json()
      if (data.success) {
        evidences.value.push(data.data)
        return data.data
      }
      throw new Error(data.message)
    } finally {
      loading.value = false
    }
  }

  async function deleteEvidence(evidenceId: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/evidences/${evidenceId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })

      const data = await response.json()
      if (data.success) {
        evidences.value = evidences.value.filter((e) => e.id !== evidenceId)
      }
      throw new Error(data.message)
    } finally {
      loading.value = false
    }
  }

  return {
    evidences,
    currentEvidence,
    loading,
    fetchEvidences,
    uploadEvidence,
    deleteEvidence,
  }
})
