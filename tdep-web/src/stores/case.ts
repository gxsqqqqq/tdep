import { defineStore } from 'pinia'

import { createCase, getCaseDetail, getCaseList, transitionCase } from '@/api/case'
import type { CaseDetail, CaseItem } from '@/api/models'

const statusStepMap: Record<string, number> = {
  DRAFT: 0,
  SUBMITTED: 0,
  FILING_REVIEW: 0,
  FILING_REJECTED: 0,
  ACCEPTED: 1,
  PAID: 2,
  EVIDENCE_SUBMITTING: 2,
  SCHEDULED: 3,
  IN_TRIAL: 3,
  JUDGED: 4,
  EFFECTIVE: 4,
  CLOSED: 4
}

export const useCaseStore = defineStore('case', {
  state: () => ({
    myCases: [] as CaseItem[],
    currentCase: null as CaseDetail | null,
    loading: false,
    detailLoading: false
  }),
  getters: {
    currentStep: (state) => {
      if (!state.currentCase) return 0
      return statusStepMap[state.currentCase.status] ?? 0
    },
    activeCases: (state) => state.myCases.filter((c) => !['CLOSED', 'EFFECTIVE'].includes(c.status)),
    closedCases: (state) => state.myCases.filter((c) => ['CLOSED', 'EFFECTIVE'].includes(c.status))
  },
  actions: {
    async loadMyCases(params?: Record<string, unknown>) {
      this.loading = true
      try {
        const page = await getCaseList(params)
        this.myCases = page.records || []
        return page
      } finally {
        this.loading = false
      }
    },
    async loadCaseDetail(id: number | string) {
      this.detailLoading = true
      try {
        this.currentCase = await getCaseDetail(id)
        return this.currentCase
      } finally {
        this.detailLoading = false
      }
    },
    async createCase(data: Record<string, unknown>) {
      const caseDetail = await createCase(data)
      await transitionCase(caseDetail.id, { action: 'SUBMIT' })
      return caseDetail
    },
    async transitionCase(id: number | string, action: string, comment?: string) {
      const detail = await transitionCase(id, { action, comment: comment || '' })
      this.currentCase = detail
      return detail
    }
  }
})
