import { ref } from 'vue'
import client from '@/lib/api/client'
import { API_ENDPOINTS } from '@/lib/api/endpoints'
import type { ApiResponse } from '@/lib/api/types'
import type { CaseStatus } from '@/lib/constants/caseStatus'

export interface WorkflowAction {
  name: string
  label: string
  description?: string
  requiredPermissions?: string[]
  requiredFields?: string[]
}

export interface WorkflowStep {
  id: string
  name: string
  label: string
  status: CaseStatus
  actions: WorkflowAction[]
  completed: boolean
  current: boolean
}

export interface WorkflowTransition {
  action: string
  fromStatus: CaseStatus
  toStatus: CaseStatus
  timestamp: string
  operatorName: string
  comment?: string
}

export interface WorkflowState {
  currentStep: WorkflowStep | null
  availableActions: WorkflowAction[]
  history: WorkflowTransition[]
  canAdvance: boolean
}

export function useWorkflow() {
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function getAvailableActions(caseId: string | number): Promise<WorkflowAction[]> {
    loading.value = true
    error.value = null
    try {
      const response = await client.get<ApiResponse<WorkflowAction[]>>(
        API_ENDPOINTS.cases.workflow(caseId)
      )
      return response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取可用动作列表失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function executeTransition(
    caseId: string | number,
    action: string,
    data?: Record<string, unknown>
  ): Promise<{ caseStatus: CaseStatus; transition: WorkflowTransition }> {
    loading.value = true
    error.value = null
    try {
      const response = await client.post<
        ApiResponse<{ caseStatus: CaseStatus; transition: WorkflowTransition }>
      >(API_ENDPOINTS.cases.transition(caseId), { action, ...data })
      return response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '执行状态转换失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function validateTransition(
    caseId: string | number,
    action: string,
    data?: Record<string, unknown>
  ): Promise<{
    valid: boolean
    errors?: Record<string, string[]>
    warnings?: string[]
  }> {
    try {
      const response = await client.post<
        ApiResponse<{
          valid: boolean
          errors?: Record<string, string[]>
          warnings?: string[]
        }>
      >(`${API_ENDPOINTS.cases.transition(caseId)}/validate`, {
        action,
        ...data,
      })
      return response.data.data
    } catch (e) {
      return {
        valid: false,
        errors: { general: ['验证请求失败'] },
      }
    }
  }

  function isTransitionAllowed(
    availableActions: WorkflowAction[],
    actionName: string
  ): boolean {
    return availableActions.some((a) => a.name === actionName)
  }

  function getNextStatus(
    availableActions: WorkflowAction[],
    actionName: string
  ): CaseStatus | null {
    const action = availableActions.find((a) => a.name === actionName)
    if (!action) return null
    return null
  }

  function requiresFields(
    availableActions: WorkflowAction[],
    actionName: string
  ): string[] | null {
    const action = availableActions.find((a) => a.name === actionName)
    return action?.requiredFields ?? null
  }

  async function getWorkflowHistory(caseId: string | number): Promise<WorkflowTransition[]> {
    loading.value = true
    error.value = null
    try {
      const response = await client.get<ApiResponse<WorkflowTransition[]>>(
        `${API_ENDPOINTS.cases.workflow(caseId)}/history`
      )
      return response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取工作流历史失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function getWorkflowSteps(caseId: string | number): Promise<WorkflowStep[]> {
    loading.value = true
    error.value = null
    try {
      const response = await client.get<ApiResponse<WorkflowStep[]>>(
        `${API_ENDPOINTS.cases.workflow(caseId)}/steps`
      )
      return response.data.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取工作流步骤失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    getAvailableActions,
    executeTransition,
    validateTransition,
    isTransitionAllowed,
    getNextStatus,
    requiresFields,
    getWorkflowHistory,
    getWorkflowSteps,
  }
}
