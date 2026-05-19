import type { RouteRecordRaw } from 'vue-router'

export const documentRoutes: RouteRecordRaw[] = [
  {
    path: '/document',
    name: 'DocumentList',
    component: () => import('@/pages/document/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '文档管理',
    },
  },
  {
    path: '/document/sign',
    name: 'SignDocument',
    component: () => import('@/pages/document/sign.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      permissions: ['document:sign'],
      title: '在线签字',
    },
  },
  {
    path: '/document/:id',
    name: 'DocumentDetail',
    component: () => import('@/pages/document/[id]/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '文档预览',
    },
  },
]
