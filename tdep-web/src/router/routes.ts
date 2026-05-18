import type { AppRouteRecord } from './types'

export const constantRoutes: AppRouteRecord[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/ForbiddenView.vue'),
    meta: { title: '无权限', hidden: true }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/NotFoundView.vue'),
    meta: { title: '页面不存在', hidden: true }
  }
]

export const asyncRoutes: AppRouteRecord[] = [
  // ── 当事人路由（流程化界面） ──
  {
    path: '/',
    name: 'Root',
    component: () => import('@/layout/UserLayout.vue'),
    redirect: '/my-cases',
    meta: { title: 'TDEP' },
    children: [
      {
        path: 'my-cases',
        name: 'MyCases',
        component: () => import('@/views/case/MyCasesView.vue'),
        meta: { title: '我的案件', icon: 'Briefcase', permissions: ['case:read:self', 'case:read:all'] }
      },
      {
        path: 'case/create',
        name: 'CaseCreate',
        component: () => import('@/views/case/CaseCreateView.vue'),
        meta: { title: '新建案件', hidden: true, permission: 'case:create' }
      },
      {
        path: 'case/:id/dashboard',
        name: 'CaseDashboard',
        component: () => import('@/views/case/CaseDashboard.vue'),
        meta: { title: '案件总览', hidden: true, permissions: ['case:read:self', 'case:read:all'] }
      },
      {
        path: 'case/:id',
        name: 'CaseProcess',
        component: () => import('@/views/case/CaseProcessView.vue'),
        meta: { title: '案件详情', hidden: true, permissions: ['case:read:self', 'case:read:all'] },
        redirect: ((to: { params: Record<string, string> }) => ({ name: 'CaseFiling', params: { id: to.params.id } })) as unknown as string,
        children: [
          {
            path: 'filing',
            name: 'CaseFiling',
            component: () => import('@/views/case/steps/FilingStep.vue'),
            meta: { title: '立案信息', hidden: true }
          },
          {
            path: 'payment',
            name: 'CasePayment',
            component: () => import('@/views/case/steps/PaymentStep.vue'),
            meta: { title: '缴费', hidden: true }
          },
          {
            path: 'evidence',
            name: 'CaseEvidence',
            component: () => import('@/views/case/steps/EvidenceStep.vue'),
            meta: { title: '举证', hidden: true }
          },
          {
            path: 'documents',
            name: 'CaseDocuments',
            component: () => import('@/views/case/steps/DocumentsStep.vue'),
            meta: { title: '文书', hidden: true }
          },
          {
            path: 'timeline',
            name: 'CaseTimeline',
            component: () => import('@/views/case/steps/TimelineStep.vue'),
            meta: { title: '进度', hidden: true }
          }
        ]
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/notify/NotificationsView.vue'),
        meta: { title: '消息通知', icon: 'Bell', permissions: ['notify:read'] }
      }
    ]
  },
  // ── 管理员路由（保留后台管理，仅 ADMIN 可见） ──
  {
    path: '/admin',
    name: 'AdminRoot',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { title: '管理后台', roles: ['ADMIN'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
        meta: { title: '工作台', icon: 'DataBoard', roles: ['ADMIN'] }
      },
      {
        path: 'cases',
        name: 'AdminCaseList',
        component: () => import('@/views/admin/CaseListView.vue'),
        meta: { title: '案件管理', icon: 'Briefcase', roles: ['ADMIN'] }
      },
      {
        path: 'evidence',
        name: 'AdminEvidenceList',
        component: () => import('@/views/admin/EvidenceListView.vue'),
        meta: { title: '证据管理', icon: 'Files', roles: ['ADMIN'] }
      },
      {
        path: 'documents',
        name: 'AdminDocumentList',
        component: () => import('@/views/admin/DocumentListView.vue'),
        meta: { title: '文书管理', icon: 'DocumentChecked', roles: ['ADMIN'] }
      },
      {
        path: 'templates',
        name: 'AdminTemplateList',
        component: () => import('@/views/admin/TemplateListView.vue'),
        meta: { title: '文书模板', icon: 'Tickets', roles: ['ADMIN'] }
      },
      {
        path: 'notify',
        name: 'AdminNotify',
        component: () => import('@/views/admin/NotifyView.vue'),
        meta: { title: '通知管理', icon: 'Bell', roles: ['ADMIN'] }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserListView.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['ADMIN'] }
      }
    ]
  }
]

export const fallbackRoute: AppRouteRecord = {
  path: '/:pathMatch(.*)*',
  redirect: '/404',
  meta: { title: '页面不存在', hidden: true }
}
