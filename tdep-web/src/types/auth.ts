import type { ApiResponse } from '@/lib/api/types'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginCredentials {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
  phone?: string
  realName?: string
  idCard?: string
  role?: string
}

export interface LoginResponse {
  token: string
  refreshToken?: string
  expiresIn: number
  user: UserInfo
  permissions: string[]
}

export interface AuthResponse extends ApiResponse<LoginResponse> {}

export interface UserInfo {
  id: string
  username: string
  realName: string
  email: string
  phone: string
  avatar?: string
  role: string
  department?: string
  createdAt: string
  updatedAt: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface RefreshTokenResponse {
  token: string
  refreshToken: string
  expiresIn: number
}
