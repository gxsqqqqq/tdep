import Layout from '@/layout/index.vue'

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
    component: () => import('@/views/system/ForbiddenView.vue'),
    meta: { title: '无权限', hidden: true }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/system/NotFoundView.vue'),
    meta: { title: '页面不存在', hidden: true }
  }
]

export const asyncRoutes: AppRouteRecord[] = [
  {
    path: '/',
    name: 'Root',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: 'TDEP' },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'DataBoard', affix: true }
      },
      {
        path: 'case/list',
        name: 'CaseList',
        component: () => import('@/views/case/ListView.vue'),
        meta: { title: '案件管理', icon: 'Briefcase', permissions: ['case:read:self', 'case:read:all'] }
      },
      {
        path: 'case/detail/:id',
        name: 'CaseDetail',
        component: () => import('@/views/case/DetailView.vue'),
        meta: { title: '案件详情', hidden: true, permissions: ['case:read:self', 'case:read:all'] }
      },
      {
        path: 'evidence/list',
        name: 'EvidenceList',
        component: () => import('@/views/evidence/ListView.vue'),
        meta: { title: '电子证据', icon: 'Files', permissions: ['evidence:read:self', 'evidence:read:case', 'evidence:read:all'] }
      },
      {
        path: 'evidence/upload',
        name: 'EvidenceUpload',
        component: () => import('@/views/evidence/UploadView.vue'),
        meta: { title: '证据上传', hidden: true, permission: 'evidence:upload' }
      },
      {
        path: 'document/list',
        name: 'DocumentList',
        component: () => import('@/views/document/ListView.vue'),
        meta: { title: '法律文书', icon: 'DocumentChecked', permissions: ['document:manage', 'document:generate', 'document:download'] }
      },
      {
        path: 'document/template',
        name: 'DocumentTemplate',
        component: () => import('@/views/document/TemplateView.vue'),
        meta: { title: '文书模板', icon: 'Tickets', permission: 'document:template:manage' }
      },
      {
        path: 'notify/message',
        name: 'NotifyMessage',
        component: () => import('@/views/notify/MessageView.vue'),
        meta: { title: '通知中心', icon: 'Bell', permissions: ['notify:read', 'notify:manage'] }
      },
      {
        path: 'notify/log',
        name: 'NotifyLog',
        component: () => import('@/views/notify/LogView.vue'),
        meta: { title: '通知日志', hidden: true, permission: 'notify:log:view' }
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/UserView.vue'),
        meta: { title: '系统管理', icon: 'Setting', roles: ['ADMIN'] }
      }
    ]
  }
]

export const fallbackRoute: AppRouteRecord = {
  path: '/:pathMatch(.*)*',
  redirect: '/404',
  meta: { title: '页面不存在', hidden: true }
}
