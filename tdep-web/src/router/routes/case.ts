import type { RouteRecordRaw } from 'vue-router'

export const caseRoutes: RouteRecordRaw[] = [
  {
    path: '/case/create',
    name: 'CreateCase',
    component: () => import('@/pages/case/create.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      permissions: ['case:create'],
      title: '新建案件',
    },
  },
  {
    path: '/case/:id',
    name: 'CaseDetail',
    component: () => import('@/pages/case/[id]/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      permissions: ['case:read'],
      title: '案件详情',
    },
    children: [
      {
        path: 'timeline',
        name: 'CaseTimeline',
        component: () => import('@/pages/case/[id]/timeline.vue'),
        meta: { title: '流程时间轴' },
      },
      {
        path: 'evidence',
        name: 'CaseEvidence',
        component: () => import('@/pages/case/[id]/evidence.vue'),
        meta: { title: '证据链' },
      },
      {
        path: 'trials',
        name: 'CaseTrials',
        component: () => import('@/pages/case/[id]/trials.vue'),
        meta: { title: '庭审记录' },
      },
      {
        path: 'documents',
        name: 'CaseDocuments',
        component: () => import('@/pages/case/[id]/documents.vue'),
        meta: { title: '文书中心' },
      },
      {
        path: 'workflow',
        name: 'CaseWorkflow',
        component: () => import('@/pages/case/[id]/workflow.vue'),
        meta: { title: '流程推进', permissions: ['case:update'] },
      },
      {
        path: 'ai-assistant',
        name: 'AIAssistant',
        component: () => import('@/pages/case/[id]/ai-assistant.vue'),
        meta: { title: 'AI 助手' },
      },
    ],
  },
]
