import { ref, computed } from 'vue'
import { useDocumentStore } from '@/lib/stores/document'

export function useDocument() {
  const documentStore = useDocumentStore()
  const loading = ref(false)
  const error = ref<string | null>(null)

  const documents = computed(() => documentStore.documents)
  const currentDocument = computed(() => documentStore.currentDocument)

  async function fetchDocuments(caseId: string) {
    loading.value = true
    error.value = null
    try {
      await documentStore.fetchDocuments(caseId)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取文档列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function previewDocument(documentId: string) {
    loading.value = true
    error.value = null
    try {
      const url = await documentStore.previewDocument(documentId)
      return url
    } catch (e) {
      error.value = e instanceof Error ? e.message : '预览文档失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function signDocument(documentId: string, signatureData: string) {
    loading.value = true
    error.value = null
    try {
      await documentStore.signDocument(documentId, signatureData)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '签署文档失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    documents: documents as any,
    currentDocument: currentDocument as any,
    loading,
    error,
    fetchDocuments,
    previewDocument,
    signDocument
  }
}
