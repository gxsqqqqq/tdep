import apiClient from './client'

export interface CaseDetail {
  id: string
  caseNumber: string
  title: string
  status: CaseStatusEnum
  caseType: string
  plaintiff: string
  defendant: string
  plaintiffCounsel?: string
  defendantCounsel?: string
  judge: {
    id: string
    name: string
    title: string
  }
  clerk?: {
    id: string
    name: string
  }
  court: {
    id: string
    name: string
    level: string
  }
  createdAt: string
  updatedAt: string
  description?: string
  amount?: number
}

export enum CaseStatusEnum {
  DRAFT = 'DRAFT',
  PENDING_REVIEW = 'PENDING_REVIEW',
  ACCEPTED = 'ACCEPTED',
  EVIDENCE_CLOSING = 'EVIDENCE_CLOSING',
  TRIAL_SCHEDULED = 'TRIAL_SCHEDULED',
  IN_TRIAL = 'IN_TRIAL',
  JUDGED = 'JUDGED',
  CLOSED = 'CLOSED',
  REJECTED = 'REJECTED'
}

export interface CreateCaseRequest {
  caseNumber: string
  title: string
  caseType: string
  plaintiff: string
  defendant: string
  plaintiffCounsel?: string
  defendantCounsel?: string
  description?: string
  amount?: number
  courtId?: string
}

export interface TransitionRequest {
  transition: string
  reason?: string
  metadata?: Record<string, unknown>
}

export interface TransitionResponse {
  previousStatus: string
  newStatus: string
  transition: string
  executedAt: string
  executedBy: string
}

export interface TimelineEventDTO {
  id: string
  timestamp: string
  status: string
  actor: string
  actorRole: string
  action: string
  description: string
  metadata?: Record<string, unknown>
}

export const casesApi = {
  async getCaseDetail(caseId: string): Promise<CaseDetail> {
    const response = await apiClient.get<CaseDetail>(`/cases/${caseId}`)
    return response.data
  },

  async executeTransition(
    caseId: string,
    request: TransitionRequest
  ): Promise<TransitionResponse> {
    const response = await apiClient.post<TransitionResponse>(
      `/cases/${caseId}/transitions`,
      request
    )
    return response.data
  },

  async getTimeline(caseId: string): Promise<TimelineEventDTO[]> {
    const response = await apiClient.get<TimelineEventDTO[]>(
      `/cases/${caseId}/timeline`
    )
    return response.data
  },

  async getAvailableTransitions(caseId: string): Promise<string[]> {
    const response = await apiClient.get<string[]>(
      `/cases/${caseId}/transitions/available`
    )
    return response.data
  },

  async createCase(data: CreateCaseRequest): Promise<CaseDetail> {
    const response = await apiClient.post<CaseDetail>('/cases', data)
    return response.data
  },

  async updateCase(
    caseId: string,
    data: Partial<CaseDetail>
  ): Promise<CaseDetail> {
    const response = await apiClient.put<CaseDetail>(`/cases/${caseId}`, data)
    return response.data
  },
}
