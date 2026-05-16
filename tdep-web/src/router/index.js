import { createRouter, createWebHistory } from 'vue-router'

import LayoutView from '@/views/LayoutView.vue'

// 路由骨架仅声明平台一级功能入口，不包含具体业务实现。
const routes = [
  {
    path: '/',
    component: LayoutView,
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'evidences',
        name: 'evidences',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: { title: '电子证据' }
      },
      {
        path: 'cases',
        name: 'cases',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: { title: '案件管理' }
      },
      {
        path: 'documents',
        name: 'documents',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: { title: '法律文书' }
      },
      {
        path: 'ai',
        name: 'ai',
        component: () => import('@/views/PlaceholderView.vue'),
        meta: { title: 'AI 法律助手' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  document.title = to.meta.title
    ? `${to.meta.title} - TDEP`
    : 'Trusted Digital Evidence Platform'
})

export default router
