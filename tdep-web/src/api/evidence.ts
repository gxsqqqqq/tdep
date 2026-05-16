import request, { upload } from './request'
import type { DownloadUrlResult, EvidenceItem } from './models'
import type { PageResult } from './types'

export function getEvidenceList(params?: Record<string, unknown>) {
  return request.get<unknown, PageResult<EvidenceItem>>('/evidences', { params })
}

export function getEvidenceDetail(id: number | string) {
  return request.get<unknown, EvidenceItem>(`/evidences/${id}`)
}

export function uploadEvidence(file: File, data: Record<string, unknown>) {
  return upload<EvidenceItem>('/evidences/upload', file, data)
}

export function verifyEvidence(id: number | string) {
  return request.post(`/evidences/${id}/verify`)
}

export function getEvidenceDownloadUrl(id: number | string) {
  return request.get<unknown, DownloadUrlResult>(`/evidences/${id}/download-url`)
}
