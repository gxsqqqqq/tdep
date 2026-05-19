import type { RouteRecordRaw } from 'vue-router'

export const evidenceRoutes: RouteRecordRaw[] = [
  {
    path: '/evidence',
    name: 'EvidenceList',
    component: () => import('@/pages/evidence/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '证据管理',
    },
  },
  {
    path: '/evidence/upload',
    name: 'UploadEvidence',
    component: () => import('@/pages/evidence/upload.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      permissions: ['evidence:create'],
      title: '上传证据',
    },
  },
  {
    path: '/evidence/:id',
    name: 'EvidenceDetail',
    component: () => import('@/pages/evidence/[id]/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '证据详情',
    },
  },
]
