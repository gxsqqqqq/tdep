import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
  type AxiosError,
} from 'axios'
import type { ApiResponse, ErrorResult } from './types'
import { ElMessage, ElMessageBox } from 'element-plus'
import { storage } from '@/lib/utils/storage'
import { useAuthStore } from '@/lib/stores/auth'

const REQUEST_TIMEOUT = 30 * 1000

function createClient(): AxiosInstance {
  const client: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: REQUEST_TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
    },
  })

  client.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = storage.getToken()
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error: AxiosError) => {
      return Promise.reject(error)
    }
  )

  client.interceptors.response.use(
    (response: AxiosResponse<ApiResponse<unknown>>) => {
      const data = response.data
      if (data && typeof data === 'object' && 'code' in data) {
        if (data.code !== 0 && data.code !== 200) {
          ElMessage.error(data.message || '请求失败')
          return Promise.reject(new Error(data.message || '请求失败'))
        }
      }
      return response
    },
    (error: AxiosError<ErrorResult>) => {
      const status = error.response?.status
      const errorData = error.response?.data

      switch (status) {
        case 401:
          handleUnauthorized()
          break
        case 403:
          handleForbidden(errorData)
          break
        case 404:
          ElMessage.warning('请求的资源不存在')
          break
        case 422:
          handleValidationError(errorData)
          break
        case 429:
          ElMessage.warning('请求过于频繁，请稍后再试')
          break
        case 500:
        case 502:
        case 503:
        case 504:
          ElMessage.error('服务器内部错误，请稍后重试')
          break
        default:
          if (!window.navigator.onLine) {
            ElMessage.error('网络连接已断开，请检查网络设置')
          } else {
            const message =
              errorData?.message || error.message || '未知错误'
            ElMessage.error(message)
          }
      }

      return Promise.reject(error)
    }
  )

  return client
}

function handleUnauthorized(): void {
  storage.clearAuth()

  const authStore = useAuthStore()
  authStore.token = null
  authStore.user = null
  authStore.permissions = []

  const currentPath = window.location.pathname
  if (currentPath !== '/login' && currentPath !== '/register') {
    window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
  }
}

function handleForbidden(errorData?: ErrorResult): void {
  const message = errorData?.message || '您没有权限执行此操作'
  ElMessage.error(message)
}

function handleValidationError(errorData?: ErrorResult): void {
  if (errorData?.errors) {
    const messages = Object.values(errorData.errors).flat()
    ElMessage.error(messages.join('; '))
  } else {
    ElMessage.error(errorData?.message || '数据验证失败')
  }
}

const clientInstance = createClient()

export default clientInstance

export function useApi(): AxiosInstance {
  return clientInstance
}

export { createClient, REQUEST_TIMEOUT }
