import axios, { type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/lib/stores/auth'

export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PaginatedResponse<T = unknown> {
  code: number
  message: string
  data: {
    content: T[]
    totalElements: number
    totalPages: number
    size: number
    number: number
  }
  timestamp: number
}

export interface ApiError {
  code: number
  message: string
  details?: string
  fieldErrors?: Array<{
    field: string
    message: string
  }>
}

class ApiClient {
  private instance: AxiosInstance

  constructor() {
    this.instance = axios.create({
      baseURL: '/api',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  private setupInterceptors() {
    this.instance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const authStore = useAuthStore()
        
        if (authStore.token) {
          config.headers.Authorization = `Bearer ${authStore.token}`
        }

        config.headers['X-Request-ID'] = this.generateRequestId()
        config.headers['X-Timestamp'] = Date.now().toString()

        return config
      },
      (error) => {
        console.error('[API Request Error]:', error)
        return Promise.reject(error)
      }
    )

    this.instance.interceptors.response.use(
      (response) => {
        const { data } = response
        
        if (data && typeof data.code === 'number' && data.code !== 200 && data.code !== 0) {
          const error: ApiError = {
            code: data.code,
            message: data.message || '请求失败',
          }
          
          if (data.fieldErrors) {
            error.fieldErrors = data.fieldErrors
          }

          return Promise.reject(this.normalizeError(error))
        }

        return response
      },
      (error) => {
        const normalizedError = this.normalizeError(error)
        
        this.handleError(normalizedError)
        
        return Promise.reject(normalizedError)
      }
    )
  }

  private normalizeError(error: unknown): ApiError {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status
      const data = error.response?.data as ApiError | undefined

      switch (status) {
        case 401:
          return {
            code: 401,
            message: '登录已过期，请重新登录',
            details: 'Unauthorized',
          }
        case 403:
          return {
            code: 403,
            message: '没有权限执行此操作',
            details: 'Forbidden',
          }
        case 404:
          return {
            code: 404,
            message: data?.message || '请求的资源不存在',
            details: 'Not Found',
          }
        case 422:
          return {
            code: 422,
            message: data?.message || '数据验证失败',
            details: data?.details || 'Unprocessable Entity',
            fieldErrors: data?.fieldErrors,
          }
        case 500:
          return {
            code: 500,
            message: '服务器内部错误，请稍后重试',
            details: data?.details || 'Internal Server Error',
          }
        default:
          if (status && status >= 400 && status < 500) {
            return {
              code: status || 400,
              message: data?.message || `客户端错误 (${status})`,
            }
          } else if (!error.response) {
            return {
              code: 0,
              message: '网络连接失败，请检查网络设置',
              details: error.message || 'Network Error',
            }
          } else {
            return {
              code: status || 500,
              message: data?.message || error.message || '未知错误',
            }
          }
      }
    }

    if (error instanceof Error) {
      return {
        code: -1,
        message: error.message || '发生未知错误',
      }
    }

    return {
      code: -1,
      message: '发生未知错误',
    }
  }

  private handleError(error: ApiError) {
    console.error('[API Error]:', error)

    if (error.code === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      
      ElMessageBox.alert('登录已过期，请重新登录', '提示', {
        confirmButtonText: '去登录',
        type: 'warning',
        callback: () => {
          window.location.href = '/login'
        },
      })
      return
    }

    if (error.code === 0 || error.message.includes('网络')) {
      ElMessage.error('网络连接失败，请检查网络后重试')
      return
    }

    if (error.fieldErrors && error.fieldErrors.length > 0) {
      const messages = error.fieldErrors.map(e => `${e.field}: ${e.message}`).join('\n')
      ElMessage.error(`数据校验失败:\n${messages}`)
      return
    }
  }

  private generateRequestId(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
      const r = (Math.random() * 16) | 0
      const v = c === 'x' ? r : (r & 0x3) | 0x8
      return v.toString(16)
    })
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.instance.get<ApiResponse<T>>(url, config)
    return response.data
  }

  async post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.instance.post<ApiResponse<T>>(url, data, config)
    return response.data
  }

  async put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.instance.put<ApiResponse<T>>(url, data, config)
    return response.data
  }

  async patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.instance.patch<ApiResponse<T>>(url, data, config)
    return response.data
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.instance.delete<ApiResponse<T>>(url, config)
    return response.data
  }

  async upload<T>(
    url: string,
    file: File | Blob,
    onProgress?: (progressEvent: ProgressEvent) => void,
    additionalData?: Record<string, string>,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const formData = new FormData()
    formData.append('file', file)

    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, value)
      })
    }

    const response = await this.instance.post<ApiResponse<T>>(
      url,
      formData,
      {
        ...config,
        headers: {
          ...config?.headers,
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: onProgress,
        timeout: 120000,
      }
    )

    return response.data
  }

  download(url: string, filename?: string): Promise<void> {
    return this.instance.get(url, {
      responseType: 'blob',
    }).then((response) => {
      const blob = new Blob([response.data])
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = filename || 'download'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(downloadUrl)
    })
  }

  getInstance(): AxiosInstance {
    return this.instance
  }
}

export const apiClient = new ApiClient()
export default apiClient
