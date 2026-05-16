import request from './request'
import type { NotifyMessageItem } from './models'
import type { PageResult } from './types'

const emptyMessagePage: PageResult<NotifyMessageItem> = {
  records: [],
  total: 0,
  size: 0,
  current: 1
}

function isNotifyUnavailable(error: unknown) {
  return typeof error === 'object'
    && error !== null
    && 'response' in error
    && [404, 503].includes(Number((error as { response?: { status?: number } }).response?.status))
}

export function getNotifyMessages(params?: Record<string, unknown>) {
  return request
    .get<unknown, PageResult<NotifyMessageItem> | NotifyMessageItem[]>('/notify/messages', { params, silentError: true })
    .catch((error: unknown) => {
      if (isNotifyUnavailable(error)) return emptyMessagePage
      return Promise.reject(error)
    })
}

export function getUnreadCount() {
  return request.get<unknown, number>('/notify/messages/unread-count', { silentError: true }).catch((error: unknown) => {
    if (isNotifyUnavailable(error)) return 0
    return Promise.reject(error)
  })
}

export function markMessageRead(id: number | string) {
  return request.post(`/notify/messages/${id}/read`)
}

export function markAllRead() {
  return request.post('/notify/messages/read-all')
}
