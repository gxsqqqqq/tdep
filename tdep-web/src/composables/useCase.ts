import { ref, computed } from 'vue'
import { useCaseStore } from '@/lib/stores/case'
import client from '@/lib/api/client'
import { API_ENDPOINTS } from '@/lib/api/endpoints'
import type { ApiResponse, PaginatedData } from '@/lib/api/types'
import type {
  Case,
  CaseListItem,
  CreateCaseRequest,
  UpdateCaseRequest,
} from '@/types/case'
import type { CaseDetailVO, CaseItem } from '@/lib/stores/case'
import type { PaginationParams } from '@/types/common'
import { CaseStatus } from '@/lib/constants/caseStatus'
import { useQuery } from '@/lib/hooks/useQuery'
import { useMutation } from '@/lib/hooks/useMutation'

export function useCase() {
  const caseStore = useCaseStore()
  const loading = ref(false)
  const error = ref<string | null>(null)

  const cases = computed(() => caseStore.caseList)
  const currentCase = computed(() => caseStore.currentCase)
  const caseStatus = computed(() => caseStore.caseStatus)
  const isEmpty = computed(() => caseStore.isEmpty)
  const total = computed(() => caseStore.total)

  function useCasesQuery(params?: Ref<PaginationParams & { status?: CaseStatus; keyword?: string } | undefined>) {
    return useQuery({
      queryKey: ['cases', params?.value],
      queryFn: () => caseStore.fetchCases(params?.value),
      errorMessage: '获取案件列表失败',
    })
  }

  function useCaseDetailQuery(caseId: Ref<string | number | null>) {
    return useQuery({
      queryKey: ['case', caseId],
      queryFn: () => (caseId.value ? caseStore.fetchCaseById(caseId.value) : Promise.reject(new Error('案件ID无效'))),
      enabled: !!caseId.value,
      errorMessage: '获取案件详情失败',
    })
  }

  function createCase() {
    return useMutation({
      mutationFn: (data: CreateCaseRequest) => caseStore.createCase(data),
      successMessage: '案件创建成功',
      errorMessage: '创建案件失败',
      invalidateQueries: [['cases']],
    })
  }

  function updateCase(id: string | number) {
    return useMutation({
      mutationFn: (data: UpdateCaseRequest) => caseStore.updateCase(id, data),
      successMessage: '案件更新成功',
      errorMessage: '更新案件失败',
      invalidateQueries: [['cases'], ['case', String(id)]],
    })
  }

  function deleteCase() {
    return useMutation({
      mutationFn: (id: string | number) => caseStore.deleteCase(id),
      successMessage: '案件删除成功',
      errorMessage: '删除案件失败',
      invalidateQueries: [['cases']],
    })
  }

  async function fetchCases(params?: PaginationParams & { status?: CaseStatus; keyword?: string }) {
    loading.value = true
    error.value = null
    try {
      return await caseStore.fetchCases(params)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取案件列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function fetchCaseById(id: string | number): Promise<CaseDetailVO> {
    loading.value = true
    error.value = null
    try {
      return await caseStore.fetchCaseById(id)
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取案件详情失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function uploadEvidence(
    caseId: string | number,
    file: File,
    onProgress?: (percent: number) => void
  ): Promise<unknown> {
    const formData = new FormData()
    formData.append('file', file)

    const response = await client.post<ApiResponse<unknown>>(
      API_ENDPOINTS.evidence.upload(caseId),
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (event) => {
          if (onProgress && event.total) {
            onProgress(Math.round((event.loaded * 100) / event.total))
          }
        },
      }
    )
    return response.data.data
  }

  function transitionStatus(caseId: string | number) {
    return useMutation({
      mutationFn: ({ action, data }: { action: string; data?: Record<string, unknown> }) =>
        caseStore.transitionStatus(caseId, action, data),
      successMessage: '状态更新成功',
      errorMessage: '状态转换失败',
      invalidateQueries: [['cases'], ['case', String(caseId)]],
    })
  }

  function assignJudge(caseId: string | number) {
    return useMutation({
      mutationFn: (judgeId: string) => caseStore.assignJudge(caseId, judgeId),
      successMessage: '法官分配成功',
      errorMessage: '分配法官失败',
      invalidateQueries: [['case', String(caseId)]],
    })
  }

  function setCurrentCase(c: CaseDetailVO | null): void {
    caseStore.setCurrentCase(c)
  }

  function clearCurrentCase(): void {
    caseStore.clearCurrentCase()
  }

  return {
    cases,
    currentCase,
    caseStatus,
    isEmpty,
    total,
    loading,
    error,
    useCasesQuery,
    useCaseDetailQuery,
    createCase,
    updateCase,
    deleteCase,
    fetchCases,
    fetchCaseById,
    uploadEvidence,
    transitionStatus,
    assignJudge,
    setCurrentCase,
    clearCurrentCase,
  }
}
