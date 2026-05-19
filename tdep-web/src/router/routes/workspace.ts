import type { RouteRecordRaw } from 'vue-router'

export const workspaceRoutes: RouteRecordRaw[] = [
  {
    path: '/workspace',
    name: 'Workspace',
    component: () => import('@/pages/workspace/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '工作台',
    },
    redirect: '/workspace/party',
    children: [
      {
        path: 'party',
        name: 'PartyWorkspace',
        component: () => import('@/pages/workspace/party/index.vue'),
        meta: {
          roles: ['party'],
          title: '当事人工作台',
        },
      },
      {
        path: 'judge',
        name: 'JudgeWorkspace',
        component: () => import('@/pages/workspace/judge/index.vue'),
        meta: {
          roles: ['judge'],
          title: '法官工作台',
        },
      },
      {
        path: 'clerk',
        name: 'ClerkWorkspace',
        component: () => import('@/pages/workspace/clerk/index.vue'),
        meta: {
          roles: ['clerk'],
          title: '书记员工作台',
        },
      },
      {
        path: 'admin',
        name: 'AdminWorkspace',
        component: () => import('@/pages/workspace/admin/index.vue'),
        meta: {
          roles: ['admin'],
          layout: 'AdminLayout',
          title: '管理员工作台',
        },
      },
    ],
  },
]
