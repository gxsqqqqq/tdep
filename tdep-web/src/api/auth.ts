import request from './request'
import type { LoginRequest, LoginResponse } from './types'

export function login(data: LoginRequest) {
  return request.post<unknown, LoginResponse>('/auth/login', data)
}

export function register(data: RegisterRequest) {
  return request.post<unknown, RegisterResponse>('/auth/register', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUserApi() {
  return request.get('/auth/me')
}

export interface RegisterRequest {
  username: string
  password: string
  nickname?: string
  phone?: string
  email?: string
  roleCode?: string
}

export interface RegisterResponse {
  id: number
  username: string
  nickname: string
  roles: string[]
}
