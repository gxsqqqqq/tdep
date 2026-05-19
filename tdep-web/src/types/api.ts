import type { ApiResponse } from '@/lib/api/types'

export interface ApiError {
  code: number
  message: string
  details?: Record<string, string[]>
}

export interface ApiSuccess<T> {
  code: 0
  message: 'success'
  data: T
}

export interface ValidationError {
  field: string
  message: string
  value?: unknown
}

export interface PaginatedResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface FileUploadResult {
  url: string
  filename: string
  size: number
  mimetype: string
}

export interface ExportOptions {
  format: 'csv' | 'excel' | 'pdf'
  columns: string[]
  filters?: Record<string, unknown>
  dateRange?: {
    start: string
    end: string
  }
}
