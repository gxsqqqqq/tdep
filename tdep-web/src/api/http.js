import axios from 'axios'
import { ElMessage } from 'element-plus'

import { useAuthStore } from '@/stores/auth'

// Axios 实例统一处理基础路径、令牌注入和异常提示。
const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.accessToken) {
    config.headers.Authorization = `Bearer ${authStore.accessToken}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || '请求处理失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default http
