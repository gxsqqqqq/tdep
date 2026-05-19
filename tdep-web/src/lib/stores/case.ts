import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import client from '@/lib/api/client'
import { API_ENDPOINTS } from '@/lib/api/endpoints'
import type { ApiResponse, PaginatedData } from '@/lib/api/types'
import type {
  Case,
  CaseListItem,
  CreateCaseRequest,
  UpdateCaseRequest,
} from '@/types/case'
import type { PaginationParams } from '@/types/common'
import { CaseStatus } from '@/lib/constants/caseStatus'

export interface CaseItem extends CaseListItem {}

export interface CaseDetailVO extends Case {}

export interface CaseState {
  currentCase: CaseDetailVO | null
  caseList: CaseItem[]
  caseStatus: CaseStatus
  loading: boolean
  total: number
  currentPage: number
  pageSize: number
}

export const useCaseStore = defineStore('case', () => {
  const currentCase = ref<CaseDetailVO | null>(null)
  const caseList = ref<CaseItem[]>([])
  const caseStatus = ref<CaseStatus>(CaseStatus.DRAFT)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const isEmpty = computed(() => !loading.value && caseList.value.length === 0)
  const hasNextPage = computed(
    () => currentPage.value * pageSize.value < total.value
  )
  const hasPrevPage = computed(() => currentPage.value > 1)

  async function fetchCases(params?: PaginationParams & { status?: CaseStatus; keyword?: string }): Promise<PaginatedData<CaseItem>> {
    loading.value = true
    try {
      const response = await client.get<ApiResponse<PaginatedData<CaseItem>>>(
        API_ENDPOINTS.cases.list,
        params as Record<string, unknown>
      )
      const data = response.data.data
      caseList.value = data.list
      total.value = data.total
      currentPage.value = data.page
      pageSize.value = data.pageSize
      return data
    } finally {
      loading.value = false
    }
  }

  async function fetchCaseById(id: string | number): Promise<CaseDetailVO> {
    loading.value = true
    try {
      const response = await client.get<ApiResponse<CaseDetailVO>>(
        API_ENDPOINTS.cases.detail(id)
      )
      currentCase.value = response.data.data
      caseStatus.value = response.data.data.status
      return response.data.data
    } finally {
      loading.value = false
    }
  }

  async function createCase(caseData: CreateCaseRequest): Promise<CaseDetailVO> {
    loading.value = true
    try {
      const response = await client.post<ApiResponse<CaseDetailVO>>(
        API_ENDPOINTS.cases.create,
        caseData
      )
      const newCase = response.data.data
      caseList.value.unshift(newCase as unknown as CaseItem)
      total.value += 1
      return newCase
    } finally {
      loading.value = false
    }
  }

  async function updateCase(
    id: string | number,
    data: UpdateCaseRequest
  ): Promise<CaseDetailVO> {
    loading.value = true
    try {
      const response = await client.put<ApiResponse<CaseDetailVO>>(
        API_ENDPOINTS.cases.update(id),
        data
      )
      const updatedCase = response.data.data

      const listIndex = caseList.value.findIndex((c) => c.id === String(id))
      if (listIndex !== -1) {
        caseList.value[listIndex] = updatedCase as unknown as CaseItem
      }
      if (currentCase.value?.id === String(id)) {
        currentCase.value = updatedCase
        caseStatus.value = updatedCase.status
      }
      return updatedCase
    } finally {
      loading.value = false
    }
  }

  async function deleteCase(id: string | number): Promise<void> {
    loading.value = true
    try {
      await client.delete(API_ENDPOINTS.cases.delete(id))
      caseList.value = caseList.value.filter((c) => c.id !== String(id))
      total.value = Math.max(0, total.value - 1)
      if (currentCase.value?.id === String(id)) {
        currentCase.value = null
      }
    } finally {
      loading.value = false
    }
  }

  async function transitionStatus(
    id: string | number,
    action: string,
    data?: Record<string, unknown>
  ): Promise<CaseDetailVO> {
    loading.value = true
    try {
      const response = await client.post<ApiResponse<CaseDetailVO>>(
        API_ENDPOINTS.cases.transition(id),
        { action, ...data }
      )
      const result = response.data.data

      const listIndex = caseList.value.findIndex((c) => c.id === String(id))
      if (listIndex !== -1) {
        caseList.value[listIndex] = result as unknown as CaseItem
      }
      if (currentCase.value?.id === String(id)) {
        currentCase.value = result
        caseStatus.value = result.status
      }
      return result
    } finally {
      loading.value = false
    }
  }

  async function assignJudge(
    id: string | number,
    judgeId: string
  ): Promise<CaseDetailVO> {
    loading.value = true
    try {
      const response = await client.post<ApiResponse<CaseDetailVO>>(
        API_ENDPOINTS.cases.assignJudge(id),
        { judgeId }
      )
      const result = response.data.data
      if (currentCase.value?.id === String(id)) {
        currentCase.value = result
      }
      return result
    } finally {
      loading.value = false
    }
  }

  function setCurrentCase(c: CaseDetailVO | null): void {
    currentCase.value = c
    if (c) {
      caseStatus.value = c.status
    }
  }

  function clearCurrentCase(): void {
    currentCase.value = null
    caseStatus.value = CaseStatus.DRAFT
  }

  function resetState(): void {
    currentCase.value = null
    caseList.value = []
    caseStatus.value = CaseStatus.DRAFT
    loading.value = false
    total.value = 0
    currentPage.value = 1
  }

  return {
    currentCase,
    caseList,
    caseStatus,
    loading,
    total,
    currentPage,
    pageSize,
    isEmpty,
    hasNextPage,
    hasPrevPage,
    fetchCases,
    fetchCaseById,
    createCase,
    updateCase,
    deleteCase,
    transitionStatus,
    assignJudge,
    setCurrentCase,
    clearCurrentCase,
    resetState,
  }
})
