export const API_ENDPOINTS = {
  auth: {
    login: '/auth/login',
    register: '/auth/register',
    me: '/auth/me',
    logout: '/auth/logout',
    refresh: '/auth/refresh',
    profile: '/auth/profile',
  },
  cases: {
    list: '/cases',
    detail: (id: string | number) => `/cases/${id}`,
    create: '/cases',
    update: (id: string | number) => `/cases/${id}`,
    delete: (id: string | number) => `/cases/${id}`,
    transition: (id: string | number) => `/cases/${id}/transitions`,
    assignJudge: (id: string | number) => `/cases/${id}/judge`,
    timeline: (id: string | number) => `/cases/${id}/timeline`,
    workflow: (id: string | number) => `/cases/${id}/workflow`,
    myCases: '/cases/my',
  },
  evidence: {
    list: (caseId: string | number) => `/cases/${caseId}/evidences`,
    upload: (caseId: string | number) => `/cases/${caseId}/evidences`,
    detail: (caseId: string | number, evidenceId: string | number) =>
      `/cases/${caseId}/evidences/${evidenceId}`,
    delete: (caseId: string | number, evidenceId: string | number) =>
      `/cases/${caseId}/evidences/${evidenceId}`,
  },
  documents: {
    list: (caseId: string | number) => `/cases/${caseId}/documents`,
    preview: (documentId: string | number) => `/documents/${documentId}/preview`,
    sign: (documentId: string | number) => `/documents/${documentId}/sign`,
    download: (documentId: string | number) => `/documents/${documentId}/download`,
    templates: '/documents/templates',
  },
  trials: {
    list: '/trials',
    detail: (id: string | number) => `/trials/${id}`,
    schedule: (caseId: string | number) => `/cases/${caseId}/trials/schedule`,
    record: (trialId: string | number) => `/trials/${trialId}/record`,
  },
  users: {
    list: '/users',
    detail: (id: string | number) => `/users/${id}`,
    update: (id: string | number) => `/users/${id}`,
    delete: (id: string | number) => `/users/${id}`,
    profile: '/users/profile',
    changePassword: '/users/password',
  },
  notifications: {
    list: '/notifications',
    markRead: (id: string | number) => `/notifications/${id}/read`,
    markAllRead: '/notifications/read-all',
    count: '/notifications/count',
  },
  system: {
    config: '/system/config',
    stats: '/system/stats',
    logs: '/system/logs',
  },
  upload: {
    common: '/upload/common',
    image: '/upload/image',
    document: '/upload/document',
  },
} as const

export type ApiEndpointKey = keyof typeof API_ENDPOINTS
