import request from './request'
import type { LoginRequest, LoginResponse } from './types'

export function login(data: LoginRequest) {
  return request.post<unknown, LoginResponse>('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUserApi() {
  return request.get('/auth/me')
}
