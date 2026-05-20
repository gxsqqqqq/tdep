import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/auth/login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      layout: 'auth',
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/auth/register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false,
      layout: 'auth',
    },
  },
  {
    path: '/',
    component: () => import('@/layouts/WorkspaceLayout.vue'),
    redirect: '/workspace',
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: '/workspace',
        name: 'Workspace',
        component: () => import('@/pages/workspace/index.vue'),
        meta: { title: '工作台' },
      },
      {
        path: '/workspace/party',
        name: 'PartyWorkspace',
        component: () => import('@/pages/workspace/party/index.vue'),
        meta: { 
          title: '当事人工作台',
          roles: ['PARTY', 'ADMIN'],
        },
      },
      {
        path: '/workspace/judge',
        name: 'JudgeWorkspace',
        component: () => import('@/pages/workspace/judge/index.vue'),
        meta: { 
          title: '法官工作台',
          roles: ['JUDGE', 'ADMIN'],
        },
      },
      {
        path: '/workspace/clerk',
        name: 'ClerkWorkspace',
        component: () => import('@/pages/workspace/clerk/index.vue'),
        meta: { 
          title: '书记员工作台',
          roles: ['CLERK', 'ADMIN'],
        },
      },
      {
        path: '/workspace/admin',
        name: 'AdminWorkspace',
        component: () => import('@/pages/workspace/admin/index.vue'),
        meta: { 
          title: '管理员后台',
          roles: ['ADMIN'],
        },
      },
      {
        path: '/cases',
        name: 'CaseList',
        component: () => import('@/pages/workspace/party/cases.vue'),
        meta: { title: '我的案件' },
      },
      {
        path: '/cases/create',
        name: 'CreateCase',
        component: () => import('@/pages/case/create.vue'),
        meta: { title: '新建案件' },
      },
      {
        path: '/cases/:id',
        name: 'CaseDetail',
        component: () => import('@/pages/case/[id]/index.vue'),
        meta: { title: '案件详情' },
      },
      {
        path: '/evidences',
        name: 'EvidenceList',
        component: () => import('@/pages/evidence/index.vue'),
        meta: { title: '证据管理' },
      },
      {
        path: '/documents',
        name: 'DocumentList',
        component: () => import('@/pages/document/index.vue'),
        meta: { title: '文书中心' },
      },
      {
        path: '/documents/:id',
        name: 'DocumentDetail',
        component: () => import('@/pages/document/[id]/index.vue'),
        meta: { title: '文书详情' },
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/pages/profile/index.vue'),
        meta: { title: '个人中心' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/components/EmptyState.vue'),
    meta: { title: '404' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
})

// 路由守卫：认证检查
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next({
      name: 'Login',
      query: { redirect: to.fullPath },
    })
  } else if ((to.name === 'Login' || to.name === 'Register') && token) {
    next({ name: 'Workspace' })
  } else {
    next()
  }
})

// 更新页面标题
router.afterEach((to) => {
  const title = to.meta.title as string
  document.title = title ? `${title} - TDEP 可信数字证据平台` : 'TDEP'
})

export default router
