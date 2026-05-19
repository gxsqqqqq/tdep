import type { RouteRecordRaw } from 'vue-router'

export const authRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/auth/login.vue'),
    meta: {
      layout: 'AuthLayout',
      requiresAuth: false,
      title: '登录',
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/auth/register.vue'),
    meta: {
      layout: 'AuthLayout',
      requiresAuth: false,
      title: '注册',
    },
  },
]
