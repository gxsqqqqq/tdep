export interface ApiResult<T> {
  code: number
  message: string
  data: T
  traceId?: string
  timestamp?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType?: string
  expiresIn?: number
  user: {
    id: number
    username: string
    nickname?: string
    phone?: string
    email?: string
    status?: number
    roles?: string[]
    permissions?: string[]
  }
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}
