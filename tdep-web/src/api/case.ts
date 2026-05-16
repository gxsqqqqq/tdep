import request from './request'
import type { CaseDetail, CaseItem } from './models'
import type { PageResult } from './types'

export function getCaseList(params?: Record<string, unknown>) {
  return request.get<unknown, PageResult<CaseItem>>('/cases', { params })
}

export function getCaseDetail(id: number | string) {
  return request.get<unknown, CaseDetail>(`/cases/${id}`)
}

export function createCase(data: Record<string, unknown>) {
  return request.post<unknown, CaseDetail>('/cases', data)
}

export function transitionCase(id: number | string, data: Record<string, unknown>) {
  return request.post<unknown, CaseDetail>(`/cases/${id}/transitions`, data)
}
