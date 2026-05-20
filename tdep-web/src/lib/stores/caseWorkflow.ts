import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  CaseStatus,
  CaseTransition,
  CaseStateDefinition,
  TransitionRule,
  CASE_STATES,
  TRANSITION_RULES,
  getAvailableTransitions,
  canTransition,
} from '@/lib/constants/caseLifecycle'
import { casesApi, type CaseDetail, type TransitionRequest, type TransitionResponse, type TimelineEventDTO } from '@/lib/api/cases.api'

export interface CaseInfo {
  id: string
  caseNumber: string
  title: string
  status: CaseStatus
  plaintiff: string
  defendant: string
  judge?: string
  clerk?: string
  createdAt: string
  updatedAt: string
}

export interface TimelineEvent {
  id: string
  timestamp: string
  status: CaseStatus
  actor: string
  action: string
  description: string
  metadata?: Record<string, unknown>
}

function mapCaseDetailToCaseInfo(detail: CaseDetail): CaseInfo {
  return {
    id: detail.id,
    caseNumber: detail.caseNumber,
    title: detail.title,
    status: detail.status as CaseStatus,
    plaintiff: detail.plaintiff,
    defendant: detail.defendant,
    judge: detail.judge?.name,
    clerk: detail.clerk?.name,
    createdAt: detail.createdAt,
    updatedAt: detail.updatedAt,
  }
}

function mapTimelineEventDTO(dto: TimelineEventDTO): TimelineEvent {
  return {
    id: dto.id,
    timestamp: dto.timestamp,
    status: dto.status as CaseStatus,
    actor: dto.actor,
    action: dto.action,
    description: dto.description,
    metadata: dto.metadata,
  }
}

export const useCaseWorkflowStore = defineStore('caseWorkflow', () => {
  const currentCase = ref<CaseInfo | null>(null)
  const timelineEvents = ref<TimelineEvent[]>([])
  const isTransitioning = ref(false)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const currentStatus = computed<CaseStatus>(() => currentCase.value?.status || CaseStatus.DRAFT)

  const currentStateDefinition = computed<CaseStateDefinition>(() => {
    return CASE_STATES[currentStatus.value] || CASE_STATES[CaseStatus.DRAFT]
  })

  const availableActions = computed<TransitionRule[]>(() => {
    return getAvailableTransitions(currentStatus.value)
  })

  const statusTimeline = computed<CaseStateDefinition[]>(() => {
    return Object.values(CASE_STATES)
      .filter(state => state.order > 0)
      .sort((a, b) => a.order - b.order)
  })

  const currentStepIndex = computed<number>(() => {
    const timeline = statusTimeline.value
    return timeline.findIndex(state => state.status === currentStatus.value)
  })

  const progressPercentage = computed<number>(() => {
    const totalSteps = statusTimeline.value.length
    if (totalSteps <= 1) return 0
    return Math.round(((currentStepIndex.value + 1) / totalSteps) * 100)
  })

  const isCompleted = computed<boolean>(() => {
    return currentStateDefinition.value.isTerminal
  })

  async function fetchCaseDetail(caseId: string): Promise<void> {
    isLoading.value = true
    error.value = null

    try {
      const [caseDetail, timeline] = await Promise.all([
        casesApi.getCaseDetail(caseId),
        casesApi.getTimeline(caseId).catch(() => []),
      ])

      currentCase.value = mapCaseDetailToCaseInfo(caseDetail)
      timelineEvents.value = timeline.map(mapTimelineEventDTO)
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载案件信息失败'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function executeTransition(transition: CaseTransition, metadata?: Record<string, unknown>): Promise<TransitionResponse> {
    if (!currentCase.value) {
      throw new Error('未加载案件信息')
    }

    if (!canTransition(currentStatus.value, transition)) {
      error.value = `无法从 ${currentStatus.value} 执行 ${transition} 操作`
      throw new Error(error.value)
    }

    isTransitioning.value = true
    error.value = null

    try {
      const request: TransitionRequest = {
        transition: transition.toString(),
        reason: metadata?.reason as string | undefined,
        metadata,
      }

      const response = await casesApi.executeTransition(currentCase.value.id, request)

      await fetchCaseDetail(currentCase.value.id)

      return response
    } catch (err) {
      error.value = err instanceof Error ? err.message : '状态转换失败'
      throw err
    } finally {
      isTransitioning.value = false
    }
  }

  function getActionForTransition(transition: CaseTransition): TransitionRule | undefined {
    return availableActions.value.find(action => action.transition === transition)
  }

  function reset() {
    currentCase.value = null
    timelineEvents.value = []
    isTransitioning.value = false
    isLoading.value = false
    error.value = null
  }

  return {
    currentCase,
    timelineEvents,
    isTransitioning,
    isLoading,
    error,
    currentStatus,
    currentStateDefinition,
    availableActions,
    statusTimeline,
    currentStepIndex,
    progressPercentage,
    isCompleted,
    fetchCaseDetail,
    executeTransition,
    getActionForTransition,
    reset,
  }
})
