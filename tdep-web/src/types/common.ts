import type { ApiResponse, PaginatedData } from '@/lib/api/types'

export interface PaginationParams {
  page?: number
  pageSize?: number
  sort?: string
  order?: 'asc' | 'desc'
}

export interface ApiListResponse<T> extends ApiResponse<PaginatedData<T>> {}

export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
}

export interface TreeNode {
  id: string
  label: string
  children?: TreeNode[]
}

export interface UploadFile {
  uid: string
  name: string
  size: number
  type: string
  url?: string
  status: 'uploading' | 'done' | 'error'
  percent?: number
}

export interface SearchResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}
