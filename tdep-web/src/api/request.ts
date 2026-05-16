import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

import router from '@/router'
import { useAuthStore } from '@/stores/auth'
import { TRACE_ID_HEADER } from '@/utils/constants'

import type { ApiResult } from './types'

declare module 'axios' {
  export interface AxiosRequestConfig {
    silentError?: boolean
  }
}

function createTraceId() {
  return crypto.randomUUID?.() || `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

const request = axios.create({
  baseURL: '/api',
  timeout: 20000
})

request.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.accessToken) {
    config.headers.Authorization = `Bearer ${authStore.accessToken}`
  }
  config.headers[TRACE_ID_HEADER] = createTraceId()
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult<unknown>
    if (typeof result?.code === 'number' && result.code !== 0) {
      ElMessage.error(result.message || '请求处理失败')
      return Promise.reject(result)
    }
    return result?.data ?? response.data
  },
  async (error: AxiosError<ApiResult<unknown>>) => {
    const status = error.response?.status
    const authStore = useAuthStore()

    if (status === 401) {
      authStore.clearAuth()
      router.replace('/login')
    } else if (status === 403) {
      router.replace('/403')
    }

    if (!error.config?.silentError) {
      ElMessage.error(error.response?.data?.message || error.message || '请求处理失败')
    }
    return Promise.reject(error)
  }
)

export function upload<T>(url: string, file: File, data: Record<string, unknown> = {}) {
  const formData = new FormData()
  formData.append('file', file)
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      formData.append(key, String(value))
    }
  })
  return request.post<unknown, T>(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export default request
