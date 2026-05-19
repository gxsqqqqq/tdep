export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  success: boolean
}

export interface PaginatedData<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface PaginatedResponse<T> extends ApiResponse<PaginatedData<T>> {}

export interface ErrorResult {
  code: number
  message: string
  errors?: Record<string, string[]>
}
