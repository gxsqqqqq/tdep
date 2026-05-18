import { defineStore } from 'pinia'

import { downloadDocument, previewDocumentPdf } from '@/api/document'
import type { DocumentItem } from '@/api/models'

export const useDocumentStore = defineStore('document', {
  state: () => ({
    documents: [] as DocumentItem[],
    loading: false
  }),
  actions: {
    addDocument(doc: DocumentItem) {
      const exists = this.documents.findIndex((d) => d.id === doc.id)
      if (exists >= 0) {
        this.documents[exists] = doc
      } else {
        this.documents.unshift(doc)
      }
    },
    async previewDocument(id: number | string) {
      const result = await previewDocumentPdf(id)
      const url = result.downloadUrl || result.url
      if (url) window.open(url, '_blank')
    },
    async downloadDocument(id: number | string, format?: string) {
      const result = await downloadDocument(id, format)
      const url = result.downloadUrl || result.url
      if (url) window.open(url, '_blank')
    }
  }
})
