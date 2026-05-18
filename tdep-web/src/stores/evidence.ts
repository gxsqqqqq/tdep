import { defineStore } from 'pinia'

import { getEvidenceList, uploadEvidence, verifyEvidence } from '@/api/evidence'
import type { EvidenceItem } from '@/api/models'

export const useEvidenceStore = defineStore('evidence', {
  state: () => ({
    evidences: [] as EvidenceItem[],
    loading: false,
    uploading: false
  }),
  actions: {
    async loadEvidences(caseId: number | string) {
      this.loading = true
      try {
        const page = await getEvidenceList({ caseId })
        this.evidences = page.records || []
        return page
      } finally {
        this.loading = false
      }
    },
    async uploadEvidence(file: File, caseId: number | string, description?: string) {
      this.uploading = true
      try {
        const evidence = await uploadEvidence(file, { caseId, description: description || '' })
        this.evidences.unshift(evidence)
        return evidence
      } finally {
        this.uploading = false
      }
    },
    async verifyEvidence(id: number | string) {
      await verifyEvidence(id)
      const index = this.evidences.findIndex((e) => e.id === id)
      if (index >= 0) {
        this.evidences[index] = { ...this.evidences[index], status: 'VERIFIED' }
      }
    }
  }
})
