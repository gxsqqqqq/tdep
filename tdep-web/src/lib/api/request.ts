import client from './client'
import type { ApiResponse, PaginatedResponse } from './types'

export async function request<T>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
  const response: AxiosResponse<ApiResponse<T>> = await client(config)
  return response.data
}

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  return request<T>({ method: 'GET', url, params })
}

export async function post<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  return request<T>({ method: 'POST', url, data })
}

export async function put<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  return request<T>({ method: 'PUT', url, data })
}

export async function del<T>(url: string): Promise<ApiResponse<T>> {
  return request<T>({ method: 'DELETE', url })
}

import type { AxiosRequestConfig, AxiosResponse } from 'axios'
