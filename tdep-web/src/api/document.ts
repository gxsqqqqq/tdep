import request, { upload } from './request'
import type { DocumentItem, DocumentTemplateItem, DownloadUrlResult } from './models'

export function generateDocument(data: Record<string, unknown>) {
  return request.post<unknown, DocumentItem>('/document/generate', data)
}

export function exportDocumentPdf(data: Record<string, unknown>) {
  return request.post<unknown, DocumentItem>('/document/export/pdf', data)
}

export function signDocument(data: Record<string, unknown>) {
  return request.post<unknown, DocumentItem>('/document/sign', data)
}

export function getTemplateList(params?: Record<string, unknown>) {
  return request.get<unknown, DocumentTemplateItem[]>('/template/list', { params })
}

export function uploadTemplate(file: File, data: Record<string, unknown>) {
  return upload<DocumentTemplateItem>('/template/upload', file, data)
}

export function previewDocumentPdf(id: number | string) {
  return request.get<unknown, DownloadUrlResult>(`/document/preview/${id}`)
}

export function downloadDocument(id: number | string, format = 'SIGNED_PDF') {
  return request.get<unknown, DownloadUrlResult>(`/document/download/${id}`, { params: { format } })
}
