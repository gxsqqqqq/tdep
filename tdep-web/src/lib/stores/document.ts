import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Document } from '@/types/document'

export const useDocumentStore = defineStore('document', () => {
  const documents = ref<Document[]>([])
  const currentDocument = ref<Document | null>(null)
  const loading = ref(false)

  async function fetchDocuments(caseId: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/cases/${caseId}/documents`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
      const data = await response.json()
      if (data.success) {
        documents.value = data.data
      }
    } finally {
      loading.value = false
    }
  }

  async function previewDocument(documentId: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/documents/${documentId}/preview`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      })
      const blob = await response.blob()
      return URL.createObjectURL(blob)
    } finally {
      loading.value = false
    }
  }

  async function signDocument(documentId: string, signatureData: string) {
    loading.value = true
    try {
      const response = await fetch(`/api/documents/${documentId}/sign`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({ signatureData }),
      })

      const data = await response.json()
      if (!data.success) {
        throw new Error(data.message)
      }
    } finally {
      loading.value = false
    }
  }

  return {
    documents,
    currentDocument,
    loading,
    fetchDocuments,
    previewDocument,
    signDocument,
  }
})
