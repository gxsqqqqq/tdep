import type { RouteRecordRaw } from 'vue-router'

export const profileRoutes: RouteRecordRaw[] = [
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/pages/profile/index.vue'),
    meta: {
      layout: 'WorkspaceLayout',
      requiresAuth: true,
      title: '个人中心',
    },
    children: [
      {
        path: 'security',
        name: 'SecuritySettings',
        component: () => import('@/pages/profile/security.vue'),
        meta: { title: '账号安全' },
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/pages/profile/notifications.vue'),
        meta: { title: '消息通知' },
      },
    ],
  },
]
